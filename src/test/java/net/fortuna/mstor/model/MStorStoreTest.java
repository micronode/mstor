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
package net.fortuna.mstor.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;


/**
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [7/07/2004]
 * </pre>
 * 
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
