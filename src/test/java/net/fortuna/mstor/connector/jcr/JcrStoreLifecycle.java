/**
 * 
 */
package net.fortuna.mstor.connector.jcr;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.naming.Context;
import javax.naming.InitialContext;

import net.fortuna.mstor.StoreLifecycle;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.core.jndi.RegistryHelper;

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
     */
    public JcrStoreLifecycle(String name, Properties props, File initMboxFile) {
        this.repoName = name;
        this.defaultProps = props;
        this.initMboxFile = initMboxFile;
    }
    
    /*
     * (non-Javadoc)
     * @see net.fortuna.mstor.StoreLifecycle#getStore()
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

    /*
     * (non-Javadoc)
     * @see net.fortuna.mstor.StoreLifecycle#shutdown()
     */
    public void shutdown() throws Exception {
        RegistryHelper.unregisterRepository(context, repoName);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.mstor.StoreLifecycle#startup()
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
//        FileUtils.deleteQuietly(testDir);
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
                    LOG.info((1f / ((System.currentTimeMillis() - start) / (1000 * interval))) + " message(s)/s. Est. completion: " + DurationFormatUtils.formatDurationHMS((((System.currentTimeMillis() - init) / end) * (initFolder.getMessageCount() - end))));
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
