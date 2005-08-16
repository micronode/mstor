/*
 * $Id$
 * 
 * Created: [14/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A test case for MStorMessage.
 * @author benfortuna
 */
public class MStorMessageTest extends TestCase {

    private static Log log = LogFactory.getLog(MStorFolderTest.class);

    private MStorStore store;
    
    /*
     * @see TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        super.setUp();
        
//        URLName url = new URLName("mstor:c:/temp/mail/Aardvark/store");
        URLName url = new URLName("mstor:c:/temp/mstor_test");
        
        store = new MStorStore(Session.getDefaultInstance(new Properties()), url);
        store.connect();
    }

    /*
     * Class under test for Date getReceivedDate()
     */
    public final void testGetReceivedDate() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);
        
        Message message;
        
        for (int i = 1; i <= inbox.getMessageCount(); i++) {
            message = inbox.getMessage(i);
            
            log.info("Message [" + i + "] received date [" + message.getReceivedDate() + "]");
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testSetFlag() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);

        Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.RECENT);
        flags.add("user 1");
        flags.add("user 2");
        
        Message message;
        
        for (int i = 1; i <= inbox.getMessageCount(); i++) {
            message = inbox.getMessage(i);
            message.setFlag(Flags.Flag.SEEN, false);
            message.setFlag(Flags.Flag.ANSWERED, true);
            message.setFlag(Flags.Flag.USER, true);
            message.setFlags(flags, true);
        }
    }
    
    /**
     * @throws MessagingException
     */
    public final void testGetFlags() throws MessagingException {
        Folder inbox = store.getDefaultFolder().getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);
        
        Message message;
        
        for (int i = 1; i <= inbox.getMessageCount(); i++) {
            message = inbox.getMessage(i);
            
            log.info("System Flags [" + message.getFlags().getSystemFlags().length + "]");
            log.info("User Flags [" + message.getFlags().getUserFlags().length + "]");
        }
    }
}
