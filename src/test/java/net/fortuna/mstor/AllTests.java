/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.mstor;

import java.io.IOException;

import junit.framework.TestSuite;
import net.fortuna.mstor.connector.ProtocolConnectorFactoryTest;
import net.fortuna.mstor.connector.mbox.MboxMStorFolderTest;
import net.fortuna.mstor.connector.mbox.MboxMStorMessageTest;
import net.fortuna.mstor.connector.mbox.MboxMStorStoreTest;
import net.fortuna.mstor.connector.mbox.MboxTagTest;
import net.fortuna.mstor.connector.mbox.MboxTagsTermTest;
import net.fortuna.mstor.connector.mbox.MboxUIDFolderTest;
import net.fortuna.mstor.data.MboxEncoderTest;
import net.fortuna.mstor.data.MboxFileTest;
import net.fortuna.mstor.data.MessageInputStreamTest;
import net.fortuna.mstor.tag.TagsTest;

/**
 * A suite of all unit tests.
 * 
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 14/05/2006
 * </pre>
 * 
 */
public class AllTests extends TestSuite {

    /**
     * @return a suit of unit tests.
     */
    public static TestSuite suite() throws IOException {
        TestSuite suite = new TestSuite();

        // handlers..
        suite.addTestSuite(ProtocolConnectorFactoryTest.class);
        
        // mbox connector..
        suite.addTest(MboxMStorStoreTest.suite());
        suite.addTest(MboxMStorFolderTest.suite());
        suite.addTest(MboxMStorMessageTest.suite());
        suite.addTest(MboxUIDFolderTest.suite());

        // mbox..
        suite.addTestSuite(MboxEncoderTest.class);
        suite.addTest(MboxFileTest.suite());
        suite.addTest(MessageInputStreamTest.suite());

        // tags..
        suite.addTestSuite(TagsTest.class);
        suite.addTest(MboxTagTest.suite());
        suite.addTest(MboxTagsTermTest.suite());

        return suite;
    }
}
