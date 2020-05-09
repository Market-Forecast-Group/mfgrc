package com.mfg.build;

import static java.lang.System.out;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PrintSystemPropertiesAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public PrintSystemPropertiesAction() {
		super("System Properties");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		out.println("\nSystem Properties");
		out.println("--------------------------------------");
		BuildAction.printProperties(System.getProperties());
		out.println();
	}

}
