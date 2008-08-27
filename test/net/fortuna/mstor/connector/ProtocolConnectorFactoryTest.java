/*
 * $Id$
 *
 * Created on 08/10/2007
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

import java.util.Properties;

import javax.mail.Session;
import javax.mail.URLName;

import net.fortuna.mstor.connector.ProtocolConnectorFactory;
import net.fortuna.mstor.connector.jcr.RepositoryConnector;
import net.fortuna.mstor.connector.mbox.MboxConnector;

import junit.framework.TestCase;

/**
 * @author Ben
 *
 */
public class ProtocolConnectorFactoryTest extends TestCase {

    /**
     * Test method for {@link net.fortuna.mstor.connector.ProtocolConnectorFactory#create(javax.mail.URLName, net.fortuna.mstor.MStorStore, javax.mail.Session)}.
     */
    public void testCreate() {
        Properties p = new Properties();
        assertTrue(ProtocolConnectorFactory.getInstance().create(
                new URLName("mstor", null, -1, "/tmp/mbox", null, null),
                null, Session.getDefaultInstance(p)) instanceof MboxConnector);
        
        p.setProperty("mstor.repository.name", "test");
        assertTrue(ProtocolConnectorFactory.getInstance().create(
                new URLName("mstor", "localhost", -1, "/tmp/mbox", "test", null),
                null, Session.getDefaultInstance(p)) instanceof RepositoryConnector);
    }

}
