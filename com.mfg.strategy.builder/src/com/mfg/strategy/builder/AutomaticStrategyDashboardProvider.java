
package com.mfg.strategy.builder;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Pattern;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.ITradeMessage;
import com.mfg.strategy.automatic.EventStrategyMessage;
import com.mfg.strategy.automatic.EventsPatternStrategy;
import com.mfg.strategy.automatic.eventPatterns.EventAtomCommand;
import com.mfg.strategy.automatic.eventPatterns.EventAtomExit;
import com.mfg.strategy.automatic.eventPatterns.EventCommandContainer;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.EventGroup;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.utils.Utils;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.IDashboardWidgetProvider;

public class AutomaticStrategyDashboardProvider implements IDashboardWidgetProvider {
	private TradingConfiguration _configuration;


	public AutomaticStrategyDashboardProvider(TradingConfiguration conf) {
		_configuration = conf;
	}


	@Override
	public IFigure createFigure() {
		com.mfg.utils.Utils.debug_id(3454566, "Start renderer");
		AutomaticStrategySettings settings = (AutomaticStrategySettings) _configuration.getInfo().getStrategySettings(AutomaticStrategyFactory.ID);
		StrategyInfo info = StrategyBuilderPlugin.getDefault().getStrategiesStorage().findById(settings.getStrategyInfoId());
		EventsPatternStrategy strategy = StrategyBuilderPlugin.createStrategyFromInfo(info, null);

		IFigure fig = buildFigure(strategy.getEventPattern());
		activeTree(fig);
		com.mfg.utils.Utils.debug_id(3454568, "End renderer");
		return fig;
	}


	private static IFigure buildFigure(EventGeneral node) {
		com.mfg.utils.Utils.debug_id(3454567, "Render " + node + " uniqueID=" + node.getNodeID());
		INode result;
		NodeLabel label = new NodeLabel(node);
		Color color;

		if (node instanceof EventGroup) {
			color = ColorConstants.orange;
			result = createTree(color, label, node);
		} else if (node instanceof EventCommandContainer) {
			color = ColorConstants.lightBlue;
			result = createTree(color, label, node);
		} else {
			label.setOpaque(true);
			color = node instanceof EventAtomCommand ? ColorConstants.red : ColorConstants.green;
			result = label;
		}

		label.setOrigColor(color);
		label.setText(node.getLabel());
		result.setBorder(createNodeBorder(color));
		return result;
	}


	private static CompoundBorder createNodeBorder(Color color) {
		return new CompoundBorder(new LineBorder(Utils.darker(color, 0.7)), new MarginBorder(5));
	}


	private static INode createTree(Color color, NodeLabel label, EventGeneral node) {
		Panel bottomPanel = new Panel();
		bottomPanel.setOpaque(false);
		FlowLayout layout = new FlowLayout(true);

		bottomPanel.setLayoutManager(layout);
		for (EventGeneral child : node.getChildren()) {
			IFigure fig = buildFigure(child);
			bottomPanel.add(fig);
		}

		NodePanel panel = new NodePanel(label, node);
		panel.setOrigColor(color);
		BorderLayout layout2 = new BorderLayout();
		layout2.setVerticalSpacing(5);
		panel.setLayoutManager(layout2);
		panel.add(label, BorderLayout.TOP);
		panel.add(bottomPanel, BorderLayout.CENTER);
		return panel;
	}

	interface INode extends IFigure {
		public int getNodeId();


		public EventGeneral getEvent();


		public Color getOrigColor();


		public void setOrigColor(Color color);


		public boolean isActive();


		public void setActive(boolean active);
	}

	static class NodePanel extends Panel implements INode {
		private int _nodeId;
		private Color _origColor;
		private boolean _active;
		private EventGeneral _event;
		private NodeLabel _label;


		public NodePanel(NodeLabel label, EventGeneral event) {
			_nodeId = event.getNodeID();
			_event = event;
			_label = label;
		}


		@Override
		public EventGeneral getEvent() {
			return _event;
		}


		@Override
		public boolean isActive() {
			return _active;
		}


		@Override
		public void setActive(boolean active) {
			_active = active;
			_label.setActive(active);
		}


		@Override
		public Color getOrigColor() {
			return _origColor;
		}


		@Override
		public void setOrigColor(Color origColor) {
			_origColor = origColor;
		}


		@Override
		public int getNodeId() {
			return _nodeId;
		}


		@Override
		public void paint(Graphics aGraphics) {
			if (isActive()) {
				Rectangle rect = getBounds();
				Device currentd = aGraphics.getFont().getDevice();
				aGraphics.setBackgroundPattern(new Pattern(currentd, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
						_origColor, ColorConstants.white));
			}
			super.paint(aGraphics);
		}


		@Override
		public Color getLocalBackgroundColor() {
			return isActive() ? null : getBackgroundColor();
		}


		@Override
		public Color getLocalForegroundColor() {
			return isActive() ? ColorConstants.black : ColorConstants.lightGray;
		}
	}

	static class NodeLabel extends Label implements INode {
		private int _nodeId;
		private Color _origColor;
		private boolean _active;
		private EventGeneral _event;


		public NodeLabel(EventGeneral event) {
			super();
			_nodeId = event.getNodeID();
			_event = event;
		}


		@Override
		public EventGeneral getEvent() {
			return _event;
		}


		@Override
		public boolean isActive() {
			return _active;
		}


		@Override
		public void setActive(boolean active) {
			_active = active;
		}


		@Override
		public Color getOrigColor() {
			return _origColor;
		}


		@Override
		public void setOrigColor(Color origColor) {
			_origColor = origColor;
		}


		@Override
		public int getNodeId() {
			return _nodeId;
		}


		public void setNodeId(int nodeId) {
			_nodeId = nodeId;
		}


		@Override
		public void paint(Graphics aGraphics) {
			if (isActive()) {
				Rectangle rect = getBounds();
				Device currentd = aGraphics.getFont().getDevice();
				aGraphics.setBackgroundPattern(new Pattern(currentd, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
						_origColor, ColorConstants.white));
			}
			super.paint(aGraphics);
		}


		@Override
		public Color getLocalBackgroundColor() {
			return isActive() ? null : ColorConstants.black;
		}


		@Override
		public Color getLocalForegroundColor() {
			return isActive() ? ColorConstants.black : ColorConstants.white;
		}
	}


	@Override
	public void repaint(final IFigure fig, ITradeMessage msg, boolean closedAcount, EAccountRouting routing) {
		if (msg != null) {

			if (msg instanceof EventStrategyMessage) {
				EventStrategyMessage msg2 = (EventStrategyMessage) msg;
				EventGeneral event = msg2.getPatternEvent();

				// Giulio does not want to highlight the CND CMD else only the entry node.
				if (event instanceof EventCommandContainer) {
					event = ((EventCommandContainer) event).getCommand();
				}

				com.mfg.utils.Utils.debug_id(9874670, routing + " Arrive event " + msg.getEvent());
				if (msg2.getAccountRouting() == routing) {
					int nodeID = event.getNodeID();
					com.mfg.utils.Utils.debug_id(9874671, routing + " Activate node " + nodeID);
					activateNodes(fig, nodeID);
				}
			} else if (closedAcount) {
				if (isActiveExitNode(fig)) {
					com.mfg.utils.Utils.debug_id(9874673, routing + " Is active an exit node: desactivate nodes.");
					desactivateNodes(fig);
				}
			}
		}
		repaint(fig, closedAcount);
		fig.invalidateTree();
	}


	private void desactivateNodes(IFigure fig) {
		if (fig instanceof INode) {
			((INode) fig).setActive(false);
		}
		for (Object obj : fig.getChildren()) {
			desactivateNodes((IFigure) obj);
		}
	}


	private boolean isActiveExitNode(IFigure fig) {
		if (fig instanceof INode) {
			INode node = (INode) fig;
			EventGeneral event = node.getEvent();
			if (event instanceof EventAtomExit) {
				return node.isActive();
			}
		}
		for (Object obj : fig.getChildren()) {
			if (isActiveExitNode((IFigure) obj)) {
				return true;
			}
		}
		return false;
	}


	private static void repaint(IFigure fig, boolean closedAccount) {
		if (fig instanceof INode) {
			INode node = (INode) fig;
			if (node.isActive()) {
				if (fig.isOpaque()) {
					fig.setBorder(createNodeBorder(node.getOrigColor()));
				}
			} else {
				if (fig.isOpaque()) {
					fig.setBorder(createNodeBorder(ColorConstants.lightGray));
				}
			}
		}

		for (Object obj : fig.getChildren()) {
			repaint((IFigure) obj, closedAccount);
		}
	}


	protected static boolean activateNodes(IFigure fig, int nodeID) {
		boolean active = false;
		INode node = null;
		if (fig instanceof INode) {
			node = (INode) fig;
			active = node.getNodeId() == nodeID;
		}
		if (active) {
			activeTree(fig);
		} else {
			for (Object obj : fig.getChildren()) {
				boolean active2 = activateNodes((IFigure) obj, nodeID);
				active = active || active2;
			}
		}
		if (node != null) {
			node.setActive(active);
		}
		return active;
	}


	private static void activeTree(IFigure fig) {
		if (fig instanceof INode) {
			((INode) fig).setActive(true);
		}
		for (Object obj : fig.getChildren()) {
			activeTree((IFigure) obj);
		}
	}
}
