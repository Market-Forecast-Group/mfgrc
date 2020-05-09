package com.mfg.common;

/**
 * This class models a tick with a real flag which says if the tick is real or
 * not. This is used by many parts of the application because we must know if
 * the tick is real or not!
 * 
 * @author Sergio
 * 
 */
public class RealTick extends Tick {

	@Override
	public RealTick clone() {
		return (RealTick) super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof RealTick)) {
			return false;
		}

		RealTick other = (RealTick) obj;

		boolean res = super.equals(obj);

		if (res == false) {
			return res;
		}

		if (fIsReal != other.fIsReal)
			return false;

		return true;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1847422933081353402L;

	public RealTick() {

	}

	public RealTick(Tick aTick, boolean real) {
		super(aTick.getPhysicalTime(), aTick.getPrice(), aTick.getVolume());
		fIsReal = real;
	}

	public RealTick(long physicalTime1, int price1, boolean real) {
		super(physicalTime1, price1);
		fIsReal = real;
	}

	public RealTick(DFSQuote quote) {
		super(quote);
		fIsReal = quote.tick.getReal();
	}

	public RealTick(long physicalTime1, int price1, boolean isReal, int volume) {
		super(physicalTime1, price1, volume);
		fIsReal = isReal;
	}

	protected boolean fIsReal;

	public boolean getReal() {
		return fIsReal;
	}

	@Override
	public String toString() {
		return super.toString() + " real? " + fIsReal;
	}

	public void setReal(boolean b) {
		fIsReal = b;
	}

}
