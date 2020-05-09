package com.mfg.widget;

import com.mfg.interfaces.indicator.IIndicator;

public class WidgetAdapter implements IWidgetListener {

	@Override
	public void newWidgetState(IIndicator widget) {
		System.out.println("New state for the widget");
//		if (widget.isThereANewPivot(1)) {
//			Pivot pv = widget.getLastPivot();
//			System.out.println("new pivot at level 1 " + pv);
//		}
	}

	@Override
	public void onStarting() {
		System.out.println("The widget is starting");

	}

	@Override
	public void onStop() {
		System.out.println("The widget has stopped");
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAttach() {
		// TODO Auto-generated method stub

	}

}
