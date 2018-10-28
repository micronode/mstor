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
package net.fortuna.mstor.provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * Test case for MStorFolder.
 * 
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [10/07/2004]
 * </pre>
 * 
 */
public class MStorFolderTest extends AbstractMStorTest {

    // private static final int INITIAL_MESSAGE_COUNT = 1;

    private static Log log = LogFactory.getLog(MStorFolderTest.class);

//    private static Properties p = new Properties();
//    static {
        // disable metadata..
//        p.setProperty(CapabilityHints.KEY_METADATA,
//                CapabilityHints.VALUE_METADATA_ENABLED);

//        p.setProperty(CapabilityHints.KEY_METADATA, CapabilityHints.VALUE_METADATA_ENABLED);
//        p.setProperty(CapabilityHints.KEY_METADATA_STRATEGY,
//                CapabilityHints.VALUE_METADATA_STRATEGY_JCR);
        
//        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_BUFFER_STRATEGY,
//                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DIRECT);
//        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_CACHE_BUFFERS,
//                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED);
//    }

    private char expectedSeparator;
    
    /**
     * Default constructor.
     */
    public MStorFolderTest(String method, StoreLifecycle lifecycle, String username, String password) {
        super(method, lifecycle, username, password);
    }
    
    /**
     * @param lifecycle
     * @param username
     * @param password
     * @param expectedSeparator
     */
    public MStorFolderTest(StoreLifecycle lifecycle, String username, String password, char expectedSeparator) {
        super("testGetSeparator", lifecycle, username, password);
        this.expectedSeparator = expectedSeparator;
    }
    
    /**
     * @throws MessagingException
     */
    public final void testExists() throws MessagingException {
        assertTrue(store.getDefaultFolder().exists());
    }

    /**
     * @throws MessagingException
     */
    public final void testGetSeparator() throws MessagingException {
        assertEquals(expectedSeparator, store.getDefaultFolder().getSeparator());
    }

    /**
     * @throws MessagingException
     */
    public final void testGetType() throws MessagingException {
        assertEquals(store.getDefaultFolder().getType(), Folder.HOLDS_FOLDERS);
        
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            
            assertTrue((folder.getType() & Folder.HOLDS_MESSAGES) > 0);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testCreate() throws MessagingException {
        Folder test = store.getDefaultFolder().getFolder("Test");
        test.create(Folder.HOLDS_FOLDERS);

        Folder test2 = test.getFolder("Test2");
        test2.create(Folder.HOLDS_MESSAGES | Folder.HOLDS_FOLDERS);

        Folder test3 = test2.getFolder("Test3");
        test3.create(Folder.HOLDS_MESSAGES);
    }

    /**
     * @throws MessagingException
     */
    public final void testDelete() throws MessagingException {
        Folder test = store.getDefaultFolder().getFolder("TestDelete");
        test.create(Folder.HOLDS_FOLDERS);

        Folder test2 = test.getFolder("TestDelete2");
        test2.create(Folder.HOLDS_MESSAGES);

        test.delete(true);
    }

    /**
     * @throws MessagingException
     */
    public final void testOpen() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_ONLY);
            assertTrue(folder.isOpen());
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testClose() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_ONLY);
            folder.close(false);
    
            try {
                folder.close(false);
                fail("Should throw IllegalStateException");
            }
            catch (IllegalStateException ise) {
                log.info("Error ocurred: [" + ise.getMessage() + "]");
            }
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testCloseExpunge() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_WRITE);
            
            int initialCount = folder.getMessageCount();
    
            Message message = folder.getMessage(1);
            message.setFlag(Flags.Flag.DELETED, true);
            folder.close(true);
    
            folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_ONLY);
            assertEquals(initialCount - 1, folder.getMessageCount());
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testGetMessageCount() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_ONLY);

            // assertEquals(INITIAL_MESSAGE_COUNT, inbox.getMessageCount());
            assertTrue("Folder has no messages", folder.getMessageCount() > 0);
    
            if (folder.getName().equals("contenttype-semis.mbox")) {
                assertEquals(1, folder.getMessageCount());
            }
            else if (folder.getName().equals("imagined.mbox")) {
                assertEquals(223, folder.getMessageCount());
            }
            else if (folder.getName().equals("parseexception.mbox")) {
                assertEquals(1, folder.getMessageCount());
            }
            else if (folder.getName().equals("samples.mbx")) {
                assertEquals(2, folder.getMessageCount());
            }
            else if (folder.getName().equals("subject-0x1f.mbox")) {
                assertEquals(1, folder.getMessageCount());
            }
            else if (folder.getName().equals("received-0xc.mbox")) {
                assertEquals(1, folder.getMessageCount());
            }
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
        }
    }

    /*
     * Class under test for String getName()
     */
    public final void testGetName() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            
//            assertEquals(getTestFolder(Folder.READ_ONLY).getName(), getTestFile()
//                    .getName());
            assertTrue(store.getDefaultFolder().getName().equals(folder.getName()));
        }
    }

    /*
     * Class under test for Folder getParent()
     */
    public final void testGetParent() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            
            assertEquals(folder.getParent().getFullName(), store.getDefaultFolder().getFullName());
        }
    }

    /*
     * Class under test for Folder[] list(String)
     */
    public final void testListString() throws MessagingException {
        Folder[] folders = store.getDefaultFolder().list("%");
        for (int i = 0; i < folders.length; i++) {
            log.info("Folder [" + i + "] = " + folders[i].getName());
            assertNotNull(folders[i].getName());
            assertFalse("".equals(folders[i].getName()));
        }
    }

    /*
     * Class under test for Folder getFolder(String)
     */
    public final void testGetFolderString() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            assertNotNull(store.getFolder(folderNames[i]));
        }
    }

    /*
     * Class under test for boolean renameTo(Folder)
     */
    public final void testRenameToFolder() throws MessagingException {
        Folder folder = store.getDefaultFolder().getFolder("Rename");
        folder.create(Folder.HOLDS_FOLDERS);

        Folder renamed = store.getDefaultFolder().getFolder("Rename2");
        assertTrue(folder.renameTo(renamed));
    }

    /*
     * Class under test for Message getMessage(int)
     */
    public final void testGetMessageint() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);
            folder.open(Folder.READ_ONLY);

            folder.close(false);
            try {
                folder.getMessage(1);
                fail("Should throw IllegalStateException");
            }
            catch (IllegalStateException ise) {
                log.info("Error ocurred: [" + ise.getMessage() + "]");
            }
    
            folder.open(Folder.READ_ONLY);
            try {
                folder.getMessage(0);
                fail("Should throw IndexOutOfBoundsException");
            }
            catch (IndexOutOfBoundsException iobe) {
                log.info("Error ocurred: [" + iobe.getMessage() + "]");
            }
    
            try {
                folder.getMessage(folder.getMessageCount() + 1);
                fail("Should throw IndexOutOfBoundsException");
            }
            catch (IndexOutOfBoundsException iobe) {
                log.info("Error ocurred: [" + iobe.getMessage() + "]");
            }
    
            assertNotNull(folder.getMessage(1));
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
        }
    }

    /*
     * Class under test for void appendMessages(Message[])
     */
    public final void testAppendMessagesMessageArray()
            throws MessagingException, IOException {
        
        for (int n = 0; n < folderNames.length; n++) {
            Folder copy = store.getDefaultFolder().getFolder(folderNames[n] + "-copy1");
            copy.create(Folder.HOLDS_FOLDERS);

            Folder copy2 = copy.getFolder(folderNames[n] + "-copy2");
            copy2.create(Folder.HOLDS_MESSAGES);
            copy2.open(Folder.READ_WRITE);

            int messageCount = copy2.getMessageCount();
            
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);
    
            Message[] messages = folder.getMessages(1, folder.getMessageCount());
            copy2.appendMessages(messages);
    
            assertEquals(messageCount + folder.getMessageCount(), copy2
                    .getMessageCount());
    
            copy2.close(false);
            copy2.open(Folder.READ_ONLY);
    
            messages = folder.getMessages(1, folder.getMessageCount());
            for (int i = 0; i < messages.length; i++) {
                Message m = copy2.getMessage(copy2.getMessageCount()
                        - (messages.length - i - 1));
                assertEquals("Copy doesn't match message [" + i + "]", IOUtils.toString(messages[i].getInputStream()),
                        IOUtils.toString(m.getInputStream()));
            }
            assertTrue(folder.isOpen());
            folder.close(false);
            assertTrue(copy2.isOpen());
            copy2.close(false);
        }
    }

    /**
     * Test appending messages to a closed folder.
     */
    public void testAppendToClosedFolder() throws MessagingException {

        for (int n = 0; n < folderNames.length; n++) {
            Folder copy = store.getDefaultFolder().getFolder(folderNames[n] + "-copy3");
            copy.create(Folder.HOLDS_MESSAGES);
            copy.open(Folder.READ_ONLY);
            int messageCount = copy.getMessageCount();
            copy.close(false);
            
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages(1, folder.getMessageCount());
            copy.appendMessages(messages);
    
            copy.open(Folder.READ_ONLY);
            assertEquals(messageCount + folder.getMessageCount(), copy
                    .getMessageCount());
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
            assertTrue(copy.isOpen());
            copy.close(false);
        }
    }

    /*
     * Class under test for Message[] expunge()
     */
    public final void testExpunge() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_WRITE);

            Message message = folder.getMessage(1);
            message.setFlag(Flags.Flag.DELETED, true);
    
            Message[] expunged = folder.expunge();
    
            log.info("Expunged [" + Arrays.toString(expunged) + "]");
            assertEquals(1, expunged.length);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testCopyMessages() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Folder copy = store.getDefaultFolder().getFolder(folderNames[n] + "2");
            if (!copy.exists()) {
                copy.create(Folder.HOLDS_MESSAGES);
            }
            copy.open(Folder.READ_WRITE);
    
            int copyMessageCount = copy.getMessageCount();
            
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                Message message = folder.getMessage(i);
    
                log.info("Message subject: [" + message.getSubject() + "]");
                folder.copyMessages(new Message[] {message}, copy);
            }
    
            assertEquals(copyMessageCount + folder.getMessageCount(), copy.getMessageCount());
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
            assertTrue(copy.isOpen());
            copy.close(false);
        }
    }

    /**
     * Test parsing of multipart messages.
     * @throws MessagingException
     */
    public void testParseMultipart() throws MessagingException, IOException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            for (int i = 1; i <= folder.getMessageCount(); i++) {
                Message message = folder.getMessage(i);
                if (message.isMimeType("multipart/*")) {
                    log.info("Message " + i + " is multipart");
    
                    Multipart parts = (Multipart) message.getContent();
                    log.info("Message part count " + i + ": " + parts.getCount());
    
                    for (int j = 0; j < parts.getCount(); j++) {
                        log.info("Message part type " + i + "(" + j + "): "
                                + parts.getBodyPart(j).getContentType());
                    }
                }
            }
    
            // XXX: Temporarily close manually..
            assertTrue(folder.isOpen());
            folder.close(false);
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
            log.info("Sample [" + samples[i] + "]");

            suite.addTest(new MStorFolderTest("testExists", samples[i]));
            suite.addTest(new MStorFolderTest("testGetSeparator", samples[i]));
            suite.addTest(new MStorFolderTest("testGetType", samples[i]));
            suite.addTest(new MStorFolderTest("testCreate", samples[i]));
            suite.addTest(new MStorFolderTest("testDelete", samples[i]));
            suite.addTest(new MStorFolderTest("testOpen", samples[i]));
            suite.addTest(new MStorFolderTest("testClose", samples[i]));
            suite.addTest(new MStorFolderTest("testCloseExpunge", samples[i]));
            suite.addTest(new MStorFolderTest("testGetMessageCount",
                            samples[i]));
            suite.addTest(new MStorFolderTest("testGetName", samples[i]));
            suite.addTest(new MStorFolderTest("testGetParent", samples[i]));
            suite.addTest(new MStorFolderTest("testListString", samples[i]));
            suite.addTest(new MStorFolderTest("testGetFolderString",
                            samples[i]));
            suite.addTest(new MStorFolderTest("testRenameToFolder",
                            samples[i]));
            suite.addTest(new MStorFolderTest("testGetMessageint", samples[i]));
            suite.addTest(new MStorFolderTest("testAppendMessagesMessageArray",
                    samples[i]));
            suite.addTest(new MStorFolderTest("testAppendToClosedFolder",
                    samples[i]));
            suite.addTest(new MStorFolderTest("testExpunge", samples[i]));
            suite.addTest(new MStorFolderTest("testCopyMessages", samples[i]));
            suite.addTest(new MStorFolderTest("testParseMultipart",
                            samples[i]));
        }
        return suite;
    }
    */
}
