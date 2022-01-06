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
import net.fortuna.mstor.model.MStorStoreTest;
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
public class JcrMStorStoreIntegrationTest {

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
        
        String repoName = "JcrMStorStoreTest.testGetDefaultFolder";
        Properties p = new Properties(defaultProps);
        p.setProperty("mstor.repository.name", repoName);
        suite.addTest(new MStorStoreTest("testGetDefaultFolder",
                new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        
//        repoName = "JcrMStorStoreTest.testGetFolderString";
//        p = new Properties(defaultProps);
//        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorStoreTest("testGetFolderString",
//            new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
//        
//        repoName = "JcrMStorStoreTest.testGetFolderURLName";
//        p = new Properties(defaultProps);
//        p.setProperty("mstor.repository.name", repoName);
//        suite.addTest(new MStorStoreTest("testGetFolderURLName",
//            new JcrStoreLifecycle(repoName, p, initMboxDir), null, null));
        return suite;
    }
}
