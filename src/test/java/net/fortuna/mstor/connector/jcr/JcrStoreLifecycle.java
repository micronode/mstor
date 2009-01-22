/**
 * 
 */
package net.fortuna.mstor.connector.jcr;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;

import net.fortuna.mstor.StoreLifecycle;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.jndi.RegistryHelper;

/**
 * @author fortuna
 */
public class JcrStoreLifecycle implements StoreLifecycle {

    private static final String BASE_TEST_DIR = System.getProperty("java.io.tmpdir")
            + File.separator + JcrStoreLifecycle.class.getSimpleName() + File.separator;

    private Context context;
    
    private String repoName;

    private Properties defaultProps;
    
    /**
     * @param name
     * @param props
     */
    public JcrStoreLifecycle(String name, Properties props) {
        this.repoName = name;
        this.defaultProps = props;
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
        FileUtils.deleteQuietly(testDir);
        RegistryHelper.registerRepository(context, repoName,
                "src/test/resources/repository.xml", testDir.getAbsolutePath(),
                false);
    }

}
