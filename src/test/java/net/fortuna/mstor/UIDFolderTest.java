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
package net.fortuna.mstor;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit tests applicable to UIDFolder support for mstor.
 * 
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 01/10/2006
 * </pre>
 * 
 */
public class UIDFolderTest extends AbstractMStorTest {

    private static final Log LOG = LogFactory.getLog(UIDFolderTest.class);

    /*
    private static Properties p = new Properties();
    static {
        // enable metadata..
        p.setProperty(CapabilityHints.KEY_METADATA,
                CapabilityHints.VALUE_METADATA_ENABLED);
    }
    */

    private UIDFolder[] uidFolders;

//    private String testFolder;

    /**
     * Default constructor.
     */
    public UIDFolderTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        super(method, lifecycle, username, password);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.AbstractMStorTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        List<UIDFolder> uidFolderList = new ArrayList<UIDFolder>();
        Folder[] folders = store.getDefaultFolder().list();
        for (int i = 0; i < folders.length; i++) {
            folders[i].open(Folder.READ_ONLY);
            uidFolderList.add((UIDFolder) folders[i]);
        }
        uidFolders = uidFolderList.toArray(new UIDFolder[uidFolderList.size()]);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.AbstractMStorTest#tearDown()
     */
    protected void tearDown() throws Exception {
        for (int i = 0; i < uidFolders.length; i++) {
            ((Folder) uidFolders[i]).close(false);
        }
        super.tearDown();
    }
    
    /**
     * Tests support for UIDFolder.
     * 
     * @throws MessagingException
     */
    public void testGetUIDValidity() throws MessagingException {
        for (int i = 0; i < uidFolders.length; i++) {
            long uidValidity = uidFolders[i].getUIDValidity();

            assertTrue(uidValidity > 0);
            assertEquals(uidValidity, uidFolders[i].getUIDValidity());
        }
    }

    /**
     * Tests support for UIDFolder.
     * 
     * @throws MessagingException
     */
    public void testGetMessageByUID() throws MessagingException {
        for (int i = 0; i < uidFolders.length; i++) {
            MStorMessage message = (MStorMessage) uidFolders[i].getMessageByUID(1);
            assertEquals(1, message.getUid());
        }
    }

    /**
     * Tests support for UIDFolder.
     * 
     * @throws MessagingException
     */
    public void testGetMessagesByUIDlonglong() throws MessagingException {
        for (int i = 0; i < uidFolders.length; i++) {
            Message[] messages = uidFolders[i].getMessagesByUID(1, 1);

            assertEquals(1, messages.length);
            assertEquals(1, ((MStorMessage) messages[0]).getUid());
//            assertEquals(3, ((MStorMessage) messages[1]).getUid());

            messages = uidFolders[i].getMessagesByUID(1, UIDFolder.LASTUID);

            assertEquals(1, messages.length);
            assertEquals(1, ((MStorMessage) messages[0]).getUid());
//            assertEquals(3, ((MStorMessage) messages[1]).getUid());
//            assertEquals(4, ((MStorMessage) messages[2]).getUid());
        }
    }

    /**
     * Tests support for UIDFolder.
     * 
     * @throws MessagingException
     */
    public void testGetMessagesByUIDArray() throws MessagingException {
        long[] uids = new long[] {1 };
        for (int i = 0; i < uidFolders.length; i++) {
            Message[] messages = uidFolders[i].getMessagesByUID(uids);

            assertEquals(1, messages.length);
            assertEquals(1, ((MStorMessage) messages[0]).getUid());
//            assertEquals(3, ((MStorMessage) messages[1]).getUid());
        }
    }

    /**
     * Tests support for UIDFolder.
     * 
     * @throws MessagingException
     */
    public void testGetUID() throws MessagingException {
        for (int i = 0; i < uidFolders.length; i++) {
            long uid = uidFolders[i].getUID(((Folder) uidFolders[i]).getMessage(1));

            assertEquals(1, uid);
        }
    }

    /**
     * @return
     * @throws IOException
     */
    /*
    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();

        File[] samples = getSamples();
        for (int i = 0; i < samples.length; i++) {
            LOG.info("Sample [" + samples[i] + "]");

            suite.addTest(new UIDFolderTest("testGetUIDValidity", samples[i]));
            suite.addTest(new UIDFolderTest("testGetMessageByUID", samples[i]));
            suite.addTest(new UIDFolderTest("testGetMessagesByUIDlonglong",
                    samples[i]));
            suite.addTest(new UIDFolderTest("testGetMessagesByUIDArray",
                    samples[i]));
            suite.addTest(new UIDFolderTest("testGetUID", samples[i]));
        }
        return suite;
    }
    */
}
