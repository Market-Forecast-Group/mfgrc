package com.mfg.symbols.trading.ui.dashboard;

import static java.lang.System.out;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.ui.interactive.PolylineTool.EquationType;
import com.mfg.symbols.trading.ui.dashboard.PolylineEventGenerator.EventArg;
import com.mfg.symbols.trading.ui.dashboard.PolylineEventGenerator.EventColor;
import com.mfg.symbols.trading.ui.dashboard.PolylineWidgetModel.RowInfo;

public class PolylineFigure extends Panel {
	private PolylineWidgetModel _model;
	private Map<RowInfo, Figure> _rowStatusMap;
	private PolylineAdapter _adapter;

	public PolylineFigure() {
		build(new PolylineWidgetModel(10));
	}

	public PolylineAdapter getAdapter() {
		return _adapter;
	}

	public void setAdapter(PolylineAdapter adapter) {
		_adapter = adapter;
	}

	public void build(PolylineWidgetModel model) {
		_rowStatusMap = new HashMap<>();

		_model = model;

		removeAll();

		setSize(-1, -1);

		setBorder(new MarginBorder(0));

		GridLayout layout = new GridLayout(5, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayoutManager(layout);

		// add title
		Label title = new Label("POLYLINE");
		title.setFont(SWTResourceManager.getFont(Font.SANS_SERIF, 20, 0));
		title.setBorder(new MarginBorder(10));
		title.setForegroundColor(ColorConstants.white);
		title.setBackgroundColor(ColorConstants.black);
		title.setOpaque(true);

		add(title);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 5;
		setConstraint(title, gd);

		// add headers
		addHeader("Scale");
		addHeader("Event");
		addHeader("Status");
		addHeader("Chart");
		addHeader("Update Type");

		int lastScale = -1;
		for (RowInfo row : _model.getRows()) {
			if (row.include) {
				EquationType polyline = row.polyline;
				// scale
				Figure label = new Label(
						row.scale != lastScale ? Integer.toString(row.scale)
								: "");
				label.setBorder(new MarginBorder(5));
				add(label);
				gd = new GridData();
				gd.horizontalAlignment = GridData.CENTER;
				setConstraint(label, gd);

				{
					// poly-line
					label = new Label("Polyline "
							+ Integer.toString(polyline.ordinal() + 1));
					label.setCursor(Display.getDefault().getSystemCursor(
							SWT.CURSOR_HAND));
					label.addMouseListener(new MouseListener() {

						@Override
						public void mouseReleased(MouseEvent arg0) {
							// find above polyline
							RowInfo foundBelow = null;
							RowInfo foundAbove = null;
							for (EquationType type : EquationType.values()) {
								RowInfo info = model
										.getInfo(0, row.scale, type);
								if (!info.include) {
									if (foundBelow == null
											&& type.ordinal() < polyline
													.ordinal()) {
										foundBelow = info;
									}
									if (type.ordinal() > polyline.ordinal()) {
										foundAbove = info;
										break;
									}
								}
							}

							RowInfo found = foundAbove != null ? foundAbove
									: foundBelow;
							if (found != null) {
								row.include = false;
								found.include = true;
								found.soundWarning = row.soundWarning;
								found.show = row.show;
								found.textWarning = row.textWarning;
								found.updateType = row.updateType;
								getAdapter().updateChartsLater(false);
								getAdapter().configureGenerator();
								build(model);
							}
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							//
						}

						@Override
						public void mouseDoubleClicked(MouseEvent arg0) {
							//
						}
					});
					label.setBorder(new MarginBorder(0, 10, 0, 10));
					add(label);
					gd = new GridData();
					gd.horizontalAlignment = GridData.CENTER;
					setConstraint(label, gd);
				}
				// status
				addStatus(row);

				// show
				label = new Label(row.show ? "Yes" : "No");
				{
					final Label showLabel = (Label) label;
					label.setCursor(Display.getDefault().getSystemCursor(
							SWT.CURSOR_HAND));
					label.addMouseListener(new MouseListener() {

						@Override
						public void mouseReleased(MouseEvent arg0) {
							row.show = !row.show;
							showLabel.setText(row.show ? "Yes" : "No");
							getAdapter().updateChartsLater(false);
							getAdapter().configureGenerator();
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							//
						}

						@Override
						public void mouseDoubleClicked(MouseEvent arg0) {
							//
						}
					});
					add(label);
					gd = new GridData();
					gd.horizontalAlignment = GridData.FILL;
					gd.verticalAlignment = GridData.FILL;
					setConstraint(label, gd);
				}

				// update type

				{
					label = new Label(row.updateType);
					final Label updateLabel = (Label) label;
					label.setCursor(Display.getDefault().getSystemCursor(
							SWT.CURSOR_HAND));
					label.addMouseListener(new MouseListener() {

						@Override
						public void mouseReleased(MouseEvent arg0) {
							row.updateType = row.updateType == RowInfo.UPDATE_LL_HH ? RowInfo.UPDATE_PRICE
									: RowInfo.UPDATE_LL_HH;
							updateLabel.setText(row.updateType);
							getAdapter().updateChartsLater(false);
							getAdapter().configureGenerator();
						}

						@Override
						public void mousePressed(MouseEvent arg0) {
							//
						}

						@Override
						public void mouseDoubleClicked(MouseEvent arg0) {
							//
						}
					});
					add(label);
					gd = new GridData();
					gd.horizontalAlignment = GridData.FILL;
					gd.verticalAlignment = GridData.FILL;
					setConstraint(label, gd);
				}

				lastScale = row.scale;
			}
		}
		invalidateTree();
	}

	@Override
	protected void paintFigure(Graphics g2) {
		super.paintFigure(g2);
		if (_model != null) {
			g2.setForegroundColor(ColorConstants.lightGray);
			Rectangle b1 = getBounds();
			Rectangle b2;
			g2.drawRectangle(b1.x + 5, b1.y + 5, b1.width - 10, b1.height - 10);

			// vertical lines
			Figure title = (Figure) getChildren().get(0);
			int titleBottom = title.getBounds().bottom();
			for (int i = 0; i < 4; i++) {
				// +1 because the title
				Figure header = (Figure) getChildren().get(i + 1);
				b2 = header.getBounds();
				int x = b2.x + b2.width;
				g2.drawLine(x, titleBottom, x, b1.bottom() - 5);
			}

			// horizontal lines
			int rows = getChildren().size() / 5;
			out.println("rows " + rows);
			g2.drawLine(b1.x + 5, titleBottom, b1.right() - 5, titleBottom);
			for (int i = 0; i < rows - 1; i++) {
				// +1 because the title
				Figure first = (Figure) getChildren().get(i * 5 + 1);
				b2 = first.getBounds();
				g2.drawLine(b1.x + 5, b2.bottom(), b1.right() - 5, b2.bottom());
			}
		}
	}

	private Figure addHeader(String name) {
		Label label = new Label(name);
		label.setFont(SWTResourceManager.getBoldFont(Display.getDefault()
				.getSystemFont()));
		LineBorder outBorder = new LineBorder(ColorConstants.darkGray, 1);
		MarginBorder inBorder = new MarginBorder(5, 10, 5, 10);
		label.setBorder(new CompoundBorder(outBorder, inBorder));
		label.setBackgroundColor(ColorConstants.lightGray);
		label.setOpaque(true);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		add(label);

		setConstraint(label, gd);

		return label;
	}

	private void addStatus(RowInfo row) {
		EventColor status = row.status;
		Figure label;
		if (status == null) {
			label = new Label("");
			add(label);
		} else {
			label = new Panel();
			label.setBorder(new LineBorder(ColorConstants.black));
			updateStatusLabel(label, status);
			add(label);
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.verticalAlignment = GridData.FILL;
			setConstraint(label, gd);
		}
		_rowStatusMap.put(row, label);
	}

	private static void updateStatusLabel(Figure label, EventColor status) {
		Color color = ColorConstants.white;
		switch (status) {
		case GREEN:
			color = ColorConstants.lightGreen;
			break;
		case RED:
			color = ColorConstants.red;
			break;
		}
		label.setBackgroundColor(color);
	}

	public void updateFromEvent(EventArg arg) {
		for (RowInfo row : _model.getRows()) {
			if (row.updateFromEvent(arg)) {
				Figure label = _rowStatusMap.get(row);
				updateStatusLabel(label, row.status);
			}
		}
	}
}
