package net.fortuna.mstor.data.yaml;

import java.util.Date;

import javax.mail.Flags;
import javax.mail.internet.InternetHeaders;

/**
 * @author Ben
 *
 */
public class MessageExt {

    private int messageNumber;

    private Date received;

    private Date forwarded;

    private Date replied;

    private boolean expunged;

    private Flags flags;

    private InternetHeaders headers;

    private long uid;

    /**
     * Default constructor.
     */
    public MessageExt() {
    }

    /**
     * @param messageNumber
     */
    public MessageExt(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    /**
     * @return the messageNumber
     */
    public final int getMessageNumber() {
        return messageNumber;
    }

    /**
     * @param messageNumber the messageNumber to set
     */
    public final void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    /**
     * @return the received
     */
    public final Date getReceived() {
        return received;
    }

    /**
     * @param received the received to set
     */
    public final void setReceived(Date received) {
        this.received = received;
    }

    /**
     * @return the forwarded
     */
    public final Date getForwarded() {
        return forwarded;
    }

    /**
     * @param forwarded the forwarded to set
     */
    public final void setForwarded(Date forwarded) {
        this.forwarded = forwarded;
    }

    /**
     * @return the replied
     */
    public final Date getReplied() {
        return replied;
    }

    /**
     * @param replied the replied to set
     */
    public final void setReplied(Date replied) {
        this.replied = replied;
    }

    /**
     * @return the expunged
     */
    public final boolean isExpunged() {
        return expunged;
    }

    /**
     * @param expunged the expunged to set
     */
    public final void setExpunged(boolean expunged) {
        this.expunged = expunged;
    }

    /**
     * @return the flags
     */
    public final Flags getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public final void setFlags(Flags flags) {
        this.flags = flags;
    }

    /**
     * @return the headers
     */
    public final InternetHeaders getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public final void setHeaders(InternetHeaders headers) {
        this.headers = headers;
    }

    /**
     * @return the uid
     */
    public final long getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public final void setUid(long uid) {
        this.uid = uid;
    }

}
