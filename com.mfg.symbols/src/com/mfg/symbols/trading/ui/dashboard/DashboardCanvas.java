package com.mfg.symbols.trading.ui.dashboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.widgets.figures.MeterFigure;
import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.events.ITradeMessage;
import com.mfg.strategy.PortfolioStrategy;

public class DashboardCanvas extends FigureCanvas {
	Panel _root;
	private IFigure _dragFig;
	private Point _dragOffset;
	private IFigure _selFig;
	private Map<IFigure, FigureAdapter<?>> _map;

	public DashboardCanvas(Composite parent) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_REDRAW_RESIZE
				| SWT.DOUBLE_BUFFERED);
		_map = new HashMap<>();
		_root = new Panel();
		_root.setLayoutManager(new XYLayout());
		_root.setBackgroundColor(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		setContents(_root);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				handleMouseDown(e);
			}

			@Override
			public void mouseUp(MouseEvent e) {
				handleMouseUp(e);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				handleMouseDoubleClick(e);
			}
		});
		addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				handleMouseMove(e);
			}
		});

	}

	protected void handleMouseDoubleClick(MouseEvent e) {
		_dragFig = null;
		IFigure fig = getElement(e.x, e.y);
		if (fig != null) {
			FigureAdapter<?> adapter = _map.get(fig);
			if (fig instanceof GaugeFigure) {
				GaugeSettingsDialog dlg = new GaugeSettingsDialog(getShell());
				dlg.setFigure((GaugeAdapter) adapter);
				dlg.open();
			} else if (fig instanceof MeterFigure) {
				MeterSettingsDialog dlg = new MeterSettingsDialog(getShell());
				dlg.setFigure((MeterAdapter) adapter);
				dlg.open();
			} else if (fig instanceof TankFigure) {
				TankSettingsDialog dlg = new TankSettingsDialog(getShell());
				dlg.setFigure((TankAdapter) adapter);
				dlg.open();
			} else if (adapter instanceof WidgetFigureAdapter) {
				FigSettingsDialog dlg = new FigSettingsDialog(getShell());
				dlg.setFigure((WidgetFigureAdapter<?>) adapter);
				dlg.open();
			} else if (adapter instanceof PolylineAdapter) {
				PolylineSettingsDialog dlg = new PolylineSettingsDialog(
						getShell());
				dlg.setFigure((PolylineAdapter) adapter);
				dlg.open();
			}
		}
	}

	public Collection<FigureAdapter<?>> getFigureAdapters() {
		return _map.values();
	}

	protected void handleMouseDown(MouseEvent e) {
		IFigure fig = getElement(e.x, e.y);
		if (e.button == 1) {
			_dragFig = fig;
			if (_dragFig != null) {
				_root.remove(_dragFig);
				_root.add(_dragFig, _dragFig.getBounds());
				Rectangle b = _dragFig.getBounds();
				_dragOffset = new Point(e.x - b.x, e.y - b.y);
			}
		} else {
			_selFig = fig;
		}
	}

	public IFigure getSelection() {
		return _selFig;
	}

	public IFigure getElement(int x, int y) {
		return getElement(_root.findFigureAt(x, y));
	}

	private IFigure getElement(IFigure f) {
		return f == null ? null : (f.getParent() == _root ? f : getElement(f
				.getParent()));
	}

	protected void handleMouseUp(@SuppressWarnings("unused") MouseEvent e) {
		_dragFig = null;
	}

	protected void handleMouseMove(MouseEvent e) {
		if (_dragFig != null) {
			Dimension size = _dragFig.getSize();
			if (_dragFig instanceof StrategyFigure) {
				size.width = -1;
				size.height = -1;
			}
			_root.setConstraint(_dragFig, new Rectangle(e.x - _dragOffset.x,
					e.y - _dragOffset.y, size.width, size.height));
			_root.invalidateTree();
		}
	}

	public void addFigure(FigureAdapter<?> adapter, Rectangle bounds) {
		Rectangle b = bounds;
		if (b == null) {
			b = new Rectangle((int) (10 + Math.random() * 50),
					(int) (10 + Math.random() * 50), 200, 200);
			if (adapter instanceof MeterAdapter) {
				b.setHeight(100);
			}
		}
		if (adapter.isDynamic()) {
			_root.add(adapter.getFigure(), new Rectangle(b.x, b.y, -1, -1));
		} else {
			_root.add(adapter.getFigure(), b);
		}
		_map.put(adapter.getFigure(), adapter);
	}

	public void removeSelection() {
		if (_selFig != null) {
			_root.remove(_selFig);
			_map.remove(_selFig);
		}
	}

	public void load(JSONObject json) {
		_root.removeAll();
		_map.clear();
		try {
			JSONArray list = json.getJSONArray("widgets");
			for (int i = 0; i < list.length(); i++) {
				JSONObject obj = list.getJSONObject(i);
				String name = obj.getString("name");
				FigureAdapter<? extends IFigure> fig = null;
				switch (name) {
				case "gauge":
					fig = new GaugeAdapter(this);
					break;
				case "tank":
					fig = new TankAdapter(this);
					break;
				case "meter":
					fig = new MeterAdapter(this);
					break;
				case "strategy":
					fig = new StrategyAdapter(this);
					break;
				case "polyline":
					fig = new PolylineAdapter(this);
					break;
				case "progress-bar":
					fig = new ProgressBarAdapter(this);
					break;
				}

				if (fig != null) {
					fig.updateFromJSON(obj);
					addFigure(fig, fig.getFigure().getBounds());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void toJSON(JSONStringer s) throws JSONException {
		s.key("widgets");
		s.array();
		for (Object fig : _root.getChildren()) {
			FigureAdapter<?> adapter = _map.get(fig);
			s.object();
			adapter.toJSON(s);
			s.endObject();
		}
		s.endArray();
	}

	public void handleLogMessage(ITradeMessage msg, PortfolioStrategy portfolio) {
		for (FigureAdapter<?> adapter : _map.values()) {
			adapter.handleLogMessage(msg, portfolio);
		}
	}

}
