/*
 * $Id$
 *
 * Created on 21/07/2007
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
package net.fortuna.mstor.delegate;

import java.io.IOException;

import javax.mail.MessagingException;

import net.fortuna.mstor.FolderDelegate;
import net.fortuna.mstor.MessageDelegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 */
public abstract class AbstractFolderDelegate implements FolderDelegate {

    private Log log = LogFactory.getLog(AbstractFolderDelegate.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#getMessage(javax.mail.Message)
     */
    /*
    public final MessageDelegate getMessage(final Message message) {
        if (message != null) {
            try {
                // String messageId = ((MimeMessage) message).getMessageID();
                int messageNumber = message.getMessageNumber();

                MessageDelegate mm = getMessage(messageNumber);
                if (mm != null) {
                    return mm;
                }
                mm = createMessage(messageNumber);
//                mm.setFlags(message.getFlags());
//                mm.setHeaders(message.getAllHeaders());
                // allocate a new UID for the message..
                allocateUid(mm);
                return mm;
            }
            catch (Exception e) {
                log.warn("Error creating metadata - no metadata available", e);
            }
        }
        return null;
    }
    */

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaFolder#allocateUid(net.fortuna.mstor.MetaMessage)
     */
    public final long allocateUid(MessageDelegate message)
        throws UnsupportedOperationException, DelegateException {
        
        long uid = getLastUid() + 1;
        message.setUid(uid);
//        try {
            setLastUid(uid);
//        }
//        catch (DelegateException de) {
//            throw new MessagingException("Error in delegate", de);
//        }
        return uid;
    }
    
    /**
     * @param uid
     * @throws IOException
     */
    protected abstract void setLastUid(long uid) throws UnsupportedOperationException, DelegateException;

    /**
     * @param messageNumber
     * @return
     */
//    protected abstract MessageDelegate getMessage(int messageNumber) throws DelegateException;

    /**
     * @param messageNumber
     * @return
     */
    protected abstract MessageDelegate createMessage(int messageNumber) throws DelegateException;
}
