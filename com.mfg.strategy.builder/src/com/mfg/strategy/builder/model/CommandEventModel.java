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

import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.ui.UIPlugin;

public abstract class CommandEventModel extends SimpleEventModel implements IConfirmEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean _requiresConfirmation;

	private boolean _playSound = true;

	private String _soundPath = UIPlugin.SOUND_DING;

	private boolean _speak;


	public CommandEventModel() {
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
		return _speak;
	}


	@Override
	public void setSpeak(boolean speak) {
		if (_speak != speak) {
			_speak = speak;
			firePropertyChange(PropertiesID.PROPERTY_SPEAK, null, Boolean.valueOf(speak));
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
