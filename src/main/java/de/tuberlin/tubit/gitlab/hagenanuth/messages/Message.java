package de.tuberlin.tubit.gitlab.hagenanuth.messages;

public abstract class Message {

	private boolean internal;
	private int payload;

	public Message(int payload, boolean internal) {
		this.payload = payload;
		this.internal = internal;
	}

	public boolean isInternal() {
		return internal;
	}

	public int getPayload() {
		return payload;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}

	public void setInternal() {
		this.internal = true;
	}
}
