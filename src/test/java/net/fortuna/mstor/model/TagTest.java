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

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for Tag support.
 * 
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 6/05/2006
 * </pre>
 * 
 */
public class TagTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(TagTest.class);

//    private String testFolder;

    private StoreLifecycle lifecycle;
    
    private Store store;
    
    private String username;
    
    private String password;
    
    private String[] folderNames;

    /**
     * Default constructor.
     */
    public TagTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method);
        this.lifecycle = lifecycle;
        this.username = username;
        this.password = password;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        lifecycle.startup();
        store = lifecycle.getStore();
        store.connect(username, password);
        
        List<String> folderList = new ArrayList<String>();
        Folder[] folders = store.getDefaultFolder().list();
        for (int i = 0; i < folders.length; i++) {
            folderList.add(folders[i].getName());
        }
        folderNames = folderList.toArray(new String[folderList.size()]);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        store.close();
        lifecycle.shutdown();
        super.tearDown();
    }


    /**
     * Logs a summary of all messages in the specified folder.
     * 
     * @param folder
     * @throws MessagingException
     */
    private void logMessages(final Folder folder) throws MessagingException {
        if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }

        for (int i = 1; i <= folder.getMessageCount(); i++) {
            Message message = folder.getMessage(i);
            LOG.info("Message [" + i + "]: " + message.getSubject());
            for (Iterator<String> it = Tags.getTags(message).iterator(); it.hasNext();) {
                LOG.info("Tag: " + it.next());
            }
        }
    }

    /**
     * A unit test that tags a message.
     */
    public void testTagMessage() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_WRITE);

            String tag = "Test 1";
    
            Message message = folder.getMessage(1);
            Tags.addTag(tag, message);
            assertTrue(Tags.getTags(message).contains(tag));
    
            logMessages(folder);
    
            folder.close(false);
        }
    }

    /**
     * A unit test that untags a message.
     */
    public void testUntagMessage() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_WRITE);

            String tag = "Test 1";
    
            Message message = folder.getMessage(1);
            Tags.addTag(tag, message);
            assertTrue(Tags.getTags(message).contains(tag));
            Tags.removeTag(tag, message);
            assertFalse(Tags.getTags(message).contains(tag));
    
            logMessages(folder);
    
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
            LOG.info("Sample [" + samples[i] + "]");

            suite.addTest(new TagTest("testTagMessage", samples[i]));
            suite.addTest(new TagTest("testUntagMessage", samples[i]));
        }
        return suite;
    }
    */
}
