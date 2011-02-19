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
package net.fortuna.mstor.connector;

import javax.mail.Session;
import javax.mail.URLName;

import net.fortuna.mstor.MStorStore;
import net.fortuna.mstor.connector.jcr.JcrConnector;
import net.fortuna.mstor.connector.mbox.MboxConnector;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 05/08/2007
 * </pre>
 * 
 *
 */
public class ProtocolConnectorFactory {

    private static ProtocolConnectorFactory instance = new ProtocolConnectorFactory();
    
    /**
     * @param url a URL location of a connector-specific store
     * @param store a store instance to associate with the connector
     * @param session a session instance to associate with the connector
     * @return a new implementation-specific connector for the specified URL
     */
    public ProtocolConnector create(URLName url, MStorStore store, Session session) {
        if (session.getProperty("mstor.repository.name") != null) {
            return new JcrConnector(url, store, session);
        }
        return new MboxConnector(url, store, session);
    }

    /**
     * @return the instance
     */
    public static final ProtocolConnectorFactory getInstance() {
        return instance;
    }
}
