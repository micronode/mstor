/*
 * $Id$
 *
 * Created: [10/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.URLName;

import junit.framework.Test;
import junit.framework.TestSuite;

import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test case for MStorFolder.
 * 
 * @author benfortuna
 */
public class MStorFolderTest extends AbstractMStorTest {

//    private static final int INITIAL_MESSAGE_COUNT = 1;
    
    private static Log log = LogFactory.getLog(MStorFolderTest.class);

    private static Properties p = new Properties();
    static {
        // disable metadata..
        p.setProperty(CapabilityHints.KEY_METADATA,
                CapabilityHints.VALUE_METADATA_DEFAULT);
        
        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_BUFFER_STRATEGY,
                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DIRECT);
        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_CACHE_BUFFERS,
                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED);
    }

    private String testFolder;
    
    /**
     * Default constructor.
     */
    public MStorFolderTest(String method, File testFile) throws IOException {
//        super(new File("etc/samples/Store"), p);
        super(method, testFile, p);
        testFolder = testFile.getName();

        // clean up..
        /*
        new File("c:/temp/mstor_test/Copy/Copy2").delete();
        new File("c:/temp/mstor_test/Copy").delete();
        new File("c:/temp/mstor_test/Rename2").delete();
        new File("c:/temp/mstor_test/Test/Test2.sbd/Test3").delete();
        new File("c:/temp/mstor_test/Test/Test2.sbd").delete();
        new File("c:/temp/mstor_test/Test/Test2").delete();
        new File("c:/temp/mstor_test/Test").delete();
        new File("c:/temp/mstor_test/Inbox2").delete();
        */
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
        assertEquals(store.getDefaultFolder().getSeparator(),
                File.separatorChar);
    }

    /**
     * @throws MessagingException
     */
    public final void testGetType() throws MessagingException {
        assertEquals(store.getDefaultFolder().getType(), Folder.HOLDS_FOLDERS);

        assertTrue((store.getDefaultFolder().getFolder(testFolder).getType()
                & Folder.HOLDS_MESSAGES) > 0);
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
     * 
     */
    public void testHasNewMessages() {
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
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);
    }

    /**
     * @throws MessagingException
     */
    public final void testClose() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);
        inbox.close(false);

        try {
            inbox.close(false);
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException ise) {
            log.info("Error ocurred: [" + ise.getMessage() + "]");
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testCloseExpunge() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_WRITE);
        Message message = inbox.getMessage(1);
        message.setFlag(Flags.Flag.DELETED, true);
        inbox.close(true);
    }

    /**
     * @throws MessagingException
     */
    public final void testIsOpen() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);

        assertTrue(inbox.isOpen());
    }

    public final void testGetMessageCount() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);

//        assertEquals(INITIAL_MESSAGE_COUNT, inbox.getMessageCount());
        assertTrue(inbox.getMessageCount() > 0);
        inbox.close(false);
    }

    public void testMStorFolder() {
    }

    /*
     * Class under test for String getName()
     */
    public final void testGetName() throws MessagingException {
        assertEquals(store.getDefaultFolder().getFolder(testFolder).getName(),
                testFolder);
    }

    /*
     * Class under test for String getFullName()
     */
    public void testGetFullName() {
    }

    /*
     * Class under test for Folder getParent()
     */
    public final void testGetParent() throws MessagingException {
        assertEquals(store.getDefaultFolder().getFolder(testFolder).getParent()
                .getFullName(), store.getDefaultFolder().getFullName());
    }

    /*
     * Class under test for Folder[] list(String)
     */
    public final void testListString() throws MessagingException {
        Folder[] folders = store.getDefaultFolder().list("%");

        for (int i = 0; i < folders.length; i++) {
            log.info("Folder [" + i + "] = " + folders[i].getName());
        }
    }

    /*
     * Class under test for Folder getFolder(String)
     */
    public final void testGetFolderString() throws MessagingException {
        assertNotNull(store.getDefaultFolder().getFolder(testFolder));
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
     * Class under test for Flags getPermanentFlags()
     */
    public void testGetPermanentFlags() {
    }

    /*
     * Class under test for Message getMessage(int)
     */
    public final void testGetMessageint() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);

        try {
            inbox.getMessage(1);
            fail("Should throw IllegalStateException");
        }
        catch (IllegalStateException ise) {
            log.info("Error ocurred: [" + ise.getMessage() + "]");
        }

        inbox.open(Folder.READ_ONLY);

        try {
            inbox.getMessage(0);
            fail("Should throw IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException iobe) {
            log.info("Error ocurred: [" + iobe.getMessage() + "]");
        }

        try {
            inbox.getMessage(inbox.getMessageCount() + 1);
            fail("Should throw IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException iobe) {
            log.info("Error ocurred: [" + iobe.getMessage() + "]");
        }

        assertNotNull(inbox.getMessage(1));
        
        inbox.close(false);
    }

    /*
     * Class under test for void appendMessages(Message[])
     */
    public final void testAppendMessagesMessageArray()
        throws MessagingException, IOException {
        
        Folder copy = store.getDefaultFolder().getFolder("Copy");
        copy.create(Folder.HOLDS_FOLDERS);

        Folder copy2 = copy.getFolder("Copy2");
        copy2.create(Folder.HOLDS_MESSAGES);
        copy2.open(Folder.READ_WRITE);

        int messageCount = copy2.getMessageCount();

        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages(1, inbox.getMessageCount());
        copy2.appendMessages(messages);

        assertEquals(messageCount + inbox.getMessageCount(),
                copy2.getMessageCount());

        copy2.close(false);
        copy2.open(Folder.READ_ONLY);

        messages = inbox.getMessages(1, inbox.getMessageCount());
        for (int i = 1; i < messages.length; i++) {
            Message m = copy2.getMessage(
                    copy2.getMessageCount() - (messages.length - 1 - i));
            assertEquals(IOUtils.toString(messages[i].getInputStream()),
                    IOUtils.toString(m.getInputStream()));
        }
        inbox.close(false);
        copy2.close(false);
    }
    
    /**
     * Test appending messages to a closed folder.
     */
    public void testAppendToClosedFolder() throws MessagingException {
        Folder copy = store.getDefaultFolder().getFolder("Copy");
        copy.create(Folder.HOLDS_MESSAGES);
        copy.open(Folder.READ_ONLY);
        int messageCount = copy.getMessageCount();
        copy.close(false);

        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages(1, inbox.getMessageCount());
        copy.appendMessages(messages);

        copy.open(Folder.READ_ONLY);
        assertEquals(messageCount + inbox.getMessageCount(),
                copy.getMessageCount());

        inbox.close(false);
        copy.close(false);
    }

    /*
     * Class under test for Message[] expunge()
     */
    public final void testExpunge() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_WRITE);

        Message message = inbox.getMessage(1);
        message.setFlag(Flags.Flag.DELETED, true);

        Message[] expunged = inbox.expunge();

        log.info("Expunged [" + expunged + "]");
    }

    public final void testCopyMessages() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);

        Folder copy = store.getDefaultFolder().getFolder("Inbox2");
        if (!copy.exists()) {
            copy.create(Folder.HOLDS_MESSAGES);
        }
        copy.open(Folder.READ_WRITE);

        for (int i = 1; i <= inbox.getMessageCount(); i++) {
            Message message = inbox.getMessage(i);

            log.info("Message subject: [" + message.getSubject() + "]");
            inbox.copyMessages(new Message[] {message}, copy);
        }

        assertEquals(inbox.getMessageCount(), copy.getMessageCount());
        
        inbox.close(false);
        copy.close(false);
    }
    
    /**
     * Test parsing of multipart messages.
     * @throws MessagingException
     */
    public void testParseMultipart() throws MessagingException, IOException {
        Folder inbox = store.getDefaultFolder().getFolder(testFolder);
        inbox.open(Folder.READ_ONLY);
        
        for (int i = 1; i <= inbox.getMessageCount(); i++) {
            Message message = inbox.getMessage(i);
            if (message.isMimeType("multipart/*")) {
                log.info("Message " + i + " is multipart");
                
                Multipart parts = (Multipart) message.getContent();
                log.info("Message part count " + i + ": " + parts.getCount());
                
                for (int j = 0; j < parts.getCount(); j++) {
                    log.info("Message part type " + i + "(" + j + "): " + parts.getBodyPart(j).getContentType());
                }
            }
        }

        inbox.close(false);
    }

    /**
     * @return
     * @throws IOException
     */
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
            suite.addTest(new MStorFolderTest("testIsOpen", samples[i]));
            suite.addTest(new MStorFolderTest("testGetMessageCount", samples[i]));
            suite.addTest(new MStorFolderTest("testGetName", samples[i]));
            suite.addTest(new MStorFolderTest("testGetParent", samples[i]));
            suite.addTest(new MStorFolderTest("testListString", samples[i]));
            suite.addTest(new MStorFolderTest("testGetFolderString", samples[i]));
            suite.addTest(new MStorFolderTest("testRenameToFolder", samples[i]));
            suite.addTest(new MStorFolderTest("testGetMessageint", samples[i]));
            suite.addTest(new MStorFolderTest("testAppendMessagesMessageArray", samples[i]));
            suite.addTest(new MStorFolderTest("testAppendToClosedFolder", samples[i]));
            suite.addTest(new MStorFolderTest("testExpunge", samples[i]));
            suite.addTest(new MStorFolderTest("testCopyMessages", samples[i]));
            suite.addTest(new MStorFolderTest("testParseMultipart", samples[i]));
        }
        return suite;
    }
}
