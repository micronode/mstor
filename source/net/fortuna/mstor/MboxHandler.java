/*
 * $Id$
 *
 * Created on 30/07/2007
 *
 * Copyright (c) 2007, Ben Fortuna
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

import java.io.File;
import java.util.Hashtable;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.fortuna.mstor.delegate.MboxFolder;
import net.fortuna.mstor.delegate.MetaFolder;
import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 */
public class MboxHandler implements ProtocolHandler {

    private Log log = LogFactory.getLog(MboxHandler.class);
    
    private URLName url;
    
    private MStorStore store;
    
    private Session session;
    
    private boolean metaEnabled;
    
    private Context context;
    
    private Repository repository;
    
    private javax.jcr.Session jcrSession;
    
    /**
     * @param url
     */
    public MboxHandler(URLName url, MStorStore store, Session session) {
        this.url = url;
        this.store = store;
        this.session = session;
        
        // enable metadata by default..
        String metadataStrategy = session.getProperties().getProperty(
                CapabilityHints.KEY_METADATA,
                CapabilityHints.getHint(CapabilityHints.KEY_METADATA));

        metaEnabled = !CapabilityHints.VALUE_METADATA_DISABLED
                .equals(metadataStrategy);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#connect(java.lang.String, int, java.lang.String, java.lang.String)
     */
    public boolean connect(String host, int port, String user, String password)
            throws AuthenticationFailedException, MessagingException {
        
        // Authentication not supported..
        return true;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#disconnect()
     */
    public void disconnect() throws MessagingException {
        // No cleanup required..
        if (jcrSession != null) {
            jcrSession.logout();
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws MessagingException {

        File file = new File(name);

        // if path is not absolute use root of store to construct file..
        if (!file.isAbsolute()) {
            file = new File(url.getFile(), name);
        }

        if (metaEnabled) {
            return new MStorFolder(store, new MetaFolder(new MboxFolder(file)));
        }
        return new MStorFolder(store, new MboxFolder(file));
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(javax.mail.URLName)
     */
    public Folder getFolder(URLName url) throws MessagingException {
        return getFolder(url.getFile());
    }

    /**
     * @return
     * @throws RepositoryException
     * @throws NamingException
     */
    private javax.jcr.Session getJcrSession() throws RepositoryException, NamingException {
        if (jcrSession == null) {
            if (url.getUsername() != null) {
                Credentials credentrials = new SimpleCredentials(url.getUsername(),
                        (url.getPassword() != null) ? url.getPassword().toCharArray() : new char[0]);
                jcrSession = getRepository().login(credentrials);
            }
            else {
                jcrSession = getRepository().login();
            }
        }
        return jcrSession;
    }
    
    /**
     * @return
     * @throws NamingException
     */
    private Repository getRepository() throws NamingException, RepositoryException {
        if (repository == null) {
            Hashtable env = new Hashtable();
            if (url.getHost() != null) {
                if (url.getPort() > 0) {
                    env.put(Context.PROVIDER_URL, url.getHost() + ':' + url.getPort());
                }
                else {
                    env.put(Context.PROVIDER_URL, url.getHost());
                }
            }
            else {
                env.put(Context.PROVIDER_URL, "localhost");
            }
            context = new InitialContext(env);
            String repoName = new File(url.getFile()).getName();
//            try {
                repository = (Repository) context.lookup(repoName);
//            }
//            catch (NamingException ne) {
                // bind repository..
//                RegistryHelper.registerRepository(context, repoName, "repository.xml", new File(url.getFile(), ".metadata").getAbsolutePath(), false);
//            }
//            repository = (Repository) context.lookup(repoName);
        }
        return repository;
    }
}
