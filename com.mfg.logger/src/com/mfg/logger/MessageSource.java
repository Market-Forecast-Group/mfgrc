package com.mfg.logger;

public class MessageSource {
	private int fID;
	private String name;

	public final static MessageSource Unknown = new MessageSource(0, "Unknown");
	public final static MessageSource Application = new MessageSource(1,
			"Application");
	public final static MessageSource Manual_Strategy = new MessageSource(2,
			"Manual Strategy");
	public final static MessageSource Probabilities_Logger = new MessageSource(
			3, "Probabilities Logger");
	public final static MessageSource Strategies_Logger = new MessageSource(4,
			"Strategies Logger");

	public MessageSource(int aID, String aName) {
		super();
		fID = aID;
		name = aName;
		// LoggerPlugin.getDefault().getLogRecordConverter()
		// .addMessageSource(this);
	}
	public int getID() {
		return fID;
	}
	public void setID(int aID) {
		fID = aID;
	}
	public String getName() {
		return name;
	}
	public void setName(String aName) {
		name = aName;
	}

}
