/*
 * $Id$
 *
 * Created: [7/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Type description.
 * @author benfortuna
 */
public class MStorStoreTest extends AbstractMStorTest {

    private static Log log = LogFactory.getLog(MStorStoreTest.class);
    
    /**
     * Default constructor.
     */
    public MStorStoreTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method, lifecycle, username, password);
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
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(folderNames[i]);

            assertNotNull(folder);

            log.info("Folder [" + folder.getName() + "]");
        }
    }

    /*
     * Class under test for Folder getFolder(URLName)
     */
    public final void testGetFolderURLName() throws MessagingException {
        for (int i = 0; i < folderNames.length; i++) {
            Folder folder = store.getFolder(new URLName(folderNames[i]));

            assertNotNull(folder);

            log.info("Folder [" + folder.getName() + "]");
        }
    }
}
