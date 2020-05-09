/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model;

import com.mfg.strategy.automatic.eventPatterns.EventAtomTH;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.ui.UIPlugin;

public class THEventModel extends ScaledEventModel implements IConfirmEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5721332646191851598L;

	private int thToSkip = 0;

	private boolean _requiresConfirmation;

	private boolean _playSound = true;

	private String _soundPath = UIPlugin.SOUND_CHIME_DOWN;

	private boolean _speakSound;


	public THEventModel() {
		super();
		_requiresConfirmation = false;
	}


	@Override
	public boolean isRequiresConfirmation() {
		return _requiresConfirmation;
	}


	@Override
	public void setRequiresConfirmation(boolean requiresConfirmation) {
		if (_requiresConfirmation != requiresConfirmation) {
			_requiresConfirmation = requiresConfirmation;
			firePropertyChange(PropertiesID.PROPERTY_REQUIRES_CONFIMATION, null, Boolean.valueOf(requiresConfirmation));
		}
	}


	@Override
	public String getLabel() {
		return "TH{scale=" + widgetScale + ((thToSkip == 0) ? "" : (", skip " + thToSkip)) + "}";
	}


	/**
	 * @return the thToSkip
	 */
	public int getThToSkip() {
		return thToSkip;
	}


	/**
	 * @param aThToSkip
	 *            the thToSkip to set
	 */
	public void setThToSkip(int aThToSkip) {
		if (aThToSkip != thToSkip) {
			thToSkip = aThToSkip;
			firePropertyChange(PropertiesID.PROPERTY_TH2SK, null, Integer.valueOf(aThToSkip));
		}
	}


	@Override
	public void setLimitedToSwing0(boolean aLimitedToSwing0) {
		throw new UnsupportedOperationException("");
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomTH res = new EventAtomTH();
		res.setWidgetScale(getWidgetScale());
		res.setTHtoSkip(getThToSkip());
		res.setRequiresConfirmation(isRequiresConfirmation());
		res.setPlaySound(isPlaySound());
		res.setSoundPath(getSoundPath());
		res.setSpeak(isSpeak());
		return res;
	}


	@Override
	public boolean isPlaySound() {
		return _playSound;
	}


	@Override
	public void setPlaySound(boolean playSound) {
		if (_playSound != playSound) {
			_playSound = playSound;
			firePropertyChange(PropertiesID.PROPERTY_PLAY_SOUND, null, Boolean.valueOf(playSound));
		}
	}
	
	@Override
	public boolean isSpeak() {
		return _speakSound;
	}
	@Override
	public void setSpeak(boolean speakSound) {
		if (_speakSound != speakSound) {
			_speakSound = speakSound;
			firePropertyChange(PropertiesID.PROPERTY_SPEAK, null, Boolean.valueOf(speakSound));
		}
	}


	@Override
	public String getSoundPath() {
		return _soundPath;
	}


	@Override
	public void setSoundPath(String soundPath) {
		if (_soundPath != soundPath) {
			_soundPath = soundPath;
			firePropertyChange(PropertiesID.PROPERTY_SOUND_PATH, null, soundPath);
		}
	}

}
