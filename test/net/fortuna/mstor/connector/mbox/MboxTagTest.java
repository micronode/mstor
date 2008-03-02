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
import net.fortuna.mstor.tag.TagTest;
import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

/**
 * @author Ben
 *
 */
public class MboxTagTest extends TestSuite {

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        
        Properties p = new Properties();
        p.setProperty(CapabilityHints.KEY_METADATA, CapabilityHints.VALUE_METADATA_ENABLED);
        
        File[] samples = new File("etc/samples").listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        //File[] samples = new File[] {new File("etc/samples/samples.mbx")};

        for (int i = 0; i < samples.length; i++) {
//            log.info("Sample [" + samples[i] + "]");
            suite.addTest(new TagTest("testTagMessage",
                    new MboxStoreLifecycle("testTagMessage", p, samples[i]), null, null));
            suite.addTest(new TagTest("testUntagMessage",
                    new MboxStoreLifecycle("testUntagMessage", p, samples[i]), null, null));
        }
        return suite;
    }
}
