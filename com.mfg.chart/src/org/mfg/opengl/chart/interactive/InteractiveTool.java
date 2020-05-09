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
package org.mfg.opengl.chart.interactive;

import java.util.List;

import javax.media.opengl.GL2;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.GLChart;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.IGLConstantsMFG;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.chart.ui.settings.ProfiledObject;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public abstract class InteractiveTool extends ProfiledObject implements
		IGLConstantsMFG {
	protected GLChart _glChart;
	protected final static GLUT _glut = new GLUT();

	private String _name;
	private final Chart _chart;
	private final BitmapData _icon;
	private String _tooltip;
	private boolean _alwaysPaint;
	private IContextActivation _contextToken;
	private ChartView _view;

	public InteractiveTool(String name, Chart chart, BitmapData icon) {
		this._name = name;
		this._chart = chart;
		this._icon = icon;
		_tooltip = name;
		_alwaysPaint = true;
	}

	public static class ToolAction extends Action {
		public ToolAction(String id) {
			this(id, null, SWT.PUSH);
		}

		public ToolAction(String id, ImageDescriptor icon) {
			this(id, icon, SWT.PUSH);
		}

		public ToolAction(String id, ImageDescriptor icon, int style) {
			super(null, style);
			if (icon != null) {
				setImageDescriptor(icon);
			}
			if (id != null) {
				setActionDefinitionId(id);

				ICommandService cmdServ = (ICommandService) PlatformUI
						.getWorkbench().getService(ICommandService.class);
				Command cmd = cmdServ.getCommand(id);
				try {
					String name = cmd.getName();
					setText(name);
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("static-method")
	public String getContextId() {
		return null;
	}

	public boolean isAlwaysPaint() {
		return _alwaysPaint;
	}

	public void setAlwaysPaint(boolean alwaysPaint) {
		_alwaysPaint = alwaysPaint;
	}

	public BitmapData getIcon() {
		return _icon;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getTooltip() {
		return _tooltip;
	}

	public void setTooltip(String tooltip) {
		_tooltip = tooltip;
	}

	public void init(GLChart chart) {
		this._glChart = chart;
	}

	public void repaint() {
		getChart().syncRepaint();
	}

	/**
	 * 
	 * @param gl
	 * @param w
	 * @param h
	 */
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		//
	}

	/**
	 * 
	 * @param gl
	 * @param w
	 * @param h
	 */
	public void paintOnScreenMatrix(GL2 gl, int w, int h) {
		//
	}

	/**
	 * 
	 */
	public void selected() {
		if (_view == null) {
			List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
			for (ChartView view : views) {
				if (view.getChart() == getChart()) {
					_view = view;
					break;
				}
			}
		}
		String contextId = getContextId();
		// there are tools which do not have a context
		if (contextId != null) {
			_contextToken = getContextService().activateContext(contextId);
		}
		setAlwaysPaint(true);
	}

	public void unselected() {
		// there are tools which do not have a context
		if (_contextToken != null) {
			getContextService().deactivateContext(_contextToken);
		}
	}

	private IContextService getContextService() {
		return (IContextService) _view.getSite().getService(
				IContextService.class);
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("static-method")
	public boolean mousePressed(ChartMouseEvent e) {
		return false;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("static-method")
	public boolean mouseReleased(ChartMouseEvent e) {
		return false;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("static-method")
	public boolean mouseScrolled(ChartMouseEvent e) {
		return false;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("static-method")
	public boolean mouseDragged(ChartMouseEvent e) {
		return false;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("static-method")
	public boolean mouseMoved(ChartMouseEvent e) {
		return false;
	}

	/**
	 * @return Return false if the tool does not want to propagate the key. For
	 *         example, if the key is ESC, the tool is deslected unless the tool
	 *         returns false.
	 * @param key
	 */
	@SuppressWarnings("static-method")
	public boolean keyPressed(Object key) {
		return true;
	}

	protected double getYScrToPlot() {
		return _glChart.plot.yrange.getLength() / _glChart.plot.screenHeight;
	}

	protected double getXScrToPlot() {
		return _glChart.plot.xrange.getLength() / _glChart.plot.screenWidth;
	}

	protected double getXSpace(int len) {
		return getXScrToPlot() * len;
	}

	protected double getYSpace(int len) {
		return getYScrToPlot() * len;
	}

	/**
	 * 
	 */
	protected void updateChartData() {
		getChart().fireRangeChanged();
	}

	@SuppressWarnings("static-method")
	public MouseCursor getMouseCursor() {
		return null;
	}

	public Chart getChart() {
		return _chart;
	}

	public abstract String getKeywords();

	/**
	 * Fill the menu with related options.
	 * 
	 * @param menu
	 */
	public void fillMenu(IMenuManager menu) {
		// nothing
	}

	public void autorange() {
		// nothing by default
	}
}
