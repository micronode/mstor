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
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import net.fortuna.mstor.StoreLifecycle;

import org.apache.commons.io.FileUtils;

/**
 * @author Ben
 */
public class MboxStoreLifecycle implements StoreLifecycle {

    private Properties sessionProps;

    private File testDir;

    private File testFile;

    private Store store;

    /**
     * @param sessionProps
     * @param storeUrl
     */
    public MboxStoreLifecycle(String name, Properties sessionProps,
            File testFile) {
        this.sessionProps = sessionProps;
        this.testFile = testFile;

        if (testFile != null) {
            testDir = new File(System.getProperty("java.io.tmpdir"), "mstor_test"
                    + File.separator + name + File.separator + testFile.getName());
        }
        else {
            testDir = new File(System.getProperty("java.io.tmpdir"), "mstor_test"
                    + File.separator + name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.StoreLifecycle#getStore()
     */
    public Store getStore() {
        return store;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.StoreLifecycle#shutdown()
     */
    public void shutdown() throws Exception {
        FileUtils.deleteDirectory(testDir);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.StoreLifecycle#startup()
     */
    public void startup() throws Exception {
        // make sure test directory is clean..
        FileUtils.deleteDirectory(testDir);
        
        if (testFile != null) {
            if (testFile.isDirectory()) {
                FileUtils.copyDirectory(testFile, testDir);
            }
            else {
                FileUtils.copyFileToDirectory(testFile, testDir);
            }
        }
        else {
            testDir.mkdirs();
        }

        Session session = Session.getInstance(sessionProps);
        store = session.getStore(new URLName("mstor:"
                + testDir.getAbsolutePath()));
    }

}
