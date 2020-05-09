package com.mfg.connector.csv.reader;

import static com.mfg.utils.FinancialMath.compute_tick_size;
import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;
import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.QueueTick;
import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.common.UnparsedBar;
import com.mfg.dm.FillGapsMachine;
import com.mfg.dm.FilterOneTick;
import com.mfg.utils.FinancialMath;

public class CsvFileReader {

	private static Hashtable<String, DataSource1P> ourCsvCache = new Hashtable<>();

	private static void _add_a_single_csv_record(read_csv_automaton_data rcad,
			long duration) {

		// I have the previous record... so I transform it in a Candle
		Candle ipcr = _create_int_parsed_csv(rcad.previous_record,
				rcad.analysis.computed_scale, rcad.analysis.computed_tick_size,
				duration, rcad.analysis.is_time_bar);

		if (ipcr.o == -1 || ipcr.h == -1 || ipcr.c == -1 || ipcr.l == -1) {
			System.out.println("Invalid record @ " + new Date(ipcr.instant)
					+ " " + ipcr);
			return;
		}

		if (rcad.r_pars.output_only_close) {
			// I output only the close
			Tick tk = new Tick(ipcr.instant, ipcr.c);
			rcad.data_out.ticks.add(tk);
			return;
		}

		// Then, if the file is not a time bar file I put these
		// ticks inside the filter.
		if (!rcad.analysis.is_time_bar) {

			// Ok, I have the int parsed record, now I can deserialize it
			deserialize_bar(ipcr, rcad.ticks);

			for (Tick tk : rcad.ticks) {

				long previous_price = -1;
				if (rcad.data_out.ticks.size() != 0) {
					previous_price = rcad.data_out.ticks.get(
							rcad.data_out.ticks.size() - 1).getPrice();
				}

				// this is the equivalent of a filter zero tick
				if (tk.getPrice() != previous_price) {
					rcad.data_out.ticks.add(tk);
				}

			}
		} else {

			// The time bar are deserialized using the machine.
			dbm_get_a_new_candle(rcad.dbm, ipcr);
		}
	}

	protected static void _cad_final_got_tick(read_csv_automaton_data rcad,
			QueueTick qt) {
		rcad.data_out.ticks.add(qt);
	}

	private static Candle _create_int_parsed_csv(parsed_csv_record pcr,
			int scale, int tick_size, long next_instant_or_duration,
			boolean is_time_bar) {

		Candle ipcr = new Candle();

		if (is_time_bar) {
			ipcr.duration = next_instant_or_duration;
		} else {
			ipcr.duration = next_instant_or_duration - pcr.instant;
		}

		ipcr.instant = pcr.instant;

		if (ipcr.duration < 4) {
			// we have 4 prices. At least they are divided by 1 millisecond.
			ipcr.duration = 4;
		}

		ipcr.o = FinancialMath.bigDecimalToIntCheck(pcr.open, scale, tick_size);
		ipcr.l = FinancialMath.bigDecimalToIntCheck(pcr.low, scale, tick_size);
		ipcr.h = FinancialMath.bigDecimalToIntCheck(pcr.high, scale, tick_size);
		ipcr.c = FinancialMath
				.bigDecimalToIntCheck(pcr.close, scale, tick_size);

		return ipcr;
	}

	private static csv_automaton_data _create_read_csv_automaton(
			CsvAnalysis ca, DataSource1P a_ds, CsvReaderParams a_rp) {

		final csv_automaton_data res = new csv_automaton_data(new csv_record(),
				new parsed_csv_record());

		res.private_automaton_data = new read_csv_automaton_data();
		final read_csv_automaton_data rcad = (read_csv_automaton_data) res.private_automaton_data;
		rcad.common_data = res;
		rcad.analysis = ca;
		rcad.data_out = a_ds;
		rcad.r_pars = a_rp;

		if (ca.is_time_bar) {
			rcad.dbm = create_des_bar_mac(ca.computed_tick_size,
					ca.computed_scale, ca.interval, new void_f_QueueTick() {
						@Override
						public void f(QueueTick qt) {
							_cad_final_got_tick(rcad, qt);
						}
					});
		}

		// now the callback!!!!
		res.proc_rec_func = new process_csv_record() {
			@Override
			public void f(Object automaton, Object cur_rec_ob) {
				parsed_csv_record curRec = (parsed_csv_record) cur_rec_ob;
				_process_read_csv_record((read_csv_automaton_data) automaton,
						curRec);
			}
		};

		res.finalize_func = new finalise_csv_reading() {
			@Override
			public void f(Object automaton) {
				_finalize_reading_csv((read_csv_automaton_data) automaton);
			}
		};

		res.read_csv_func = new read_csv_record() {
			@Override
			public void f(CsvReader aReader, Object record) throws IOException {
				_read_csv_record(aReader, (csv_record) record);
			}
		};

		res.parse_csv_func = new parse_csv_record() {
			@Override
			public void f(Object a_csv_rec, Object a_parsed_rec,
					SimpleDateFormat sdf) throws java.text.ParseException {
				_parse_csv_record((csv_record) a_csv_rec,
						(parsed_csv_record) a_parsed_rec, sdf);
			}
		};

		return res;
	}

	private static void _dbm_last_final_pass(BarDeserializator dbm, Tick qt,
			boolean is_real) {

		// Just a final check...
		assert (!_final_zero_tick_filter(dbm, qt)) : "duplicate tick " + qt;

		_massage_tick_time_before_sending(dbm, qt);
		QueueTick resqt = new QueueTick(qt, dbm.fake_time++, is_real);
		dbm.cb.f(resqt);
	}

	private static SimpleDateFormat _determine_csv_date_format(String time) {

		SimpleDateFormat sdf;

		if (time.length() == 0) {
			sdf = new SimpleDateFormat("MM/dd/yy");
		} else {
			// now I should look for the seconds
			// first for the milliseconds
			Pattern pt = Pattern.compile("^\\d{1,2}:\\d\\d:\\d\\d\\.\\d{3}$");
			Matcher mt = pt.matcher(time);
			if (mt.matches()) {
				sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss.SSS");
				assert (false) : "We are not prepared for milliseconds info";
			} else {
				pt = Pattern.compile("^\\d{1,2}:\\d\\d:\\d\\d$");
				mt = pt.matcher(time);
				if (mt.matches()) {
					sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
				} else {
					sdf = new SimpleDateFormat("MM/dd/yy HH:mm");
				}
			}
		}

		return sdf;

	}

	private static boolean _final_zero_tick_filter(BarDeserializator dbm,
			Tick qt) {
		boolean res = false;
		if ((dbm.last_sent_qt != null)
				&& (qt.getPrice() == dbm.last_sent_qt.getPrice())) {
			res = true;
		}
		if (!res) {
			dbm.last_sent_qt = qt;
		}
		return res;
	}

	protected static void _finalize_analyse_csv_file(
			analyse_csv_automaton_data acad) {
		acad.analysis_out.computed_tick_size = compute_tick_size(acad.ranges,
				acad.analysis_out.computed_scale);
	}

	protected static void _finalize_reading_csv(read_csv_automaton_data rcad) {

		// I should finalize the last record.
		if (rcad.previous_record != null) {
			long duration = rcad.overall_duration / rcad.n_recs;
			_add_a_single_csv_record(rcad, duration);

			rcad.n_recs++;
			rcad.overall_duration += duration;
		}

		/* Ok, now I should finalize the physical time or the fake time */
		_finalize_tick_time(rcad);

	}

	private static void _finalize_tick_time(read_csv_automaton_data rcad) {
		_finalize_tick_time_physical(rcad);
	}

	private static void _finalize_tick_time_physical(
			read_csv_automaton_data rcad) {

		long physicalPresent = -1;
		for (Object obt : rcad.data_out.ticks) {
			Tick tk = (Tick) obt;
			long curTime = tk.getPhysicalTime();
			if (physicalPresent < 0) {
				physicalPresent = curTime;
				continue;
			}

			if (tk.getPhysicalTime() <= physicalPresent) {

				tk.setPhysicalTime(++physicalPresent);
				// debug_var(333525, "Backward time @ " + new Date(curTime),
				// " now is ", physicalPresent, " deltat ",
				// (physicalPresent - curTime), " now is ",
				// tk.getPhysicalTime());
			}

			physicalPresent = tk.getPhysicalTime();
		}
	}

	private static int _get_max_record_scale(parsed_csv_record aRec) {
		int scaleMax = aRec.open.scale();
		scaleMax = Math.max(aRec.high.scale(), scaleMax);
		scaleMax = Math.max(aRec.low.scale(), scaleMax);
		scaleMax = Math.max(aRec.close.scale(), scaleMax);
		return scaleMax;
	}

	private static BigDecimal _get_record_amplitude(parsed_csv_record aRec) {
		assert (aRec.high.compareTo(aRec.low) >= 0) : "high < low in file";
		return aRec.high.subtract(aRec.low);
	}

	private static int _iterate_over_csv_file(InputStream aStream,
			csv_automaton_data automaton) {
		// Ok, let's open the file
		try {
			CsvReader reader = new CsvReader(aStream,
					Charset.forName("ISO-8859-1"));
			reader.readHeaders();

			// csv_record rec = new csv_record();
			// parsed_csv_record parsedRec = new parsed_csv_record();
			SimpleDateFormat sdf = null;

			while (reader.readRecord()) {

				// the record is inside the automaton.
				automaton.read_csv_func.f(reader, automaton.cur_record);
				// _read_csv_record(reader, rec);

				if (sdf == null) {
					sdf = _determine_csv_date_format(((has_time_part) automaton.cur_record)
							.get_tp().t);
				}

				automaton.rec_num++;

				automaton.parse_csv_func.f(automaton.cur_record,
						automaton.parsed_record, sdf);

				automaton.proc_rec_func.f(automaton.private_automaton_data,
						automaton.parsed_record);
			}

			reader.close();

			automaton.finalize_func.f(automaton.private_automaton_data);

		} catch (Exception e) {
			System.out.println("Cannot read csv file "
					+ aStream.getClass().getName() + " at record "
					+ automaton.rec_num);
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	private static void _massage_tick_time_before_sending(
			BarDeserializator dbm, Tick tk) {
		// Last check, I have to change the physical time
		if (tk.getPhysicalTime() <= dbm.last_physical_time) {
			tk.setPhysicalTime(++dbm.last_physical_time);
		} else {
			dbm.last_physical_time = tk.getPhysicalTime();
		}

	}

	protected static void _parse_csv_record(csv_record rec_in,
			parsed_csv_record rec_out, SimpleDateFormat sdf)
			throws ParseException {

		rec_out.instant = _parse_date_time_part(rec_in.date_time, sdf);

		// Ok, now the numbers.
		rec_out.open = new BigDecimal(rec_in.open);
		rec_out.high = new BigDecimal(rec_in.high);
		rec_out.low = new BigDecimal(rec_in.low);
		rec_out.close = new BigDecimal(rec_in.close);
	}

	private static long _parse_date_time_part(csv_time_part ctp,
			SimpleDateFormat sdf) throws ParseException {
		String date_time = ctp.d + " " + ctp.t;
		Date dt = sdf.parse(date_time);
		return dt.getTime();
	}

	protected static void _process_read_csv_record(
			read_csv_automaton_data rcad, parsed_csv_record pcr) {
		// To read a record, first of all I transform it in integer
		// data.
		if (rcad.previous_record != null) {
			long duration = rcad.analysis.is_time_bar ? rcad.analysis.interval
					: pcr.instant;
			_add_a_single_csv_record(rcad, duration);

			rcad.n_recs++;
			rcad.overall_duration += duration;
		}

		rcad.previous_record = pcr.clone();
	}

	protected static void _read_csv_record(CsvReader aReader, csv_record aRecord)
			throws IOException {
		_read_date_time_part(aReader, aRecord.date_time);
		aRecord.open = aReader.get("Open");
		aRecord.high = aReader.get("High");
		aRecord.low = aReader.get("Low");
		aRecord.close = aReader.get("Close");

	}

	private static void _read_date_time_part(CsvReader a_reader,
			csv_time_part ctp) throws IOException {
		ctp.d = a_reader.get("Date");
		ctp.t = a_reader.get("Time");
	}

	/**
	 * Analyzes a csv data source (can be also from a network) and returns zero
	 * if success,
	 * 
	 * @param is
	 *            [in] the input stream
	 * @param ca
	 *            [out] the analysis will be stored in this object.
	 * 
	 * @return 0 on success.
	 */
	public static int analyse_csv_file(InputStream is, CsvAnalysis ca) {
		csv_automaton_data cad = create_analyze_csv_automaton(ca);
		int res = _iterate_over_csv_file(is, cad);
		ca.numRecords = cad.rec_num;
		return res;
	}

	private static csv_automaton_data create_analyze_csv_automaton(
			CsvAnalysis ca) {
		csv_automaton_data res = new csv_automaton_data(new csv_record(),
				new parsed_csv_record());

		res.private_automaton_data = new analyse_csv_automaton_data();
		((analyse_csv_automaton_data) res.private_automaton_data).common_data = res; // double
																						// linked
		((analyse_csv_automaton_data) res.private_automaton_data).analysis_out = ca;

		// now the callbacks!!!!
		res.proc_rec_func = new process_csv_record() {
			@Override
			public void f(Object automaton, Object cur_rec_ob) {
				parsed_csv_record curRec = (parsed_csv_record) cur_rec_ob;
				process_record((analyse_csv_automaton_data) automaton, curRec);
			}
		};

		res.finalize_func = new finalise_csv_reading() {
			@Override
			public void f(Object automaton) {
				_finalize_analyse_csv_file((analyse_csv_automaton_data) automaton);
			}
		};

		res.read_csv_func = new read_csv_record() {
			@Override
			public void f(CsvReader aReader, Object record) throws IOException {
				_read_csv_record(aReader, (csv_record) record);
			}
		};

		res.parse_csv_func = new parse_csv_record() {
			@Override
			public void f(Object a_csv_rec, Object a_parsed_rec,
					SimpleDateFormat sdf) throws java.text.ParseException {
				_parse_csv_record((csv_record) a_csv_rec,
						(parsed_csv_record) a_parsed_rec, sdf);
			}
		};

		return res;

	}

	private static BarDeserializator create_des_bar_mac(int tick, int scale,
			long bar_dur, void_f_QueueTick cb) {

		BarDeserializator dbm = new BarDeserializator();

		dbm.tick = tick;
		dbm.scale = scale;
		dbm.bar_dur = bar_dur;

		dbm.fotd = new FilterOneTick(tick);
		dbm.fga = new FillGapsMachine(tick, 22, false);

		dbm.cb = cb;

		return dbm;

	}

	private static void dbm_final_pass_tick(BarDeserializator dbm,
			Tick final_tick, boolean is_real) {
		// QueueTick qt = new QueueTick(final_tick, -1, is_real);
		_dbm_last_final_pass(dbm, final_tick, is_real);

	}

	private static int dbm_get_a_new_candle(BarDeserializator dbm, Candle cd) {

		// Ok, I get a new bar

		int old_fake_time = dbm.fake_time;

		if (cd.instant == -1) {
			// debug_var(475912, "Got final candle...", cd);
			return 0;
		}

		// debug_var(871083, "Got a new candle! ", cd);

		// if (dbm.prev_bar != null){

		long dur_4 = dbm.bar_dur / 4;

		if (dbm.bar_dur == -1) {
			// range bar, usually 1p bar, only one price
			dbm_get_raw_tick(dbm, new Tick(cd.instant, cd.o));
		} else {

			long point_time = cd.instant;

			if (cd.o < cd.c) {
				// high bar, first low then high
				dbm_get_raw_tick(dbm, new Tick(point_time, cd.l));
				point_time += dur_4;
				dbm_get_raw_tick(dbm, new Tick(point_time, cd.h));
				point_time += dur_4;
			} else {
				dbm_get_raw_tick(dbm, new Tick(point_time, cd.h));
				point_time += dur_4;
				dbm_get_raw_tick(dbm, new Tick(point_time, cd.l));
				point_time += dur_4;
			}

		}

		// dbm.prev_bar = cd;
		return dbm.fake_time - old_fake_time; // this is the number of
		// prices serialized
	}

	private static void dbm_get_raw_tick(BarDeserializator dbm, Tick tkraw) {
		if (dbm.fotd != null) {
			ArrayList<Tick> ticksOut = dbm.fotd.filter(tkraw);

			// debug_var(57228, "I have " ,ticksOut.size(), " ticks filtered");

			for (Tick tk_out : ticksOut) {
				dbm_tick_after_fot(dbm, tk_out);
			}

		} else {
			dbm_tick_after_fot(dbm, tkraw);
		}

	}

	private static void dbm_tick_after_fot(BarDeserializator dbm, Tick tk_a_fot) {

		if (dbm.fga == null) {
			dbm_final_pass_tick(dbm, tk_a_fot, true);
		} else {
			// dbm.fga.accept(tk_a_fot);
			int[] res = dbm.fga.acceptPrice(tk_a_fot.getPrice(),
					tk_a_fot.getPhysicalTime());
			// fga_receive_tick(dbm.fga, tk_a_fot);

			if (res != null) {
				long builtTimes[] = dbm.fga.getBuiltTimes();

				int numTicks = -1;
				while (res[++numTicks] != FillGapsMachine.END_OF_PRICE) {
					// nothing
				}

				for (int ii = 0; ii < numTicks; ++ii) {
					RealTick afterFgmTick = new RealTick(builtTimes[ii],
							res[ii], ii == numTicks - 1 ? true : false);
					dbm_final_pass_tick(dbm, afterFgmTick,
							afterFgmTick.getReal());
				}

			} else {
				dbm_final_pass_tick(dbm, tk_a_fot, true);
			}

		}
	}

	private static void deserialize_bar(Candle aRec, Tick[] ticks) {
		int idx = -1;

		ticks[++idx] = new Tick(aRec.instant, aRec.o);

		// Ok, now the instant (real time simulated) of the prices.
		long dur_div_4 = aRec.duration / 4;
		long cur_instant = aRec.instant + dur_div_4;

		if (aRec.c >= aRec.o) {
			// high bar, it comes first low then high
			ticks[++idx] = new Tick(cur_instant, aRec.l);
			cur_instant += dur_div_4;

			ticks[++idx] = new Tick(cur_instant, aRec.h);
			cur_instant += dur_div_4;

		} else {
			// low bar, it comes first high then low

			ticks[++idx] = new Tick(cur_instant, aRec.h);
			cur_instant += dur_div_4;

			ticks[++idx] = new Tick(cur_instant, aRec.l);
			cur_instant += dur_div_4;

		}

		// then the close;
		ticks[++idx] = new Tick(cur_instant, aRec.c);
	}

	protected static void process_record(analyse_csv_automaton_data acad,
			parsed_csv_record curRec) {
		BigDecimal gap = null;
		if (acad.previous_record != null) {

			// determine the minimum gap
			gap = curRec.open.subtract(acad.previous_record.close).abs();
			if (gap.compareTo(BigDecimal.ZERO) != 0) {
				if (acad.analysis_out.minimumGap == null
						|| (gap.compareTo(acad.analysis_out.minimumGap) < 0)) {
					acad.analysis_out.minimumGap = gap;
				}
			}
		}

		BigDecimal amplitude = _get_record_amplitude(curRec);
		if (!acad.decided_time_or_range) {

			/*
			 * To decide if this is a time or range file I simply look at the
			 * range of the bars. If it is always equal then this should be a
			 * range file.
			 */
			// System.out.println("amplitude record " + amplitude);

			if (amplitude.compareTo(BigDecimal.ZERO) != 0) {
				if (acad.analysis_out.current_range == null) {
					acad.analysis_out.current_range = amplitude;
				} else if (acad.analysis_out.current_range.compareTo(amplitude) != 0) {
					System.out
							.println("Different ranges I decide for time bar cur: "
									+ amplitude
									+ " past "
									+ acad.analysis_out.current_range);
					acad.decided_time_or_range = true;
					acad.analysis_out.is_time_bar = true;
				}
			}

			/* Now the interval. */
			if (acad.previous_record != null) {
				long interval = curRec.instant - acad.previous_record.instant;

				// System.out.println("interval record " + interval +
				// " cur instant " + curRec.instant +
				// " past " + acad.previous_record.instant);

				if (acad.analysis_out.interval < 0) {
					acad.analysis_out.interval = interval;
				} else if (interval != acad.analysis_out.interval) {
					acad.time_bar_score--;
					if (acad.time_bar_score < -100) {
						debug_var(738515,
								"score < -100, I decide for range bar.");
						acad.decided_time_or_range = true;
						acad.analysis_out.is_time_bar = false;
					}
				} else {
					// the intervals are the same
					acad.time_bar_score++;
					if (acad.time_bar_score > 100) {
						debug_var(819351, "score > 100, it is a time bar file");
						acad.decided_time_or_range = true;
						acad.analysis_out.is_time_bar = true;
					}
				}
			}

		} else {
			if (acad.analysis_out.is_time_bar) {
				// I have decided for time bar, so I put the current amplitude
				// in the array

				if (amplitude.compareTo(BigDecimal.ZERO) == 0) {
					// it is possibile for a time bar to have zero
					// range! yes... if there are no trades!!!
				} else {
					BigDecimal bd;
					for (int i = 0; i < analyse_csv_automaton_data.RANGES; ++i) {
						bd = acad.ranges[i];
						if (bd != null) {
							if (bd.compareTo(amplitude) == 0) {
								// there is already this range break
								break;
							}
						} else {
							// I have arrived here... the range is not present,
							// I store it.
							System.out.println("Stored the range " + amplitude
									+ " @ " + new Date(curRec.instant));
							acad.ranges[i] = amplitude;
							break;
						}
					}
				}
			} else {
				// I have decided for the range bars, so I have to store the
				// gaps
				if (gap != null) {

					if (gap.compareTo(BigDecimal.ZERO) == 0) {
						System.out
								.println("Detected zero lenght gap at record "
										+ acad.rec_num + " rec " + curRec);
					} else {

						BigDecimal bd;
						for (int i = 0; i < analyse_csv_automaton_data.RANGES; ++i) {
							bd = acad.ranges[i];
							if (bd != null) {
								if (bd.compareTo(gap) == 0) {
									// there is already this range break
									break;
								}
							} else {
								// I have arrived here... the range is not
								// present, I store it.
								System.out.println("Stored the gap " + gap
										+ " @ " + new Date(curRec.instant));
								acad.ranges[i] = gap;
								break;
							}
						}
					}
				}
			}
		}

		acad.analysis_out.computed_scale = Math
				.max(acad.analysis_out.computed_scale,
						_get_max_record_scale(curRec));
		acad.previous_record = curRec.clone();

	}

	public static interface IBarProcessor {

		/**
		 * gets the new bar from the stream of bars.
		 * 
		 * @param aRecNumber
		 *            the increment number of this bar. First bar has index
		 *            zero.
		 * @param aBar
		 *            the parsed bar.
		 * @throws DFSException
		 */
		public void onNewBar(int aRecNumber, Bar aBar) throws DFSException;

		/**
		 * Called when the stream has ended.
		 */
		public void onEnd();
	}

	/**
	 * imports the bars. The file should have been already
	 * 
	 * 
	 * @param aStream
	 *            the stream from which we import the bars, it must be a text
	 *            stream with csv data. The input stream must be already opened
	 *            and ready to the start.
	 * @param aProcessor
	 *            the callback which will import the data.
	 * @param ca
	 *            the analysis of the stram, it will tell me what are the
	 *            parameters.
	 * @return
	 * @throws IOException
	 * @throws DFSException
	 */
	public static boolean importCsvBars(InputStream aStream,
			IBarProcessor aProcessor, CsvAnalysis ca) throws IOException,
			DFSException, ParseException {

		/*
		 * I use directly the CsvReader object, in this way I can avoid the
		 * building of the automaton object.
		 */

		CsvReader reader = new CsvReader(aStream, Charset.forName("ISO-8859-1"));
		reader.readHeaders();

		int hasVolume = reader.getIndex("Volume");

		// csv_record rec = new csv_record();
		// parsed_csv_record parsedRec = new parsed_csv_record();
		SimpleDateFormat sdf = null;

		int numRecord = 0;

		csv_record record = new csv_record();
		// parsed_csv_record aParsedRecord = new parsed_csv_record();

		while (reader.readRecord()) {

			_read_csv_record(reader, record);
			// automaton.read_csv_func.f(reader, automaton.cur_record);
			// _read_csv_record(reader, rec);

			if (sdf == null) {
				sdf = _determine_csv_date_format(((has_time_part) record)
						.get_tp().t);
			}

			// automaton.parse_csv_func.f(automaton.cur_record,
			// automaton.parsed_record, sdf);

			long instant = _parse_date_time_part(record.date_time, sdf);

			int vol = Integer.parseInt(hasVolume > 0 ? reader.get(hasVolume)
					: "0");
			UnparsedBar ub = new UnparsedBar(instant, record.open, record.high,
					record.low, record.close, vol);

			/*
			 * Then I convert to a bar
			 */
			Bar bar = new Bar(ub, ca.computed_scale);

			aProcessor.onNewBar(numRecord++, bar);
		}
		reader.close();
		aProcessor.onEnd();
		return true;
	}

	/**
	 * Read a csv file and puts the resulting stream of data in the ds
	 * structure.
	 * 
	 * @param params
	 *            some parameters that will change the reader way to read the
	 *            file.
	 * @param ds
	 *            the dataset to fill
	 * 
	 * @return the data set, otherwise null in case of error.
	 * 
	 */
	@SuppressWarnings("boxing")
	public static DataSource1P read_csv_file(CsvReaderParams params,
			DataSource1P ds, boolean force) {

		// is it the csv file in cache? Already computed?
		if (ourCsvCache.containsKey(params.csv_file_name)) {
			debug_var(391952, "The csv file ", params.csv_file_name,
					" is already in cache, returning");
			return ourCsvCache.get(params.csv_file_name);
		}
		debug_var(991915, "the file ", params.csv_file_name, " is not any more"
				+ "present in cache... whose size is ",
				Integer.valueOf(ourCsvCache.size()));

		// the steps are:

		// 1. analyse the csv file

		// 2. read the file and deserialize the bars and filter the
		// ticks accordingly

		// let's analyze the file.

		CsvAnalysis ca = new CsvAnalysis();
		try (FileInputStream fis = new FileInputStream(new File(
				params.csv_file_name));) {
			if (analyse_csv_file(fis, ca) < 0) {
				return null;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

		// Ok, now I have analysed... let's check coherence with the
		// tick size supplied by the user... (if she has supplied one).
		if (params.forced_tick) {
			if (!force) {
				// Only in this case I check the coherence
				if (params.tick_size != ca.computed_tick_size) {
					System.out.println("incoherent tick size you say "
							+ params.tick_size + " I computed "
							+ ca.computed_tick_size);
					return null;
				}

				if (params.prices_scale != ca.computed_scale) {
					System.out.println("incoherent scale you claim "
							+ params.prices_scale + " I computed "
							+ ca.computed_scale);
					return null;
				}
			}
		}

		ds.dsc.scale_from_the_source = ca.computed_scale;
		ds.dsc.tick = ca.computed_tick_size;

		// Ok, I can now read the file...
		csv_automaton_data cad = _create_read_csv_automaton(ca, ds, params);

		debug_var(382993, " analysis of the csv file");
		System.out.println(ca);

		debug_var(839892, "*********** structure of the csv file ");

		try (FileInputStream fis = new FileInputStream(new File(
				params.csv_file_name));) {

			if (_iterate_over_csv_file(fis, cad) >= 0) {
				debug_var(392922, "I put the ", params.csv_file_name,
						" file into cache, the cache size is ",
						ourCsvCache.size());
				ourCsvCache.put(params.csv_file_name, ds);
				return ds;
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}
