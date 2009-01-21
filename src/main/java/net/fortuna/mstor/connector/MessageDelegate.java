/*
 * $Id$
 *
 * Created on 30/07/2007
 *
 * Copyright (c) 2007, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.connector;

import java.util.Date;
import java.util.Enumeration;

import javax.mail.Flags;
import javax.mail.internet.InternetHeaders;

import net.fortuna.mstor.MStorMessage;

/**
 * Implementors support delegation of specific functions from {@link MStorMessage}.
 * @author Ben
 *
 */
public interface MessageDelegate {

    /**
     * @return Returns the expunged.
     */
    boolean isExpunged();

    /**
     * @param expunged The expunged to set.
     */
    void setExpunged(boolean expunged);

    /**
     * @return headers saved in delegate
     */
    InternetHeaders getHeaders();

    /**
     * Saves headers to delegate. Implementations may choose to only save a subset of the specified
     * headers.
     *
     * @param headers headers to save to delegate
     */
    void setHeaders(InternetHeaders headers);

    /**
     * @param headers
     */
    void setHeaders(Enumeration headers);

    /**
     * @return Returns the flags.
     */
    Flags getFlags();

    /**
     * @param flags The flags to set.
     */
    void setFlags(Flags flags);

    /**
     * @return Returns the forwarded.
     */
    Date getForwarded();

    /**
     * @param forwarded The forwarded to set.
     */
    void setForwarded(Date forwarded);

    /**
     * Returns the 1-based number of the message the delegate is associated with. If a message
     * number is not set it is assumed the message does not belong to a folder and thus has a
     * message number of zero (0).
     *
     * @return the message number associated with this delegate, or zero if no message number is
     *         identified
     */
    int getMessageNumber();

    /**
     * @return Returns the received.
     */
    Date getReceived();

    /**
     * @param received The received to set.
     */
    void setReceived(Date received);

    /**
     * @return Returns the replied.
     */
    Date getReplied();

    /**
     * @param replied The replied to set.
     */
    void setReplied(Date replied);

    /**
     * Returns the UID associated with the message.
     *
     * @return a long representation of a UID, or -1 if no UID is assigned
     */
    long getUid();

    /**
     * Sets the UID associated with the message.
     *
     * @param uid
     */
    void setUid(long uid);

    /**
     * Persist unsaved changes.
     */
    void saveChanges() throws DelegateException;
}
