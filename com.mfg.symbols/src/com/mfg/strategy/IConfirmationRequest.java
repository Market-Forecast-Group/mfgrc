package com.mfg.strategy;

public interface IConfirmationRequest {

	public abstract boolean isConfirmed();

	public abstract void setConfirmed(boolean aConfirmed);

	public abstract void confirm();

	public abstract boolean isCanceled();

	public abstract void setCanceled(boolean aCanceled);

	public abstract void cancel();

}