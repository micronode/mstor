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
package net.fortuna.mstor.connector.mbox;

import java.io.File;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;

import net.fortuna.mstor.MStorFolder;
import net.fortuna.mstor.MStorStore;
import net.fortuna.mstor.connector.AbstractProtocolHandler;
import net.fortuna.mstor.util.CapabilityHints;

/**
 * An mbox-based protocol handler.
 * 
 * An mbox-based store would be specified with a url name as follows:
 * 
 * <pre>mstor:/home/user/mail/</pre>
 * 
 * Metadata is enabled by default,
 * however it may be disabled by specifying the following session property:
 * 
 * <pre>
 * mstor.meta.enabled = false
 * </pre>
 * 
 * @author Ben
 *
 */
public class MboxHandler extends AbstractProtocolHandler {
    
    private boolean metaEnabled;
    
    /**
     * @param url
     */
    public MboxHandler(URLName url, MStorStore store, Session session) {
        super(url, store);
//        this.session = session;
        
        // enable metadata by default..
        String metadataStrategy = session.getProperties().getProperty(
                CapabilityHints.KEY_METADATA,
                CapabilityHints.getHint(CapabilityHints.KEY_METADATA));

        metaEnabled = CapabilityHints.VALUE_METADATA_ENABLED
                .equals(metadataStrategy);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#connect(java.lang.String, int, java.lang.String, java.lang.String)
     */
    public boolean connect(String host, int port, String user, String password)
            throws AuthenticationFailedException, MessagingException {
        
        // Authentication not supported..
        return true;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#disconnect()
     */
    public void disconnect() throws MessagingException {
        // No cleanup required..
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getDefaultFolder()
     */
    public Folder getDefaultFolder() throws MessagingException {
        return getFolder("");
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws MessagingException {

        File file = new File(name);

        // if path is not absolute use root of store to construct file..
        if (!file.isAbsolute()) {
            file = new File(url.getFile(), name);
        }

        if (metaEnabled) {
            return new MStorFolder(store, new MetaFolder(new MboxFolder(file)));
        }
        return new MStorFolder(store, new MboxFolder(file));
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(javax.mail.URLName)
     */
    public Folder getFolder(URLName url) throws MessagingException {
        return getFolder(url.getFile());
    }
}