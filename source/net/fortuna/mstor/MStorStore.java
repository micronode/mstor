/*
 * $Id$
 *
 * Created: [7/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
package net.fortuna.mstor;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * Implementation of a javamail store for the mstor provider. An mbox-based store
 * would be specified with a url name as follows:
 * 
 * <pre>mstor:/home/user/mail/</pre>
 * 
 * A JCR-based store, accessible via JNDI, would have the following url structure:
 * 
 * <pre>mstor:jcr://user:password@localhost:1099/mail</pre>
 * 
 * Metadata is enabled by default,
 * however it may be disabled by specifying the following session property:
 * 
 * <pre>
 * mstor.meta.enabled = false
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class MStorStore extends Store {

    public static final String INBOX = "Inbox";
    
    private ProtocolHandler protocolHandler;

    /**
     * Constructor.
     * 
     * @param session
     * @param url
     */
    public MStorStore(final Session session, final URLName url) {
        super(session, url);
        protocolHandler = new MboxHandler(url, this, session);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Store#getDefaultFolder()
     */
    public final Folder getDefaultFolder() throws MessagingException {
        return protocolHandler.getFolder("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Store#getFolder(java.lang.String)
     */
    public final Folder getFolder(final String name) throws MessagingException {
        if (!isConnected()) {
            throw new IllegalStateException("Store not connected");
        }
        return protocolHandler.getFolder(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Store#getFolder(javax.mail.URLName)
     */
    public final Folder getFolder(final URLName url) throws MessagingException {
        return protocolHandler.getFolder(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Service#protocolConnect(java.lang.String, int, java.lang.String,
     *      java.lang.String)
     */
    /**
     * Override the superclass method to bypass authentication.
     */
    protected final boolean protocolConnect(final String host, final int port,
            final String user, final String password) throws MessagingException {
        
        return protocolHandler.connect(host, port, user, password);
    }

    /**
     * Close all open folders to release resources.
     * @see javax.mail.Service#close()
     */
    public synchronized void close() throws MessagingException {
        Folder[] folders = getDefaultFolder().list();
        for (int i = 0; i < folders.length; i++) {
            if (folders[i].isOpen()) {
                folders[i].close(false);
            }
        }
        protocolHandler.disconnect();
        super.close();
    }
}
