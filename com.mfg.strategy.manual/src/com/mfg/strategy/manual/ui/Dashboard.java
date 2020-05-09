//
//package com.mfg.strategy.manual.ui;
//
//import java.util.List;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.DisposeEvent;
//import org.eclipse.swt.events.MouseAdapter;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.GC;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//
//import com.mfg.interfaces.MFGPlugin;
//import com.mfg.interfaces.trading.ITradeConfiguration;
//import com.mfg.interfaces.trading.TradingListener;
//import com.mfg.strategy.manual.ManualSymbolsPlugin;
//import com.mfg.utils.ListenerSupport;
//import com.mfg.utils.ui.AdvancedCanvas;
//
//public class Dashboard extends AdvancedCanvas {
//
//	private ITradeConfiguration selectedMarket;
//	private boolean[] selectedScales;
//	private int maxScales;
//	protected int selectedScalesCount;
//	private int marketColumnWidth;
//	private final static int SCALE_WIDTH = 180;
//	private final static int LINE_HEIGHT = 30;
//	private static final int HEADER_HEIGHT = LINE_HEIGHT * 2;
//	private int BOARD_Y = 10 + HEADER_HEIGHT;
//	protected ListenerSupport chartBoxClicked = new ListenerSupport();
//	private int chartColumnWidth = 60;
//	private Image marketImage;
//	private static final int SCALE_SELECTOR_SIZE = 30;
//
//
//	public Dashboard(Composite parent) {
//		super(parent, SWT.NONE);
//
//		initImages();
//
//		addMouseListeners();
//		addLaunchListeners();
//
//		updateDashboard();
//	}
//
//
//	private void initImages() {
//		marketImage = ManualSymbolsPlugin.getBundledImageDescriptor("icons/db.png").createImage();
//	}
//
//
//	@Override
//	protected void canvasDisposed(DisposeEvent e) {
//		super.canvasDisposed(e);
//		if (marketImage != null) {
//			marketImage.dispose();
//		}
//	}
//
//
//	private void addLaunchListeners() {
//		MFGPlugin.getDefault().addTradeConfigurationListener(new TradingListener() {
//
//			@Override
//			public void tradeConfigurationAdded(ITradeConfiguration configuration) {
//				updateDashboard();
//			}
//
//
//			@Override
//			public void tradeConfigurationRemoved(ITradeConfiguration configuration) {
//				updateDashboard();
//
//			}
//		});
//		Runnable chartListener = new Runnable() {
//
//			@Override
//			public void run() {
//				if (!isDisposed()) {
//					updateDashboard();
//				}
//			}
//		};
//		TradingChartPlugin.getDefault().getChartsChanged().addListener(chartListener);
//	}
//
//
//	public void updateDashboard() {
//		resetSelectedScales();
//
//		redraw();
//		update();
//	}
//
//
//	protected int getScaleCount(ITradeConfiguration launch) {
//		// TODO:getScaleCount
//		return 6;
//	}
//
//
//	private double getProbability_TargetSwingM1(ITradeConfiguration launch, int level) {
//		// TODO:getProbability_TargetSwingM1
//		return 0;
//	}
//
//
//	private double getProbability_Swing0M1(ITradeConfiguration launch, int level) {
//		// TODO: getProbability_Swing0M1
//		return 0;
//	}
//
//
//	protected Color getChartColor(ITradeConfiguration launch) {
//		List<TradingChartView> charts = TradingChartPlugin.getDefault().getChartViews(launch);
//		return charts.isEmpty() ? null : charts.get(0).getColor();
//	}
//
//
//	public ListenerSupport getChartCellClicked() {
//		return chartBoxClicked;
//	}
//
//
//	private void resetSelectedScales() {
//		List<ITradeConfiguration> tradeConfigurations = ManualSymbolsPlugin.getDefault().getMSManager().getTradeConfigurations();
//		for (ITradeConfiguration launch : tradeConfigurations) {
//			maxScales = Math.max(getScaleCount(launch), maxScales);
//		}
//		boolean[] old = selectedScales;
//		this.selectedScales = new boolean[maxScales + 1];
//		selectedScalesCount = 0;
//		for (int l = 1; l <= maxScales; l++) {
//			boolean selected = old != null && l < old.length ? old[l] : true;
//			selectedScales[l] = selected;
//			if (selected) {
//				selectedScalesCount++;
//			}
//		}
//	}
//
//
//	public ITradeConfiguration getSelectedLaunch() {
//		return selectedMarket;
//	}
//
//
//	/**
//	 * @param control
//	 */
//	private void addMouseListeners() {
//		addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDown(MouseEvent e) {
//				List<ITradeConfiguration> tradeConfigurations = ManualSymbolsPlugin.getDefault().getMSManager().getTradeConfigurations();
//				int launchCount = tradeConfigurations.size();
//				// e.y -= origin.y;
//				e.x -= origin.x;
//
//				// select launch
//				if (e.y > BOARD_Y && e.y < BOARD_Y + LINE_HEIGHT * launchCount) {
//					int index = (e.y - BOARD_Y) / LINE_HEIGHT;
//					selectedMarket = tradeConfigurations.get(index);
//
//					redraw();
//					update();
//				}
//
//				// select level
//
//				int ySelector = origin.y + LINE_HEIGHT * 3 + LINE_HEIGHT * tradeConfigurations.size();
//				if (e.x > 10 && e.x < 10 + maxScales * (SCALE_SELECTOR_SIZE + 5) && e.y > ySelector && e.y <= ySelector + SCALE_SELECTOR_SIZE) {
//					int l = (e.x - 10) / (SCALE_SELECTOR_SIZE + 5) + 1;
//					selectedScales[l] = !selectedScales[l];
//
//					selectedScalesCount = 0;
//
//					for (l = 1; l <= maxScales; l++) {
//						selectedScalesCount += selectedScales[l] ? 1 : 0;
//					}
//
//					updateScrollDimension();
//				}
//			}
//		});
//		setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
//		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
//	}
//
//
//	public Rectangle getPreferredBounds() {
//		Point size = computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
//		return new Rectangle(0, 0, size.x, size.y);
//	}
//
//
//	@Override
//	public void paintCanvas(PaintEvent e) {
//		GC gc = e.gc;
//		Color bg = gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
//		Color fg = gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
//		Color selBg = gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION);
//		Color selFg = gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
//		Color widgetFg = getForeground();
//		Color widgetBg = getBackground();
//		Color whiteBg = gc.getDevice().getSystemColor(SWT.COLOR_WHITE);
//
//		gc.setBackground(whiteBg);
//		gc.fillRectangle(getBounds());
//		List<ITradeConfiguration> tradeConfigurations = ManualSymbolsPlugin.getDefault().getMSManager().getTradeConfigurations();
//
//		if (tradeConfigurations.isEmpty()) {
//			gc.drawString("No markets to show", 30, 30);
//
//		} else {
//
//			int fontH = gc.getFontMetrics().getHeight();
//			int fontW = gc.getFontMetrics().getAverageCharWidth();
//			int strYOffset = (LINE_HEIGHT - fontH) / 2;
//
//			// for (int marketIndex = 0; marketIndex < model.getMarketCount();
//			// marketIndex++) {
//
//			for (ITradeConfiguration launch : tradeConfigurations) {
//				String name = launch.getName();
//				marketColumnWidth = Math.max(marketColumnWidth, name.length() * fontW + 40);
//			}
//
//			chartColumnWidth = 0;
//
//			for (ITradeConfiguration launch : tradeConfigurations) {
//				List<TradingChartView> charts = TradingChartPlugin.getDefault().getChartViews(launch);
//				chartColumnWidth = Math.max(30 * charts.size() + 5, chartColumnWidth);
//			}
//
//			chartColumnWidth = Math.max(chartColumnWidth, 60);
//
//			int boardWidth = marketColumnWidth + chartColumnWidth + (selectedScalesCount + 1) * SCALE_WIDTH - SCALE_WIDTH / 2;
//			int boardHeight = LINE_HEIGHT * tradeConfigurations.size();
//
//			// border
//
//			gc.setBackground(bg);
//			gc.fillRoundRectangle(origin.x + 10, BOARD_Y - LINE_HEIGHT, boardWidth, boardHeight + LINE_HEIGHT, 20, 20);
//
//			gc.setBackground(whiteBg);
//
//			gc.fillRectangle(origin.x + 10 + marketColumnWidth, BOARD_Y, boardWidth - marketColumnWidth, boardHeight);
//
//			// gc.drawRectangle(10 + maxMarketWidth, BOARD_Y, boardWidth
//			// - maxMarketWidth, boardHeight);
//			gc.setForeground(fg);
//			gc.drawRoundRectangle(origin.x + 10, BOARD_Y - LINE_HEIGHT, boardWidth, boardHeight + LINE_HEIGHT, 20, 20);
//
//			// STRINGS
//			gc.setForeground(((Control) e.widget).getForeground());
//
//			int x = origin.x + 10 + marketColumnWidth;
//			int y = origin.y + 10;
//
//			// header strings
//
//			for (int level = 1; level <= maxScales + 1; level++) {
//
//				if (level == 1) {
//					// chart string
//					gc.setBackground(bg);
//					String str = "Chart";
//					gc.drawString(str, x + (chartColumnWidth / 2 - (fontW * 5) / 2), y + LINE_HEIGHT + strYOffset);
//					x += chartColumnWidth;
//				}
//
//				if (level == maxScales + 1 || isSelectedScale(level)) {
//					if (level <= maxScales) {
//						gc.setForeground(fg);
//						gc.drawRectangle(x + 5, y, SCALE_WIDTH - 10, LINE_HEIGHT);
//						gc.setForeground(widgetFg);
//						int strX = x + (SCALE_WIDTH - (6 * fontW)) / 2;
//						gc.setBackground(widgetBg);
//						gc.drawString("Scale " + level, strX, y + strYOffset);
//						gc.setBackground(bg);
//
//						strX = x + SCALE_WIDTH / 4 - (6 * fontW) / 2;
//						gc.drawString("S0/S-1", strX, y + LINE_HEIGHT + strYOffset);
//
//						strX = x + SCALE_WIDTH / 2 + SCALE_WIDTH / 4 - (5 * fontW) / 2;
//						gc.drawString("T/S-1", strX, y + LINE_HEIGHT + strYOffset);
//						x += SCALE_WIDTH;
//					} else {
//						gc.setBackground(bg);
//						int strX = x + SCALE_WIDTH / 4 - (3 * fontW) / 2;
//						gc.drawString("P/L", strX, y + LINE_HEIGHT + strYOffset);
//					}
//				}
//			}
//
//			x = origin.x + 10 + 5;
//			y = BOARD_Y;
//
//			// market strings
//			// for (int marketIndex = 0; marketIndex < model.getMarketCount();
//			// marketIndex++) {
//			for (ITradeConfiguration launch : tradeConfigurations) {
//				if (launch == selectedMarket) {
//					gc.setBackground(selBg);
//					gc.setForeground(selFg);
//					gc.fillRectangle(x - 5, y, marketColumnWidth, LINE_HEIGHT + 1);
//				}
//
//				int strY = y + strYOffset;
//
//				gc.drawImage(marketImage, x, strY);
//
//				String marketName = launch.getName();
//				gc.drawString(marketName, x + 20, strY);
//				y += LINE_HEIGHT;
//				gc.setBackground(bg);
//				gc.setForeground(widgetFg);
//
//			}
//
//			y = BOARD_Y;
//
//			// chart colors
//			int[] dash = { 2, 2 };
//			for (ITradeConfiguration launch : tradeConfigurations) {
//				x = origin.x + 10 + marketColumnWidth;
//				List<TradingChartView> charts = TradingChartPlugin.getDefault().getChartViews(launch);
//				if (charts.size() < 2) {
//					x += chartColumnWidth / 2.0 - 25 / 2.0 - 5;
//				}
//				if (charts.isEmpty()) {
//					gc.setLineDash(dash);
//					gc.drawRectangle(x + 5, y + 5, 25, LINE_HEIGHT - 10);
//				} else {
//					for (TradingChartView chart : charts) {
//						Color color = chart.getColor();
//						gc.setBackground(color);
//						x += 5;
//						gc.fillRectangle(x, y + 5, 25, LINE_HEIGHT - 10);
//						gc.drawRectangle(x, y + 5, 25, LINE_HEIGHT - 10);
//						x += 25;
//					}
//				}
//				y += LINE_HEIGHT;
//			}
//			gc.setLineDash(null);
//			gc.setBackground(bg);
//
//			gc.setBackground(whiteBg);
//			y = BOARD_Y;
//			// table probs
//			for (ITradeConfiguration launch : tradeConfigurations) {
//				x = origin.x + 10 + marketColumnWidth + chartColumnWidth + 10;
//				for (int level = 1; level <= getScaleCount(launch); level++) {
//					if (isSelectedScale(level)) {
//						double s0m1 = getProbability_Swing0M1(launch, level);
//						double tm1 = getProbability_TargetSwingM1(launch, level);
//
//						int strY = y + LINE_HEIGHT / 2 - fontH / 2;
//						gc.drawString(String.format("%.2f", s0m1), x, strY);
//						gc.drawString(String.format("%.2f", tm1), x + SCALE_WIDTH / 2, strY);
//						x += SCALE_WIDTH;
//					}
//				}
//				y += LINE_HEIGHT;
//			}
//
//			y = BOARD_Y;
//			x = origin.x + 10 + marketColumnWidth + chartColumnWidth + selectedScalesCount * SCALE_WIDTH + 10;
//			// table p/l
//			for (@SuppressWarnings("unused")
//			ITradeConfiguration launch : tradeConfigurations) {
//				int strY = y + LINE_HEIGHT / 2 - fontH / 2;
//				// gc.drawString(String.format("%.2f", 0.0), x, strY);
//				gc.drawString("?", x, strY);
//				y += LINE_HEIGHT;
//			}
//
//			// LINES
//
//			gc.setForeground(fg);
//
//			x = origin.x + 10;
//			y = BOARD_Y;
//
//			// horizontal lines
//			boolean first = true;
//			for (@SuppressWarnings("unused")
//			ITradeConfiguration launch : tradeConfigurations) {
//				if (first) {
//					first = false;
//					gc.drawLine(x + marketColumnWidth, y, origin.x + 10 + boardWidth, y);
//				} else {
//					gc.drawLine(x, y, origin.x + 10 + boardWidth, y);
//				}
//				y += LINE_HEIGHT;
//			}
//
//			x = origin.x + 10 + marketColumnWidth;
//			y = BOARD_Y;
//
//			// vertical chart line
//			gc.drawLine(x, y - LINE_HEIGHT, x, BOARD_Y + boardHeight);
//			x += chartColumnWidth;
//
//			// vertical lines
//			for (int level = 1; level <= maxScales + 1; level++) {
//				boolean selected = level <= maxScales && isSelectedScale(level);
//
//				if (level <= maxScales && !selected) {
//					continue;
//				}
//
//				// if (level == 1) {
//				// gc.drawLine(x, y, x, BOARD_Y + boardHeight);
//				// } else
//				{
//					gc.drawLine(x, y - LINE_HEIGHT, x, BOARD_Y + boardHeight);
//					if (level == maxScales + 1) {
//						gc.drawLine(x + 2, y - LINE_HEIGHT, x + 2, BOARD_Y + boardHeight);
//					}
//				}
//				if (level <= maxScales) {
//					gc.drawLine(x + SCALE_WIDTH / 2, y - LINE_HEIGHT, x + SCALE_WIDTH / 2, BOARD_Y + boardHeight);
//				}
//				x += SCALE_WIDTH;
//			}
//
//			// SCALE SELECTOR
//			gc.setForeground(fg);
//			gc.setBackground(whiteBg);
//			x = origin.x + 10;
//			y = origin.y + LINE_HEIGHT * 3 + LINE_HEIGHT * tradeConfigurations.size();
//			for (int level = 1; level <= maxScales; level++) {
//				if (isSelectedScale(level)) {
//					gc.setBackground(selBg);
//					gc.setForeground(selFg);
//					gc.fillRoundRectangle(x, y, SCALE_SELECTOR_SIZE, SCALE_SELECTOR_SIZE, 10, 10);
//				} else {
//					gc.setForeground(fg);
//					gc.drawRoundRectangle(x, y, SCALE_SELECTOR_SIZE, SCALE_SELECTOR_SIZE, 10, 10);
//				}
//
//				if (isSelectedScale(level)) {
//					gc.setForeground(selFg);
//				}
//				String str = Integer.toString(level);
//				gc.drawString(str, x + (SCALE_SELECTOR_SIZE - (fontW * str.length())) / 2, y + (SCALE_SELECTOR_SIZE - fontH) / 2);
//				x += SCALE_SELECTOR_SIZE + 5;
//				gc.setForeground(widgetFg);
//				gc.setBackground(widgetBg);
//			}
//		}
//	}
//
//
//	private boolean isSelectedScale(int level) {
//		return selectedScales[level];
//	}
//
//
//	@Override
//	public Point computeSize(int wHint, int hHint, boolean changed) {
//		List<ITradeConfiguration> tradeConfigurations = ManualSymbolsPlugin.getDefault().getMSManager().getTradeConfigurations();
//
//		int w = 10 + marketColumnWidth + chartColumnWidth + (selectedScalesCount) * SCALE_WIDTH + SCALE_WIDTH / 2 + 10;
//		int h = 5 * LINE_HEIGHT + tradeConfigurations.size() * LINE_HEIGHT;
//		return new Point(w, h);
//	}
//
//
//	@Override
//	protected void redrawBecauseScroll() {
//		updateBOARD_Y();
//		super.redrawBecauseScroll();
//	}
//
//
//	/**
//	 * 
//	 */
//	private void updateBOARD_Y() {
//		BOARD_Y = origin.y + 10 + HEADER_HEIGHT;
//	}
// }
