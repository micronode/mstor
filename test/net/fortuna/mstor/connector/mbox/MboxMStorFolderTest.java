/*
 * $Id$
 *
 * Created on 01/03/2008
 *
 * Copyright (c) 2008, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.connector.mbox;

import java.io.File;
import java.io.FileFilter;
import java.util.Properties;

import junit.framework.TestSuite;
import net.fortuna.mstor.MStorFolderTest;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

/**
 * @author Ben
 *
 */
public class MboxMStorFolderTest extends TestSuite {

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite(MboxMStorFolderTest.class.getSimpleName());
        
        Properties p = new Properties();
        
        File[] samples = new File("etc/samples/mailboxes").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        //File[] samples = new File[] {new File("etc/samples/mailboxes/samples.mbx")};

        for (int i = 0; i < samples.length; i++) {
//            log.info("Sample [" + samples[i] + "]");
            suite.addTest(new MStorFolderTest("testExists",
                    new MboxStoreLifecycle("testExists", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetSeparator",
                    new MboxStoreLifecycle("testGetSeparator", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetType",
                    new MboxStoreLifecycle("testGetType", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testCreate",
                    new MboxStoreLifecycle("testCreate", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testDelete",
                    new MboxStoreLifecycle("testDelete", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testOpen",
                    new MboxStoreLifecycle("testOpen", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testClose",
                    new MboxStoreLifecycle("testClose", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testCloseExpunge",
                    new MboxStoreLifecycle("testCloseExpunge", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetMessageCount",
                    new MboxStoreLifecycle("testGetMessageCount", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetName",
                    new MboxStoreLifecycle("testGetName", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetParent",
                    new MboxStoreLifecycle("testGetParent", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testListString",
                    new MboxStoreLifecycle("testListString", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetFolderString",
                    new MboxStoreLifecycle("testGetFolderString", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testRenameToFolder",
                    new MboxStoreLifecycle("testRenameToFolder", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testGetMessageint",
                    new MboxStoreLifecycle("testGetMessageint", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testAppendMessagesMessageArray",
                    new MboxStoreLifecycle("testAppendMessagesMessageArray", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testAppendToClosedFolder",
                    new MboxStoreLifecycle("testAppendToClosedFolder", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testExpunge",
                    new MboxStoreLifecycle("testExpunge", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testCopyMessages",
                    new MboxStoreLifecycle("testCopyMessages", p, samples[i]), null, null));
            suite.addTest(new MStorFolderTest("testParseMultipart",
                    new MboxStoreLifecycle("testParseMultipart", p, samples[i]), null, null));
        }
        return suite;
    }
}
