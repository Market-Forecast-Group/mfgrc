//
//package com.mfg.strategy.manual.ui.views;
//
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IToolBarManager;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.MouseAdapter;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.ui.part.ViewPart;
//
//import com.mfg.interfaces.trading.ITradeConfiguration;
//import com.mfg.strategy.manual.ManualSymbolsPlugin;
//import com.mfg.utils.ui.UIHelper;
//
//public class ManualStrategyDashboardView extends ViewPart {
//
//	public static final String ID = "com.mfg.strategy.manual.ui.views.ManualStrategyDashboardView";
//
//	// private Dashboard dashboard;
//
//	private Action showInChart;
//
//	private Menu contextMenu;
//
//	private Action showInManualStrategy;
//
//
//	@Override
//	public void createPartControl(Composite parent) {
//		parent.setLayout(new FillLayout());
//		dashboard = new Dashboard(parent);
//
//		createActions();
//		fillActionBar(getViewSite().getActionBars().getToolBarManager());
//
//		registerDashboardListeners();
//	}
//
//
//	private void registerDashboardListeners() {
//		dashboard.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//				if (e.button == 3 && dashboard.getSelectedLaunch() != null) {
//					dashboard.setMenu(createContextMenu());
//				}
//			}
//
//		});
//	}
//
//
//	protected Menu createContextMenu() {
//		if (contextMenu != null) {
//			contextMenu.dispose();
//		}
//
//		contextMenu = new Menu(dashboard.getShell(), SWT.POP_UP);
//		UIHelper.addItem(contextMenu, showInChart);
//		UIHelper.addItem(contextMenu, showInManualStrategy);
//
//		return contextMenu;
//	}
//
//
//	private void createActions() {
//		showInChart = new AbstractOpenChartMenuAction() {
//			@Override
//			public void run() {
//				super.run();
//				dashboard.updateDashboard();
//			}
//
//
//			@Override
//			public ITradeConfiguration getLaunch() {
//				return dashboard.getSelectedLaunch();
//			}
//		};
//		showInManualStrategy = new Action("Show in Manual Strategy", ManualSymbolsPlugin.getBundledImageDescriptor("/icons/ms-icon.png")) {
//		};
//	}
//
//
//	private void fillActionBar(IToolBarManager bar) {
//		bar.add(showInChart);
//		bar.add(showInManualStrategy);
//	}
//
//
//	@Override
//	public void setFocus() {
//		dashboard.setFocus();
//	}
// }
