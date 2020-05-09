package com.mfg.symbols.trading.ui.views;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Panel;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.widgets.figures.MeterFigure;
import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TestNebulaView extends ViewPart {

	public static final String ID = "com.mfg.symbols.trading.ui.views.TestNebulaView"; //$NON-NLS-1$

	public TestNebulaView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		Canvas canvas = new Canvas(container, SWT.DOUBLE_BUFFERED);

		final LightweightSystem lws = new LightweightSystem(canvas);

		final GaugeFigure fig1 = new GaugeFigure();

		XYGraphMediaFactory factory = XYGraphMediaFactory.getInstance();

		Panel panel = new Panel();
		panel.setBackgroundColor(factory.getColor(255, 255, 255));

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayoutManager(layout);

		fig1.setBackgroundColor(factory.getColor(0, 0, 0));
		fig1.setForegroundColor(factory.getColor(255, 255, 255));

		fig1.setRange(0, 100);
		fig1.setLoLevel(20);
		fig1.setLoloLevel(5);
		fig1.setHiLevel(65);
		fig1.setHihiLevel(80);
		fig1.setMajorTickMarkStepHint(50);

		fig1.setLoloColor(factory.getColor(0, 0, 255));
		fig1.setShowMarkers(true);
		fig1.setShowHi(false);
		fig1.setShowHihi(false);
		panel.add(fig1);

		GaugeFigure fig2 = new GaugeFigure();
		fig2.setBackgroundColor(factory.getColor(140, 140, 140));
		panel.add(fig2);

		MeterFigure fig3 = new MeterFigure();
		fig3.setBackgroundColor(factory.getColor(0, 0, 0));
		fig3.setForegroundColor(factory.getColor(255, 255, 255));
		panel.add(fig3);

		MeterFigure fig4 = new MeterFigure();
		fig4.setLoColor(factory.getColor(0, 0, 255));
		fig4.setShowMarkers(true);
		fig4.setShowLo(true);
		panel.add(fig4);

		TankFigure fig5 = new TankFigure();
		fig5.setPreferredSize(200, 300);
		panel.add(fig5);

		TankFigure fig6 = new TankFigure();
		fig6.setPreferredSize(200, 300);
		fig6.setFillColor(factory.getColor(0, 255, 0));
		panel.add(fig6);

		lws.setContents(panel);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		@SuppressWarnings("unused")
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
