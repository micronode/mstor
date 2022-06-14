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
package org.mstor.mbox.model;

import jakarta.mail.*;
import org.mstor.mbox.connector.ProtocolConnectorFactory;
import org.mstor.provider.mail.ProtocolConnector;

/**
 * Implementation of a javamail store for the mstor provider.
 * 
 * @author Ben Fortuna
 *
 * <pre>
 * $Id$
 *
 * Created: [7/07/2004]
 * </pre>
 * 
 */
public final class MStorStore extends Store {

    /**
     * The default inbox folder name.
     */
    public static final String INBOX = "Inbox";
    
    private final ProtocolConnector protocolHandler;

    /**
     * Constructor.
     * 
     * @param session the session associated with the store
     * @param url a store location URL
     */
    public MStorStore(final Session session, final URLName url) {
        super(session, url);
        protocolHandler = ProtocolConnectorFactory.getInstance().create(
                url, this, session);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Folder getDefaultFolder() throws MessagingException {
        if (!isConnected()) {
            throw new IllegalStateException("Store not connected");
        }
        return protocolHandler.getDefaultFolder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Folder getFolder(final String name) throws MessagingException {
        if (!isConnected()) {
            throw new IllegalStateException("Store not connected");
        }
        return protocolHandler.getFolder(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Folder getFolder(final URLName url) throws MessagingException {
        if (!isConnected()) {
            throw new IllegalStateException("Store not connected");
        }
        return protocolHandler.getFolder(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean protocolConnect(final String host, final int port,
            final String user, final String password) throws MessagingException {
        
//        return protocolHandler.connect(host, port, user, password);
        return protocolHandler.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close() throws MessagingException {
        Folder[] folders = getDefaultFolder().list();
        for (Folder folder : folders) {
            if (folder.isOpen()) {
                folder.close(false);
            }
        }
        protocolHandler.disconnect();
        super.close();
    }
}
