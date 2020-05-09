package com.mfg.interfaces.trading;

public interface IPositionListener {
	public void positionOpened(PositionOpenedEvent event);

	public void positionClosed(PositionClosedEvent event);
}
