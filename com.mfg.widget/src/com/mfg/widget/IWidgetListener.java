package com.mfg.widget;

import com.mfg.interfaces.indicator.IIndicator;

public interface IWidgetListener {

	/**
	 * Called when a new state for the indicator widget comes.
	 * 
	 * @param widget
	 *            The widget that has changed state.
	 */
	public void newWidgetState(IIndicator widget);

	/**
	 * Called when the widget is starting new... this is always a new widget,
	 * from zero.
	 * 
	 */
	public void onStarting();

	/**
	 * This is called when the widget stops forever... not a pause. This is a
	 * real end. The next start will be with a new widget (maybe with different
	 * parameters)
	 */
	public void onStop();

	/**
	 * This is called as a chance for the listener to do some cleanup. This
	 * means that this is the last call for the listener. It won't be called any
	 * more!
	 */
	public void onDetach();

	/**
	 * Called when the listener is first attached to the plugin.
	 */
	public void onAttach();

}
