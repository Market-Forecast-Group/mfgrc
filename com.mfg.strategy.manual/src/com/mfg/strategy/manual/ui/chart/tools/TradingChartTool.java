/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.strategy.manual.ui.chart.tools;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.eclipse.core.runtime.jobs.Job;
import org.mfg.opengl.chart.interactive.ChartMouseEvent;
import org.mfg.opengl.chart.interactive.ChartPoint;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.ITradingModel.IOpeningHandler;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.interfaces.trading.IStrategy;
import com.mfg.strategy.PendingOrderInfo;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.strategy.manual.ManualStrategy;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.trading.configurations.TradingConfiguration;

/**
 * @author arian
 * 
 */
public class TradingChartTool extends InteractiveTool {
	private static final String PROFILE_SET_KEY = "TradingTool";
	private ChartPoint pos;
	private double xclose;
	private double yclose;
	private IOpeningHandler handler;
	private boolean dragging;
	private PortfolioStrategy portfolio;
	private ManualStrategy strategy;

	public TradingChartTool(Chart priceChart) {
		super("Trading", priceChart, BITMAP_TRADING_TOOL_ICON);
		setTooltip("Trading Tool: to move trade positions.");
	}

	private void updatePos(ChartMouseEvent e) {
		pos = e.getPosition();
		yclose = pos.getPlotY();
		double size = _glChart.yTickSize;

		long min = (long) yclose / (long) size * (long) size;
		long max = ((long) yclose / (long) size + 1) * (long) size;
		if (max - yclose < yclose - min) {
			yclose = max;
		} else {
			yclose = min;
		}
	}

	@Override
	public boolean mousePressed(ChartMouseEvent e) {
		handler = findHandler(e);

		if (handler != null) {
			dragging = true;
			updatePos(e);
			xclose = handler.getTime();
			updateCrosshair();
			repaint();
		}
		return handler != null;
	}

	private IOpeningHandler findHandler(ChartMouseEvent e) {
		ITradingModel model = getChart().getModel().getTradingModel();
		double x = e.getPosition().getPlotX();
		double y = e.getPosition().getPlotY();
		double xspace = getXSpace(40);
		double yspace = getYSpace(40);

		IOpeningHandler h = model.getOpeningHandler(x, y, xspace, yspace);

		if (h == null) {
			PortfolioStrategy portfolio2 = getPortfolio();
			if (portfolio2 == null) {
				return null;
			}

			// look for pending orders
			PendingOrderInfo[] orders = portfolio2.getPendingOrders();
			double miny = Double.MAX_VALUE;
			double minx = Double.MAX_VALUE;
			PendingOrderInfo minInfo = null;

			for (PendingOrderInfo info : orders) {
				double diffx = Math.abs(info.getTime() - x);
				double diffy = Math.abs(((OrderImpl) info.getOrder())
						.getOpeningPrice() - y);
				if (diffx < minx && diffx < xspace && diffy < miny
						&& diffy < yspace) {
					minx = diffx;
					miny = diffy;
					minInfo = info;
				}
			}

			if (minInfo != null) {
				final PendingOrderInfo info = minInfo;
				h = new IOpeningHandler() {

					@Override
					public boolean modifyPrice(long price) {
						// To prevent that an order is modified after been
						// executed.
						for (PendingOrderInfo aInfo : getPortfolio()
								.getPendingOrders()) {
							if (aInfo.getOrder().getId() == getOrderId()) {
								((OrderImpl) aInfo.getOrder())
										.setOpeningPrice((int) price);
								return true;
							}
						}
						return false;
					}

					@Override
					public long getTime() {
						return info.getTime();
					}

					@Override
					public long getPrice() {
						return ((OrderImpl) info.getOrder()).getOpeningPrice();
					}

					@Override
					public int getOrderId() {
						return info.getOrder().getId();
					}
				};
			}
		}

		return h;
	}

	@Override
	public boolean mouseMoved(ChartMouseEvent e) {
		dragging = false;
		handler = findHandler(e);
		if (handler != null) {
			getPortfolio();
			xclose = handler.getTime();
			yclose = handler.getPrice();
			repaint();
		}
		return handler != null;
	}

	private void updateCrosshair() {
		int x = _glChart.convertPlotToScreen_X(xclose);
		int y = _glChart.convertPlotToScreen_Y(yclose);
		_glChart.setCrosshair(x, y);
	}

	@Override
	public boolean mouseDragged(ChartMouseEvent e) {
		if (handler != null) {
			dragging = true;
			updatePos(e);
			updateCrosshair();
			repaint();
		}
		return handler != null;
	}

	@Override
	public boolean mouseReleased(ChartMouseEvent e) {
		dragging = false;
		boolean doit = handler != null;
		if (doit) {
			int id = handler.getOrderId();
			OrderImpl order = (OrderImpl) portfolio.getOrdersMap().get(
					Integer.valueOf(id));
			long price = (long) yclose;
			boolean isopen = handler.modifyPrice(price);
			boolean ispending = false;

			for (PendingOrderInfo pending : getPortfolio().getPendingOrders()) {
				if (pending.getOrder().getId() == id) {
					ispending = true;
					break;
				}
			}

			if (isopen) {
				order.setOpeningPrice((int) price);
				// TODO: it gets the first strategy for now. Maybe it should
				// be
				// the manual strategy or a new one!
				if (ispending) {
					portfolio.removePendingOrder(order.getId());
				}
				portfolio.addOrder(getStrategy(), order);
				updateChartData();
			}
			pos = null;
			handler = null;

		}

		return doit;
	}

	@Override
	public MouseCursor getMouseCursor() {
		return handler == null ? null : dragging ? MouseCursor.DRAGGING
				: MouseCursor.DEFAULT;
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		if (handler != null) {
			double xspace = getXSpace(40);
			double yspace = getYSpace(40);

			double x1 = xclose - xspace / 2;
			double x2 = xclose + xspace / 2;
			double y1 = yclose - yspace / 2;
			double y2 = yclose + yspace / 2;
			gl.glColor4fv(COLOR_CYAN, 0);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(x1, y1);
			gl.glVertex2d(x2, y1);
			gl.glVertex2d(x2, y2);
			gl.glVertex2d(x1, y2);
			gl.glVertex2d(x1, y1);
			gl.glEnd();

			long time = handler.getTime();
			long price = handler.getPrice();
			if (price != yclose) {
				double y = price < yclose ? y1 : y2;

				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glVertex2d(time, price);
				gl.glVertex2d(x1, y);
				gl.glVertex2d(x2, y);
				gl.glVertex2d(time, price);
				gl.glEnd();
			}

			gl.glRasterPos2d(x2 + getXSpace(10), yclose + getYSpace(10));
			_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10,
					"ID (" + handler.getOrderId() + ") "
							+ _glChart.getCustom().formatYTick(yclose));
		}
	}

	PortfolioStrategy getPortfolio() {
		if (portfolio == null) {
			Object content = getChart().getContent();
			Job[] jobs = content == null ? null : Job.getJobManager().find(
					content);
			if (jobs != null && jobs.length > 0) {
				SymbolJob<?> job = (SymbolJob<?>) jobs[0];
				TradingPipe pipe = job
						.getTradingPipe((TradingConfiguration) getChart()
								.getContent());
				if (pipe != null) {
					portfolio = pipe.getPortfolio();
				}
			}
		}
		return portfolio;
	}

	private ManualStrategy getStrategy() {
		if (strategy == null) {
			List<IStrategy> list = getPortfolio().getStrategies();
			for (IStrategy s : list) {
				if (s instanceof ManualStrategy) {
					strategy = (ManualStrategy) s;
					break;
				}
			}
		}
		return strategy;
	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_SET_KEY;
	}

	@Override
	public String getKeywords() {
		return "open close position trade";
	}
}
