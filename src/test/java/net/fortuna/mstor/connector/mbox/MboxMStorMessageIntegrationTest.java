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
package net.fortuna.mstor.connector.mbox;

import junit.framework.TestSuite;
import net.fortuna.mstor.connector.mbox.MboxConnector.MetadataStrategy;
import net.fortuna.mstor.model.MStorMessageTest;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.io.File;
import java.io.FileFilter;
import java.util.Properties;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 01/03/2008
 * </pre>
 * 
 *
 */
@RunWith(AllTests.class)
public class MboxMStorMessageIntegrationTest {

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite(MboxMStorMessageIntegrationTest.class.getSimpleName());
        
        Properties p = new Properties();
        p.setProperty(MboxConnector.KEY_METADATA_STRATEGY, MetadataStrategy.YAML.toString());

        File[] samples = new File("etc/samples/mailboxes").listFiles(
                (FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        //File[] samples = new File[] {new File("etc/samples/mailboxes/samples.mbx")};

        for (int i = 0; i < samples.length; i++) {
//            log.info("Sample [" + samples[i] + "]");
            suite.addTest(new MStorMessageTest("testGetReceivedDate",
                    new MboxStoreLifecycle("testGetReceivedDate", p, samples[i]), null, null));
            suite.addTest(new MStorMessageTest("testSetFlag",
                    new MboxStoreLifecycle("testSetFlag", p, samples[i]), null, null));
            suite.addTest(new MStorMessageTest("testGetFlags",
                    new MboxStoreLifecycle("testGetFlags", p, samples[i]), null, null));
            suite.addTest(new MStorMessageTest("testGetAllHeaders",
                    new MboxStoreLifecycle("testGetAllHeaders", p, samples[i]), null, null));
        }
        return suite;
    }
}
