/*
 * $Id$
 *
 * Created on 01/10/2006
 *
 * Copyright (c) 2006, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;

import net.fortuna.mstor.util.CapabilityHints;


/**
 * Unit tests applicable to UIDFolder support for mstor.
 * @author Ben Fortuna
 */
public class UIDFolderTest extends AbstractMStorTest {

    private static Properties p = new Properties();
    static {
        // enable metadata..
        p.setProperty(CapabilityHints.KEY_METADATA,
                CapabilityHints.VALUE_METADATA_ENABLED);
    }

    private UIDFolder folder;

    /**
     * Default constructor.
     */
    public UIDFolderTest() throws IOException {
        super(new File("etc/samples/UIDFolder"), p);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.AbstractMStorTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        folder = (UIDFolder) store.getDefaultFolder().getFolder("Inbox");
        ((Folder) folder).open(Folder.READ_ONLY);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.AbstractMStorTest#tearDown()
     */
    protected void tearDown() throws Exception {
        ((Folder) folder).close(false);

        super.tearDown();
    }

    /**
     * Tests support for UIDFolder.
     * @throws MessagingException
     */
//    public void testGetUIDValidity() throws MessagingException {
//        assertTrue(folder.getUIDValidity() > 0);
//    }

    /**
     * Tests support for UIDFolder.
     * @throws MessagingException
     */
    public void testGetMessageByUID() throws MessagingException {
        MStorMessage message = (MStorMessage) folder.getMessageByUID(2);
        assertEquals(2, message.getUid());
    }

    /**
     * Tests support for UIDFolder.
     * @throws MessagingException
     */
    public void testGetMessagesByUIDlonglong() throws MessagingException {
        Message[] messages = folder.getMessagesByUID(2, 3);

        assertEquals(2, messages.length);
        assertEquals(2, ((MStorMessage) messages[0]).getUid());
        assertEquals(3, ((MStorMessage) messages[1]).getUid());

        messages = folder.getMessagesByUID(2, UIDFolder.LASTUID);

        assertEquals(3, messages.length);
        assertEquals(2, ((MStorMessage) messages[0]).getUid());
        assertEquals(3, ((MStorMessage) messages[1]).getUid());
        assertEquals(4, ((MStorMessage) messages[2]).getUid());
    }

    /**
     * Tests support for UIDFolder.
     * @throws MessagingException
     */
    public void testGetMessagesByUIDArray() throws MessagingException {
        long[] uids = new long[] { 2, 3 };
        Message[] messages = folder.getMessagesByUID(uids);

        assertEquals(2, messages.length);
        assertEquals(2, ((MStorMessage) messages[0]).getUid());
        assertEquals(3, ((MStorMessage) messages[1]).getUid());
    }

    /**
     * Tests support for UIDFolder.
     * @throws MessagingException
     */
    public void testGetUID() throws MessagingException {
        long uid = folder.getUID(((Folder) folder).getMessage(1)); 

        assertEquals(1, uid);
    }
}
