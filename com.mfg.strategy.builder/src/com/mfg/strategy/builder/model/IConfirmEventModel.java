
package com.mfg.strategy.builder.model;

public interface IConfirmEventModel {
	public boolean isRequiresConfirmation();


	public void setRequiresConfirmation(boolean requiresConfirmation);


	public boolean isPlaySound();


	public void setPlaySound(boolean playSound);


	public String getSoundPath();


	public void setSoundPath(String soundPath);


	public boolean isSpeak();


	public void setSpeak(boolean speak);
}
