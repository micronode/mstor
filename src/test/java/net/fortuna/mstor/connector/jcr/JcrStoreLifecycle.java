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
/**
 * 
 */
package net.fortuna.mstor.connector.jcr;

import net.fortuna.mstor.model.StoreLifecycle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.jndi.RegistryHelper;

import javax.mail.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author fortuna
 */
public class JcrStoreLifecycle implements StoreLifecycle {

    private static final Log LOG = LogFactory.getLog(JcrStoreLifecycle.class);
    
    private static final String BASE_TEST_DIR = System.getProperty("java.io.tmpdir")
            + File.separator + JcrStoreLifecycle.class.getSimpleName() + File.separator;

    private Context context;
    
    private String repoName;

    private Properties defaultProps;
    
    private File initMboxFile;
    
    /**
     * @param name
     * @param props
     * @param initMboxFile
     */
    public JcrStoreLifecycle(String name, Properties props, File initMboxFile) {
        this.repoName = name;
        this.defaultProps = props;
        this.initMboxFile = initMboxFile;
    }
    
    /**
     * {@inheritDoc}
     */
    public Store getStore() throws NoSuchProviderException {
        Session session = Session.getInstance(defaultProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("fortuna", "mstor");
            }
        });
        return session.getStore();
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() throws Exception {
        RegistryHelper.unregisterRepository(context, repoName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void startup() throws Exception {
        // bind repository..
        Hashtable env = new Hashtable();
        // env.put(Context.INITIAL_CONTEXT_FACTORY,
        // "com.sun.jndi.rmi.registry.RegistryContextFactory");
        env.put(Context.PROVIDER_URL, "localhost");
        context = new InitialContext(env);

        File testDir = new File(BASE_TEST_DIR, repoName);
        FileUtils.deleteQuietly(testDir);
        RegistryHelper.registerRepository(context, repoName,
                "src/test/resources/repository.xml", testDir.getAbsolutePath(),
                false);
        
        // copy data from init mbox..
        Session session = Session.getInstance(new Properties());
        Store initMboxStore = session.getStore(new URLName("mstor:" + initMboxFile.getAbsolutePath()));
        initMboxStore.connect();
        Store store = getStore();
        store.connect();
        
        for (Folder initFolder : initMboxStore.getDefaultFolder().list()) {
            if ((initFolder.getType() & Folder.HOLDS_MESSAGES) > 0) {
                initFolder.open(Folder.READ_ONLY);
                Folder folder = store.getDefaultFolder().getFolder(initFolder.getName());
                if (!folder.exists()) {
                    folder.create(initFolder.getType());
                }
                folder.open(Folder.READ_WRITE);
                
                int interval = 500;
                long start = -1, init = System.currentTimeMillis();
                for (int i = 1; i <= initFolder.getMessageCount();) {
                    int end = Math.min(interval + i - 1, initFolder.getMessageCount());
                    LOG.info("Appending messages: " + i + " - " + (end));
                    start = System.currentTimeMillis();
                    folder.appendMessages(initFolder.getMessages(i, end));
                    LOG.info((1f / ((System.currentTimeMillis() - start) / (1000 * interval)))
                            + " message(s)/s. Est. completion: "
                            + DurationFormatUtils.formatDurationHMS((((System.currentTimeMillis() - init) / end)
                                    * (initFolder.getMessageCount() - end))));
                    i += interval;
                }
                folder.close(false);
                initFolder.close(false);
            }
        }
        
        initMboxStore.close();
//        getStore().close();
    }

}
