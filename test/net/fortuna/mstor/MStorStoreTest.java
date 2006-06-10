/*
 * $Id$
 *
 * Created: [7/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.URLName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Type description.
 * @author benfortuna
 */
public class MStorStoreTest extends MStorTest {

    private static Log log = LogFactory.getLog(MStorStoreTest.class);

    /**
     * Default constructor.
     */
    public MStorStoreTest() {
        super(new URLName("mstor:etc/samples/Store"));
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

        log.info("Messages count [" + folder.getMessageCount() + "]");
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
