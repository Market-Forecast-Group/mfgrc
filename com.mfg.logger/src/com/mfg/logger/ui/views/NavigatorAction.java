package com.mfg.logger.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class NavigatorAction extends Action {

	private AbstractLoggerViewControl control;
	private String text;

	public NavigatorAction() {
		super();
	}

	public NavigatorAction(String aText) {
		super(aText);
		text = aText;
		setToolTipText(getText());
	}

	public NavigatorAction(String aText, ImageDescriptor aImage) {
		super(aText, aImage);
	}

	public NavigatorAction(String aText, int aStyle) {
		super(aText, aStyle);
	}

	public AbstractLoggerViewControl getControl() {
		return control;
	}

	public void setControl(AbstractLoggerViewControl aControl) {
		control = aControl;
	}

	public void setEventType(String type) {
		control.setEvent(type);
		updateText(type);
	}

	public void updateText(String type) {
		setText(text + " (" + type + ")");
		setToolTipText(getText());
	}

	public String getEventType() {
		return control.getEvent();
	}

}