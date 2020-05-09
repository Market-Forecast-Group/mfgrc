/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.ui;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mfg.mdb.runtime.DBSynchronizer;
import org.mfg.mdb.runtime.SessionMode;
import org.mfg.opengl.chart.GLChartCanvas;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.PriceChartCanvas_OpenGL;
import com.mfg.chart.layers.CompressedDataset;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.ITemporalPricesModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.chart.model.PhysicalTradingModel_MDB;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.chart.model.TradingModel_MDB;
import com.mfg.chart.ui.animation.ChartAnimator_Standalone;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.tradingdb.mdb.TradingMDBSession;

public interface IChartUtils {
	public static class WrapperDataset<T> implements IDataset {

		private T _model;

		public WrapperDataset(T model) {
			super();
			_model = model;
		}

		public T getModel() {
			return _model;
		}

		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public int getItemCount(int series) {
			return 0;
		}

		@Override
		public double getX(int series, int item) {
			return 0;
		}

		@Override
		public double getY(int series, int item) {
			return 0;
		}

	}

	// dataset
	public static final IDataset EMPTY_DATASET = new IDataset() {

		@Override
		public double getY(final int series, final int item) {
			return 0;
		}

		@Override
		public double getX(final int series, final int item) {
			return 0;
		}

		@Override
		public int getSeriesCount() {
			return 0;
		}

		@Override
		public int getItemCount(final int series) {
			return 0;
		}
	};

	public static <T> WrapperDataset<T> createWrapperDataset(T model) {
		return new WrapperDataset<>(model);
	}

	public static CompressedDataset createCompressedDataset(
			final IDataset dataset, final int maxNumber) {
		return new CompressedDataset(dataset, maxNumber);
	}

	public static CompressedDataset createCompressedDataset(final int maxNumber) {
		return new CompressedDataset(EMPTY_DATASET, maxNumber);
	}

	public static Point2D.Double findDatasetPoint(IDataset ds, PlotRange xr,
			PlotRange yr, long time, long price) {
		long dist = Long.MAX_VALUE;
		int j = -1;

		for (int i = 0; i < ds.getItemCount(0); i++) {
			long x = (long) ds.getX(0, i);
			long y = (long) ds.getY(0, i);
			if (xr.contains(x) && yr.contains(y)) {
				double d = Point2D.distance(time, price, x, y);
				if (d < dist) {
					dist = (long) d;
					j = i;
				}
			}
		}

		if (j != -1) {
			double x = ds.getX(0, j);
			double y = ds.getY(0, j);
			return new Point2D.Double(x, y);
		}

		return null;
	}

	public static void autorangeAll(final Chart chart, final IDataset ds) {
		double xmin = Double.MAX_VALUE;
		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;
		for (int series = 0; series < ds.getSeriesCount(); series++) {
			final int itemCount = ds.getItemCount(series);

			for (int item = 0; item < itemCount; item++) {
				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);
				if (x < xmin) {
					xmin = x;
				}
				if (x > xmax) {
					xmax = x;
				}
				if (y < ymin) {
					ymin = y;
				}
				if (y > ymax) {
					ymax = y;
				}
			}
		}

		chart.setXRange(new PlotRange(xmin, xmax));
		chart.setYRange(new PlotRange(ymin, ymax));
	}

	public static void autorange(final Chart chart, final IDataset... datasets) {
		final PlotRange xrange = chart.getXRange();

		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		for (int k = 0; k < datasets.length; k++) {
			IDataset ds = datasets[k];
			for (int series = 0; series < ds.getSeriesCount(); series++) {

				final int itemCount = ds.getItemCount(series);

				for (int item = 0; item < itemCount; item++) {
					final double x = ds.getX(series, item);
					final double y = ds.getY(series, item);

					if (xrange.contains(x)) {
						if (y < min) {
							min = y;
						}

						if (y > max) {
							max = y;
						}
					}
				}
			}
		}
		fixAutorange(chart, min, max);
	}

	public static void fixAutorange(final Chart chart, double aMin, double aMax) {
		double min = aMin;
		double max = aMax;

		PlotRange yrange = chart.getYRange();
		if (min < Double.MAX_VALUE) {
			final double tickSize = chart.getModel().getPriceModel()
					.getTickSize();
			final double margin = Math.max(tickSize, (max - min) * 0.1);
			final PlotRange bottomRange = new PlotRange(yrange.lower + margin,
					yrange.lower + margin * 2);
			final PlotRange topRange = new PlotRange(yrange.upper - margin * 2,
					yrange.upper - margin);

			if (bottomRange.contains(min)) {
				min = bottomRange.lower;
			} else {
				min -= margin;
			}

			if (topRange.contains(max)) {
				max = topRange.upper;
			} else {
				max += margin;
			}

			chart.setYRange(new PlotRange(min - margin, max + margin));
		}
	}

	public static SWTComposite_PriceChart_Connection connect_SWTComposite_PriceChart(
			final Composite comp, final Chart chart) {
		return new SWTComposite_PriceChart_Connection(comp, chart);
	}

	public static ChartModel_MDB createModel(File priceDbRoot,
			File inputDbRoot, File tradingDbRoot, boolean usePhysicalTimes)
			throws IOException {

		PriceMDBSession priceSession = new PriceMDBSession("prices",
				priceDbRoot, SessionMode.READ_ONLY, false, new DBSynchronizer());

		IndicatorMDBSession inputSession = inputDbRoot == null
				|| !inputDbRoot.exists() ? null : new IndicatorMDBSession(
				"input", inputDbRoot, SessionMode.READ_ONLY, false);
		TradingMDBSession tradingSession = tradingDbRoot == null
				|| !tradingDbRoot.exists() ? null : new TradingMDBSession(
				"trading", tradingDbRoot, SessionMode.READ_ONLY, false);

		PriceModel_MDB priceModel = usePhysicalTimes ? new PhysicalPriceModel_MDB(
				priceSession) : new PriceModel_MDB(priceSession);
		ChartModel_MDB model = new ChartModel_MDB(priceSession, inputSession,
				tradingSession, priceModel, ITemporalPricesModel.EMPTY,
				tradingSession == null ? ITradingModel.EMPTY
						: (usePhysicalTimes ? new PhysicalTradingModel_MDB(
								priceSession.getTimeMap(0), tradingSession,
								priceModel) : new TradingModel_MDB(
								tradingSession, priceModel)), usePhysicalTimes);
		return model;
	}

	public static void openStandAlongChartWindow(final Chart chart) {

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Demo OpenGL/SWT");
		shell.setLayout(new FillLayout());
		shell.setSize(640, 480);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());

		final GLData gldata = new GLData();
		gldata.doubleBuffer = true;

		chart.zoomOutAll(true);

		final GLChartCanvas glcanvas = new PriceChartCanvas_OpenGL(composite,
				gldata, chart);

		glcanvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case 'u':
					chart.setScrollingMode(chart.getScrollingMode()
							.swapScrolling());
					break;
				default:
					break;
				}
			}
		});

		shell.open();

		ChartAnimator_Standalone animator = new ChartAnimator_Standalone(chart);
		animator.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		animator.stop();

		if (!glcanvas.isDisposed()) {
			glcanvas.dispose();
		}
		if (!display.isDisposed()) {
			display.dispose();
		}
	}

	public static void openStandAlongChartWindow(File priceDbRoot,
			File inputDbRoot, File tradingDbRoot, boolean usePhysicalTimes)
			throws IOException {
		ChartModel_MDB model = createModel(priceDbRoot, inputDbRoot,
				tradingDbRoot, usePhysicalTimes);
		openStandAlongChartWindow(new Chart("Demo", model, ChartType.TRADING,
				tradingDbRoot));
	}

	public static void openStandAlongChartWindow(PriceMDBSession priceSession,
			IndicatorMDBSession inputSession, TradingMDBSession tradingSession) {
		ChartModel_MDB model = new ChartModel_MDB(priceSession, inputSession,
				tradingSession);
		openStandAlongChartWindow(new Chart("Demo", model, ChartType.TRADING,
				tradingSession));
	}

	public static void openStandAlong_Equity_ChartWindow(File priceDbRoot,
			File inputDbRoot, File tradingDbRoot, boolean usePhysicalTimes)
			throws IOException {
		ChartModel_MDB model = createModel(priceDbRoot, inputDbRoot,
				tradingDbRoot, usePhysicalTimes);
		openStandAlongChartWindow(new Chart("Demo", model, ChartType.EQUITY,
				tradingDbRoot));
	}

	public static void openStandAlong_Equity_ChartWindow(
			PriceMDBSession priceSession, IndicatorMDBSession inputSession,
			TradingMDBSession tradingSession) {
		ChartModel_MDB model = new ChartModel_MDB(priceSession, inputSession,
				tradingSession);
		openStandAlongChartWindow(new Chart("Demo", model, ChartType.EQUITY,
				tradingSession));
	}

	/**
	 * Create a stand along chart window with the chart data of a ZIP file.
	 * 
	 * @param tmpDir
	 *            A directory where is uncompressed (temporarily) the ZIP file
	 *            content.
	 * @param zipFile
	 *            The ZIP file with the chart data.
	 * @throws IOException
	 */
	public static void openStandAlongChartWindow(File tmpDir, File zipFile,
			boolean usePhysicalTimes) throws IOException {
		openStandAlongChartWindow(createChart_fromZipFile(tmpDir, zipFile,
				usePhysicalTimes));
	}

	public static Chart createChart_fromZipFile(File tmpDir, File zipFile,
			boolean usePhysicalTimes) throws ZipException, IOException {
		File dstRoot = new File(tmpDir, "tmp-chart-data-"
				+ UUID.randomUUID().toString());
		ChartType type = ChartType.FINANCIAL;

		try (FileInputStream fis = new FileInputStream(zipFile);
				ZipInputStream in = new ZipInputStream(fis)) {
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				Path path = dstRoot.toPath().resolve(entry.getName());
				Files.createDirectories(path.getParent());
				Files.copy(in, path);
				in.closeEntry();

				if (entry.getName().equals("chart.properties")) {
					Properties props = new Properties();
					try (FileInputStream propsinput = new FileInputStream(
							path.toFile())) {
						props.load(propsinput);
						type = ChartType.valueOf(props
								.getProperty("Chart-Type"));
					}
				}
			}
		}

		ChartModel_MDB model = createModel(dstRoot.toPath().resolve("prices")
				.toFile(), dstRoot.toPath().resolve("input").toFile(), dstRoot
				.toPath().resolve("trading").toFile(), usePhysicalTimes);

		Chart chart = new Chart(zipFile.getName(), model, type, zipFile);

		return chart;
	}

	public static void zipChartData(ChartModel_MDB model, ChartType type,
			File zipFile) throws IOException {

		try (FileOutputStream fos = new FileOutputStream(zipFile, false);
				final ZipOutputStream out = new ZipOutputStream(fos)) {

			class ZipVisitor extends SimpleFileVisitor<Path> {
				private final Path _src;
				private final String _subdir;

				public ZipVisitor(Path src, String subdir) {
					super();
					_src = src;
					_subdir = subdir;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Path rel = Paths.get(_subdir)
							.resolve(_src.relativize(file));
					ZipEntry entry = new ZipEntry(rel.toString());
					out.putNextEntry(entry);
					Files.copy(file, out);
					return FileVisitResult.CONTINUE;
				}
			}

			Path root = model.getPriceSession().getRoot().toPath();
			Files.walkFileTree(root, new ZipVisitor(root, "prices"));
			IndicatorMDBSession inputSession = model.getIndicatorSession();
			if (inputSession != null) {
				root = inputSession.getRoot().toPath();
				Files.walkFileTree(root, new ZipVisitor(root, "input"));
				TradingMDBSession tradingSession = model.getTradingSession();
				if (tradingSession != null) {
					root = tradingSession.getRoot().toPath();
					Files.walkFileTree(root, new ZipVisitor(root, "trading"));
				}
			}
			out.putNextEntry(new ZipEntry("chart.properties"));
			Properties props = new Properties();
			props.put("Chart-Type", type.name());
			props.store(out, null);
		}
	}
}
