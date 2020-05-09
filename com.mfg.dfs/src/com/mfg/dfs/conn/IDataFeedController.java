package com.mfg.dfs.conn;

/**
 * This interface is used to have a controller which can give some commands to
 * the data feed.
 * 
 * <p>
 * Of course a simulated data feed
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDataFeedController {

	public void play();

	public void pause();

	public void step();

	/**
	 * Plays the sequence at the constant interval between ticks. Zero means to
	 * replay at they normal speed (variable).
	 * 
	 * @param interval
	 *            an interval in milliseconds, if zero the speed return
	 *            variable.
	 */
	public void playAtConstantInterval(long interval);

	/**
	 * Stops every subscription, it is roughly equivalent to a
	 * {@link #disconnect()} followed immediately by a {@link #connect()}
	 */
	public void stop();

	/**
	 * change the real time replay factor.
	 * <p>
	 * Value less then 1 are slower, greater than 1 are faster. 1 is normal
	 * speed. The values are linear, in the sense that 0.5 is two times slower,
	 * 0.25 is four times slower, 2 is two times faster, 4 is four times faster
	 * and so on.
	 * <p>
	 * zero means to stop completely, as if you have called the {@link #pause()}
	 * method
	 * 
	 * @param aFactor
	 */
	public void replayFactor(double aFactor);

	/**
	 * simulates a disconnection. The real time ticks are lost.
	 */
	public void disconnect();

	/**
	 * Simulates to reconnect.
	 */
	public void connect();

}
