/*
 * $Id$
 *
 * Created: [14/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import java.util.Enumeration;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for MStorMessage.
 *
 * @author benfortuna
 */
public class MStorMessageTest extends AbstractMStorTest {

    private static Log log = LogFactory.getLog(MStorFolderTest.class);

//    private String testFolder;

    /**
     * Default constructor.
     */
    public MStorMessageTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method, lifecycle, username, password);
    }

    /*
     * Class under test for Date getReceivedDate()
     */
    public final void testGetReceivedDate() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);
    
            Message message;
    
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                message = folder.getMessage(i);
    
                log.info("Message [" + i + "] received date ["
                        + message.getReceivedDate() + "]");
            }
    
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testSetFlag() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Flags flags = new Flags();
            flags.add(Flags.Flag.SEEN);
            flags.add(Flags.Flag.RECENT);
            flags.add("user 1");
            flags.add("user 2");
    
            Message message;
    
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                message = folder.getMessage(i);
                message.setFlag(Flags.Flag.SEEN, false);
                message.setFlag(Flags.Flag.ANSWERED, true);
                message.setFlag(Flags.Flag.USER, true);
                message.setFlags(flags, true);
            }
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testGetFlags() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Message message;
    
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                message = folder.getMessage(i);
    
                log.info("System Flags ["
                        + message.getFlags().getSystemFlags().length + "]");
                log.info("User Flags [" + message.getFlags().getUserFlags().length
                        + "]");
            }
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public void testGetAllHeaders() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            for (int i = 1; i < folder.getMessageCount(); i++) {
                Message message = folder.getMessage(i);
                for (Enumeration e = message.getAllHeaders(); e.hasMoreElements();) {
                    Header header = (Header) e.nextElement();
                    assertFalse("From_ line returned as header", header.getName()
                            .startsWith("From "));
                }
            }
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

            suite.addTest(new MStorMessageTest("testGetReceivedDate",
                    samples[i]));
            suite.addTest(new MStorMessageTest("testSetFlag", samples[i]));
            suite.addTest(new MStorMessageTest("testGetFlags", samples[i]));
            suite
                    .addTest(new MStorMessageTest("testGetAllHeaders",
                            samples[i]));
        }
        return suite;
    }
    */
}
