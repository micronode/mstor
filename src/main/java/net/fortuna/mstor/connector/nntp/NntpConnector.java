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
package net.fortuna.mstor.connector.nntp;

import net.fortuna.mstor.connector.AbstractProtocolConnector;
import net.fortuna.mstor.provider.MStorStore;
import org.apache.commons.net.nntp.NNTPClient;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;
import java.io.IOException;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 10/08/2008
 * </pre>
 * 
 *
 */
public class NntpConnector extends AbstractProtocolConnector {

    private NNTPClient client;
    
    /**
     * @param url
     * @param store
     */
    public NntpConnector(URLName url, MStorStore store) {
        super(url, store);
        client = new NNTPClient();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.ProtocolConnector#connect(java.lang.String, int, java.lang.String, java.lang.String)
     */
    public boolean connect() {
/*
        try {
            client.connect(host, port);
        }
        catch (SocketException se) {
            throw new MessagingException("Cannot connect", se);
        }
        catch (IOException ioe) {
            throw new MessagingException("Communication error", ioe);
        }
        try {
            return client.authenticate(user, password);
        }
        catch (IOException ioe) {
            throw new AuthenticationFailedException("Communication error: " + ioe);
        }
*/
        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.ProtocolConnector#disconnect()
     */
    public void disconnect() throws MessagingException {
        try {
            client.disconnect();
        }
        catch (IOException ioe) {
            throw new MessagingException("Error disconnecting", ioe);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.ProtocolConnector#getDefaultFolder()
     */
    public Folder getDefaultFolder() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.ProtocolConnector#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.ProtocolConnector#getFolder(javax.mail.URLName)
     */
    public Folder getFolder(URLName url) {
        // TODO Auto-generated method stub
        return null;
    }

}
