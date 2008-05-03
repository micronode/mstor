package net.fortuna.mstor.data.yaml;

import java.util.List;

public class FolderExt {

	private List messages;
	
	private long lastUid;
	
	private long uidValidity;

	/**
	 * @return the lastUid
	 */
	public final long getLastUid() {
		return lastUid;
	}

	/**
	 * @param lastUid the lastUid to set
	 */
	public final void setLastUid(long lastUid) {
		this.lastUid = lastUid;
	}

	/**
	 * @return the messages
	 */
	public final List getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public final void setMessages(List messages) {
		this.messages = messages;
	}

	/**
	 * @return the uidValidity
	 */
	public final long getUidValidity() {
		return uidValidity;
	}

	/**
	 * @param uidValidity the uidValidity to set
	 */
	public final void setUidValidity(long uidValidity) {
		this.uidValidity = uidValidity;
	}
	
}
