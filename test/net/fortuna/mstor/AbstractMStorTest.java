/*
 * $Id$
 *
 * Created on 6/05/2006
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.URLName;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

/**
 * Abstract base class for MStor unit tests. Provides setup of a mail store.
 * @author Ben Fortuna
 */
public abstract class AbstractMStorTest extends TestCase {

    private File source;

    private File testDir;

    private Properties sessionProps;

    private Session session;

    protected MStorStore store;

    /**
     * @param source
     * @throws IOException
     */
    public AbstractMStorTest(String method, File source) throws IOException {
        this(method, source, null);
    }

    /**
     * @param source
     * @throws IOException
     */
    public AbstractMStorTest(String method, File source, Properties props) throws IOException {
        super(method);
        this.source = source;
        this.sessionProps = props;
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        if (sessionProps != null) {
            session = Session.getDefaultInstance(sessionProps);
        } else {
            session = Session.getDefaultInstance(new Properties());
        }
        testDir = createTestHierarchy(source);
        URLName storeUrl = new URLName("mstor:" + testDir.getPath());
        store = new MStorStore(session, storeUrl);
        store.connect();
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (store != null && store.isConnected()) {
            store.close();
            store = null;
        }
        FileUtils.deleteDirectory(testDir);
    }

    /**
     * @param url
     * @return
     */
    private File createTestHierarchy(File source) throws IOException {
        File testDir = new File(System.getProperty("java.io.tmpdir"),
                "mstor_test" + File.separator + getName());
        
        if (source.isDirectory()) {
            FileUtils.copyDirectory(source, testDir);
        }
        else {
            FileUtils.copyFileToDirectory(source, testDir);
        }
        return testDir;
    }
    
    /**
     * @return
     */
    protected static File[] getSamples() {
        return new File("etc/samples").listFiles(
                (FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
    }

    /**
     * Overridden to return the current mbox file under test.
     */
    public final String getName() {
        return super.getName() + " [" + source.getName() + "]";
    }
}
