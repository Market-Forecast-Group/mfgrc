package com.mfg.connector.csv;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.join_thread;
import static com.mfg.utils.Utils.notifyObject;
import static com.mfg.utils.Utils.waitForEverOn;

import java.util.ArrayList;
import java.util.UUID;

import com.mfg.common.DFSException;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.DataProvider;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.U;

public class CsvDataProvider extends DataProvider {

	// package protected... this is the lock for all the data sources
	Object lock = new Object();

	protected Thread dpThread;

	// /This is the list of all the composite data sources
	ArrayList<CompositeDataSource> _myCds = new ArrayList<>();

	@Override
	public synchronized CompositeDataSource createTickDataSource(
			TickDataRequest req, UUID aId) {
		CompositeDataSource cds = new CsvCompositeDataSource(req, this, aId);
		synchronized (this._myCds) {
			this._myCds.add(cds);
		}
		return cds;
	}

	@Override
	public String getName() {
		return "CSV";
	}

	@Override
	public void switchOff() throws DFSException {
		if (this._myCds.size() == 0 && this.dpThread == null) {
			debug_var(828283, "The data provider is already switched off");
			return;
		}

		debug_var(349255, "waiting for the thread csv to finish");

		boolean force = true;

		if (force) {
			for (CompositeDataSource cds : this._myCds) {
				cds.stop();
			}
		} else {
			// the kick the can is already completed...
			debug_var(325793,
					"removing only the data stopped data sources, size is  ",
					Integer.valueOf(this._myCds.size()));

			synchronized (lock) {
				ArrayList<CompositeDataSource> deletedCds = new ArrayList<>();
				for (CompositeDataSource cds : this._myCds) {
					CsvCompositeDataSource csvds = (CsvCompositeDataSource) cds;
					if (csvds.isStopped()) {
						deletedCds.add(cds);
					}
				}

				for (CompositeDataSource cds : deletedCds) {
					this._myCds.remove(cds);
				}
			}

			debug_var(409619, "After the purge I have still ",
					Integer.valueOf(this._myCds.size()), " data sources.");
		}

		if (force || this._myCds.size() == 0) {
			debug_var(295115, "Force ? ", Boolean.valueOf(force),
					" or null cds size, I will join the thread!");

			notifyObject(lock);

			join_thread(this.dpThread);
			this._myCds.clear();
			this.dpThread = null;
			// fDataSources.clear();

		}

	}

	/**
	 * The csv data provider will switch on building a fake thread.
	 * 
	 * This thread simulates the arrival of the real bars from the outside,
	 * instead they are coming from a file.
	 */
	@Override
	public boolean switchOn() {
		if (dpThread != null) {
			debug_var(329521, "already started");
			return true;
		}

		dpThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// wait first that someone creates the ds

				// I wait for the object which will buzz me.
				synchronized (lock) {
					for (;;) {
						waitForEverOn(lock);

						boolean allStopped = true;

						/*
						 * I have to synchronize on the arrayList because there
						 * is the requestcomposite which modifies the list and
						 * this should not happen in concurrency with the
						 * structural modification of the list itself in the
						 * thread.
						 */
						synchronized (_myCds) {
							for (CompositeDataSource cds : _myCds) {
								CsvCompositeDataSource csvds = (CsvCompositeDataSource) cds;
								if (!csvds.isStopped()) {
									allStopped = false;
									break;
								}
							}
							if (allStopped) {
								debug_var(251699, "ending CSV thread");
								return;
							}

							for (CompositeDataSource cds : _myCds) {
								try {
									((CsvCompositeDataSource) cds)
											.notifyCsvTick();
								} catch (MonitorCancelledException e) {
									U.debug_var(109931,
											"the source has been cancelled.");
									try {
										cds.stop();
									} catch (DFSException e1) {
										e1.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}, "CSV Data Provider");

		dpThread.start();
		return true;
	}

}
