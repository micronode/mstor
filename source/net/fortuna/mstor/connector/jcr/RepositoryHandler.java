/*
 * $Id$
 *
 * Created on 05/08/2007
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
package net.fortuna.mstor.connector.jcr;

import java.io.File;
import java.util.Hashtable;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.fortuna.mstor.MStorFolder;
import net.fortuna.mstor.MStorStore;
import net.fortuna.mstor.connector.AbstractProtocolHandler;

/**
 * Java Content Repository (JCR)-based mail storage protocol handler.
 * 
 * A JCR-based store, accessible via JNDI, would have the following url structure:
 * 
 * <pre>mstor://user:password@localhost:1099/mail#mailbox</pre>
 * 
 * @author Ben
 *
 */
public class RepositoryHandler extends AbstractProtocolHandler {

    public static final String NAMESPACE = "mstor";

    public static final String NAMESPACE_URL = "http://mstor.sourceforge.net/mstor/1.0";

    /**
     * Constants defining node names.
     */
    public static class NodeNames {
        
        // folder-specific nodes..
        public static final String FOLDER = NAMESPACE + ':' + "folder";
        
        public static final String MESSAGE = NAMESPACE + ':' + "message";
        
        public static final String CONTENT = NAMESPACE + ':' + "content";
        
        // message-specific nodes..
        public static final String HEADER = NAMESPACE + ':' + "header";
        
        public static final String FLAG = NAMESPACE + ':' + "flag";
    }

    /**
     * Constants defining property names.
     */
    public static class PropertyNames {
        
        public static final String NAME = NAMESPACE + ':' + "name";
        
        // folder-specific properties..
        public static final String TYPE = NAMESPACE + ':' + "type";
        
        public static final String LAST_UID = NAMESPACE + ':' + "last-uid";
        
        public static final String UID_VALIDITY = NAMESPACE + ':' + "uid-validity";
        
        // message-specific properties..
        public static final String VALUE = NAMESPACE + ':' + "value";
        
        public static final String MESSAGE_NUMBER = NAMESPACE + ':' + "messageNumber";
        
        public static final String RECEIVED = NAMESPACE + ':' + "received";
        
        public static final String REPLIED = NAMESPACE + ':' + "replied";
        
        public static final String FOWARDED = NAMESPACE + ':' + "forwarded";
        
        public static final String UID = NAMESPACE + ':' + "uid";
        
        public static final String EXPUNGED = NAMESPACE + ':' + "expunged";
        
    }
    
    private Repository repository;
    
    private javax.jcr.Session repositorySession;
    
    /**
     * @param url
     * @param store
     * @param session
     */
    public RepositoryHandler(URLName url, MStorStore store) {
        super(url, store);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#connect(java.lang.String, int, java.lang.String, java.lang.String)
     */
    public boolean connect(String host, int port, String user, String password)
            throws AuthenticationFailedException, MessagingException {

        try {
            if (user != null) {
                Credentials credentials = new SimpleCredentials(user, password.toCharArray());
                repositorySession = getRepository().login(credentials);
            }
            else {
                // login anonymously..
                repositorySession = getRepository().login();
            }
            Workspace ws = repositorySession.getWorkspace();
            ws.getNamespaceRegistry().registerNamespace(NAMESPACE, NAMESPACE_URL);
            return true;
        }
        catch (LoginException le) {
            throw new AuthenticationFailedException(
                    "Error authenticating user: " + le.getMessage());
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error authenicating user", re);
        }
        catch (NamingException ne) {
            throw new MessagingException("Error locating repository", ne);
        }
//        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#disconnect()
     */
    public void disconnect() throws MessagingException {
        repositorySession.logout();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getDefaultFolder()
     */
    public Folder getDefaultFolder() throws MessagingException {
        try {
            if (url.getRef() != null) {
                return new MStorFolder(store, new RepositoryFolder(
                        repositorySession.getRootNode().getNode(url.getRef())));
            }
            else {
                return new MStorFolder(store, new RepositoryFolder(
                        repositorySession.getRootNode()));
            }
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error retrieving default folder node", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(java.lang.String)
     */
    public Folder getFolder(String name) throws MessagingException {
//        try {
//            Node folderNode = repositorySession.getRootNode().getNode(name);
            return getDefaultFolder().getFolder(name);
//        }
//        catch (RepositoryException re) {
//            throw new MessagingException("Error retrieving folder node", re);
//        }
//        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.ProtocolHandler#getFolder(javax.mail.URLName)
     */
    public Folder getFolder(URLName url) throws MessagingException {
        return getDefaultFolder().getFolder(url.getFile());
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
            Context context = new InitialContext(env);
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
