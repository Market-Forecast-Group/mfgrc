package com.marketforescastgroup.logger;


public class Log {

	private String date;

	private String message;

	private MessageType messageType;

	public Log(final String date1, final String message1, final MessageType type) {

		this.date = date1;

		this.message = message1;

		this.messageType = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date1) {
		this.date = date1;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String symbol) {
		this.message = symbol;
	}

	public MessageType getMessageType() {
		return messageType;
	}
}
