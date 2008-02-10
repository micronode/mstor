/*
 * $Id$
 *
 * Created on 14/05/2006
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.mstor;

import java.io.IOException;

import net.fortuna.mstor.data.MboxEncoderTest;
import net.fortuna.mstor.data.MboxFileTest;
import net.fortuna.mstor.search.TagsTermTest;
import net.fortuna.mstor.tag.TagTest;
import net.fortuna.mstor.tag.TagsTest;
import junit.framework.TestSuite;

/**
 * A suite of all unit tests.
 * 
 * @author Ben Fortuna
 */
public class AllTests extends TestSuite {

    /**
     * @return a suit of unit tests.
     */
    public static TestSuite suite() throws IOException {
        TestSuite suite = new TestSuite();

        // handlers..
        suite.addTestSuite(ProtocolHandlerFactoryTest.class);
        
        // javamail..
        suite.addTest(MStorStoreTest.suite());
        suite.addTest(MStorFolderTest.suite());
        suite.addTest(MStorMessageTest.suite());
        suite.addTest(UIDFolderTest.suite());

        // mbox..
        suite.addTestSuite(MboxEncoderTest.class);
        suite.addTest(MboxFileTest.suite());

        // tags..
        suite.addTestSuite(TagsTest.class);
        suite.addTest(TagTest.suite());
        suite.addTest(TagsTermTest.suite());

        return suite;
    }
}
