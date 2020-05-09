package com.mfg.common;

/**
 * This is the common class for all the Bar Automatons.
 * 
 * <p>
 * In our project there are the {@linkplain TimeBarAutomaton} and the
 * {@linkplain RangeBarAutomaton}, which are used to create time and range bar,
 * respectively, from ticks.
 * 
 * <p>
 * The general contract for the automaton is to return null if a new forming bar
 * has not being created.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BarAutomaton {

	/**
	 * This is the forming bar, the forming bar can be a range or a time bar, it
	 * does not matter.
	 * <p>
	 * From the point of view of the automaton all bars are equals.
	 */
	protected Bar _formingBar = null;

	/**
	 * This is the last completed bar.
	 */
	protected Bar _lastCompleteBar = null;

	/**
	 * returns the forming bar. Convenience method which is give to let users
	 * gets the most recent formed bar. This bar is changed when the automaton
	 * gets a new tick.
	 * 
	 * So to get the last complete bar users should call the
	 * {@link #getLastCompleteBar()} method.
	 * 
	 * @return the forming bar. This is used to have the possibility for the
	 *         user to forget the last forming bar reference.
	 */
	public final Bar getFormingBar() {
		return this._formingBar;
	}

	/**
	 * gets the last formed bar.
	 * <p>
	 * returns the last completed bar, of course this happens when I have
	 * received at least one tick of the <b>new</b> bar.
	 * <p>
	 * This delay is inevitable, because I don't know how many ticks I receive.
	 * I could have a strange case in which I receive N ticks at the same
	 * milliseconds, the same millisecond of the last time of this duration...
	 * so... I have to wait until a tick of the new millisecond.
	 * 
	 * @return the last formed bar. This bar <b>never</b> changes, but it can be
	 *         substituted
	 */
	public final Bar getLastCompleteBar() {
		return _lastCompleteBar;
	}

	protected void _createNewBar(Tick tk) {
		this._formingBar = new Bar();
		_formingBar.setTime(tk.getPhysicalTime());
		// the starting candle has all the prices equals.
		this._formingBar.setOpen(tk.getPrice());
		this._formingBar.setLow(tk.getPrice());
		this._formingBar.setHigh(tk.getPrice());
		this._formingBar.setClose(tk.getPrice());
		this._formingBar.setInitialVolume(tk.getVolume());
	}

	/**
	 * Accepts a new tick which is used to create the bar.
	 * 
	 * <p>
	 * returns a bar if a new bar has been created, null otherwise (the most
	 * recent created bar is modified IN PLACE).
	 * 
	 * <p>
	 * users of this class should store the last reference of the bar in order
	 * to look at the difference.
	 * 
	 * @param tk
	 *            The tick to be accepted.
	 * 
	 * @return a bar if it has been formed, null otherwise.
	 * 
	 */
	public abstract Bar accept(Tick tk);

}
