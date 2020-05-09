package com.mfg.common;

/**
 * A queue tick is a tick with a fake time. All the ticks are then put into a
 * queue.
 * 
 * @author Sergio
 * 
 */
public class QueueTick extends RealTick {
	/**
     * 
     */
	private static final long serialVersionUID = 2815084671041361118L;
	private int fakeTime;

	/**
	 * Simple pod constructor. The index is simply the fake time.
	 */
	public QueueTick(Tick tick1, int fakeTime1, boolean real1) {
		super(tick1, real1);
		this.fakeTime = fakeTime1;
	}

	/**
	 * Builds a QueueTick object from the primitive data. This is useful when
	 * you don't have a tick object to wrap or the tick object must be massaged
	 * (especially in the physical time)
	 * 
	 * @param physicalTime1
	 *            The physical time of this object
	 * 
	 * @param aFakeTime
	 *            The Fake time (usually QueueTicks are in order)
	 * 
	 * @param aPrice
	 *            the price of this queue tick (as an integer)
	 * 
	 * @param real
	 *            if it is real or not, not real ticks are displayed in the
	 *            chart as red points.
	 * @param aVolume
	 */
	public QueueTick(long physicalTime1, int aFakeTime, int aPrice,
			boolean real, int aVolume) {
		super(physicalTime1, aPrice, real, aVolume);
		this.fakeTime = aFakeTime;
	}

	public QueueTick() {
	}

	public QueueTick(RealTick temp, int aFakeTime) {
		super(temp, temp.fIsReal);
		this.fakeTime = aFakeTime;
	}

	@Override
	/**
	 * A simple shallow cloning is sufficient.
	 */
	public QueueTick clone() {
		QueueTick qt = (QueueTick) super.clone();
		return qt;
	}

	@Override
	public String toString() {
		return "qt-> i= " + getFakeTime() + " tk " + super.toString();
	}

	/**
	 * This is the index of the tick in the tcb of the subscription.
	 */
	public int getFakeTime() {
		return fakeTime;
	}

	/**
	 * Simply sets the fields of this QueueTick from outside. This is really a
	 * transitional method used for the purpose of transition to MDB
	 * 
	 * @param fakeTime1
	 * @param physTime
	 * @param priceReal
	 * @param isReal
	 */
	public void set(int fakeTime1, long physTime, int priceReal, boolean isReal) {
		this.fakeTime = fakeTime1;
		this.physicalTime = physTime;
		this.price = priceReal;
		this.fIsReal = isReal;
	}

}
