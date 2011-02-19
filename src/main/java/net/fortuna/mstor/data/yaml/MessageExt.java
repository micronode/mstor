/**
 * Copyright (c) 2011, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

    private InternetHeaders internetHeaders;
    
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
        this.flags = new Flags(flags);
    }

    /**
     * @return the headers
     */
    public final InternetHeaders getInternetHeaders() {
        return internetHeaders;
    }

    /**
     * @param headers the headers to set
     */
    public final void setInternetHeaders(InternetHeaders headers) {
        this.internetHeaders = headers;
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
