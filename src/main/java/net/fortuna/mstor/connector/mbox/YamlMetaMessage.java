/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.mstor.connector.mbox;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.internet.InternetHeaders;

import net.fortuna.mstor.connector.AbstractMessageDelegate;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.yaml.MessageExt;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 03/05/2008
 * </pre>
 * 
 *
 */
public class YamlMetaMessage extends AbstractMessageDelegate {

    private MessageExt messageExt;
    
    private YamlMetaFolder folder;
    
    /**
     * @param messageNumber
     * @param folder
     */
    public YamlMetaMessage(final MessageExt messageExt, final YamlMetaFolder folder) {
        this.messageExt = messageExt;
        this.folder = folder;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getFlags()
     */
    public Flags getFlags() {
        return messageExt.getFlags();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getForwarded()
     */
    public Date getForwarded() {
        return messageExt.getForwarded();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getHeaders()
     */
    public InternetHeaders getHeaders() {
        return messageExt.getInternetHeaders();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getMessageNumber()
     */
    public int getMessageNumber() {
        return messageExt.getMessageNumber();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getReceived()
     */
    public Date getReceived() {
        return messageExt.getReceived();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getReplied()
     */
    public Date getReplied() {
        return messageExt.getReplied();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getUid()
     */
    public long getUid() {
        return messageExt.getUid();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#isExpunged()
     */
    public boolean isExpunged() {
        return messageExt.isExpunged();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#saveChanges()
     */
    public void saveChanges() throws DelegateException {
        folder.save();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setExpunged(boolean)
     */
    public void setExpunged(boolean expunged) {
        messageExt.setExpunged(expunged);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setFlags(javax.mail.Flags)
     */
    public void setFlags(Flags flags) {
        messageExt.setFlags(flags);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setForwarded(java.util.Date)
     */
    public void setForwarded(Date forwarded) {
        messageExt.setForwarded(forwarded);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setHeaders(java.util.Enumeration)
     */
    public void setHeaders(Enumeration<Header> headers) {
        InternetHeaders iHeaders = new InternetHeaders();
        while (headers.hasMoreElements()) {
            Header header = headers.nextElement();
            iHeaders.addHeader(header.getName(), header.getValue());
        }
        messageExt.setInternetHeaders(iHeaders);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setReceived(java.util.Date)
     */
    public void setReceived(Date received) {
        messageExt.setReceived(received);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setReplied(java.util.Date)
     */
    public void setReplied(Date replied) {
        messageExt.setReplied(replied);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setUid(long)
     */
    public void setUid(long uid) {
        messageExt.setUid(uid);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getInReplyTo()
     */
    public MessageDelegate getInReplyTo() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Message references not supported");
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getReferences()
     */
    public List<? extends MessageDelegate> getReferences() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Message references not supported");
    }

}
