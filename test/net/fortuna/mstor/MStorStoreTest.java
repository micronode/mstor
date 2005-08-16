/*
 * $Id$
 *
 * Created: [7/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Type description.
 * @author benfortuna
 */
public class MStorStoreTest extends TestCase {

    private static Log log = LogFactory.getLog(MStorStoreTest.class);

    private MStorStore store;

    /*
     * @see TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        super.setUp();

//        URLName url = new URLName("mstor:c:/temp/mail/store");
//        URLName url = new URLName("mstor:c:/temp/mail/Fastmail/archive2");
        URLName url = new URLName("mstor:f:/development/workspace/mstor/etc/samples");

        store = new MStorStore(Session.getDefaultInstance(new Properties()), url);
        store.connect();
    }

    /*
     * Class under test for Folder getDefaultFolder()
     */
    public final void testGetDefaultFolder() throws MessagingException {
        Folder folder = store.getDefaultFolder();

        assertNotNull(folder);

        log.info("Folder [" + folder.getName() + "]");
    }

    /*
     * Class under test for Folder getFolder(String)
     */
    public final void testGetFolderString() throws MessagingException {
//        Folder folder = store.getFolder("Inbox");
        Folder folder = store.getDefaultFolder().list()[0];

        assertNotNull(folder);

        log.info("Folder [" + folder.getName() + "]");

        folder.open(Folder.READ_WRITE);

        log.info("Message count [" + folder.getMessageCount() + "]");

        Message message = folder.getMessage(1);

        log.info("Message subject [" + message.getSubject() + "]");

        assertNotNull(message);

        log.info("Messages count [" + folder.getMessages().length + "]");
    }

    /*
     * Class under test for Folder getFolder(URLName)
     */
    public final void testGetFolderURLName() throws MessagingException {
        Folder folder = store.getFolder(new URLName("Test"));

        assertNotNull(folder);

        log.info("Folder [" + folder.getName() + "]");
    }

}
