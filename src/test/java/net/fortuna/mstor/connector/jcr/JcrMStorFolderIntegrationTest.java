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
package net.fortuna.mstor.connector.jcr;

import junit.framework.TestSuite;
import net.fortuna.mstor.model.MStorFolderTest;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.io.File;
import java.util.Properties;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 22/01/2009
 * </pre>
 * 
 *
 */
@RunWith(AllTests.class)
public class JcrMStorFolderIntegrationTest {

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        
        Properties defaultProps = new Properties();
        defaultProps.setProperty("mail.store.protocol", "mstor");
//        defaultProps.setProperty("mstor.repository.provider.url", "localhost");
        defaultProps.setProperty("mstor.repository.path", "mail");
        defaultProps.setProperty("mstor.repository.create", "true");
        
        File initMboxDir = new File("etc/samples/mailboxes/MboxFile");
        
        String repoName = "JcrMStorFolderTest.testExists";
        Properties p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testExists", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testGetSeparator";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest(new JcrStoreLifecycle(repoName, p, initMboxDir), null, null, '/'));
        
        repoName = "JcrMStorFolderTest.testGetType";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testGetType", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testCreate";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testCreate", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testDelete";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testDelete", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testOpen";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testOpen", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testClose";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testClose", new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testCloseExpunge";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testCloseExpunge", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testGetMessageCount";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testGetMessageCount", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testGetName";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testGetName", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testGetParent";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testGetParent", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testListString";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testListString",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testGetFolderString";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testGetFolderString",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testRenameToFolder";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testRenameToFolder",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testGetMessageInt";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testGetMessageInt", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testAppendMessagesMessageArray";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testAppendMessagesMessageArray",
//            new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testAppendToClosedFolder";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testAppendToClosedFolder",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testExpunge";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorFolderTest("testExpunge", new JcrStoreLifecycle(repoName, p), null, null));
        
        repoName = "JcrMStorFolderTest.testCopyMessages";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testCopyMessages",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        repoName = "JcrMStorFolderTest.testParseMultipart";
        p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorFolderTest("testParseMultipart",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
        return suite;
    }
}
