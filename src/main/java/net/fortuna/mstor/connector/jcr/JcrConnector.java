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
package net.fortuna.mstor.connector.jcr;

import net.fortuna.mstor.connector.AbstractProtocolConnector;
import net.fortuna.mstor.model.MStorFolder;
import net.fortuna.mstor.model.MStorStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.rmi.client.ClientRepositoryFactory;
import org.jcrom.Jcrom;

import javax.jcr.Session;
import javax.jcr.*;
import javax.mail.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 22/01/2009
 * </pre>
 * 
 *
 */
public class JcrConnector extends AbstractProtocolConnector {

    private final Jcrom jcrom;

    private Session session;
    
    private final javax.mail.Session mailSession;
    
    /**
     * @param url
     * @param store
     */
    public JcrConnector(URLName url, MStorStore store, javax.mail.Session session) {
        super(url, store);
        this.mailSession = session;
        
        this.jcrom = new Jcrom();
        jcrom.map(JcrFolder.class);
        jcrom.map(JcrMessage.class);
    }

    /**
     * {@inheritDoc}
     */
    public boolean connect() throws MessagingException {
        Repository repository;

        if (StringUtils.isEmpty(mailSession.getProperty("mstor.repository.provider.url"))) {
            try {
                Context context = new InitialContext();
                repository = (Repository) context.lookup(mailSession.getProperty("mstor.repository.name"));
            }
            catch (NamingException ne) {
                throw new MessagingException("Error locating repository", ne);
            }
        }
        else {
            try {
                ClientRepositoryFactory factory = new ClientRepositoryFactory();
                repository = factory.getRepository(mailSession.getProperty("mstor.repository.provider.url") + "/" + mailSession.getProperty("mstor.repository.name"));
            }
            catch (Exception e) {
                throw new MessagingException("Error locating repository", e);
            }
        }
        
        PasswordAuthentication auth = mailSession.requestPasswordAuthentication(null, 0, "mstor", null, null);
//        PasswordAuthentication auth = session.getPasswordAuthentication(url);
        
        // may be null, in which case the default workspace is used..
//        String workspaceName = mailSession.getProperty("mstor.repository.workspace");
            
        try {
            if (auth != null) {
                Credentials credentials = new SimpleCredentials(auth.getUserName(), auth.getPassword().toCharArray());
                session = repository.login(credentials);
            }
            else {
                // login anonymously..
                session = repository.login();
            }
            return true;
        }
        catch (LoginException le) {
            throw new AuthenticationFailedException(
                    "Error authenticating user: " + le.getMessage());
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error authenicating user", re);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect() {
        session.logout();
    }

    /**
     * {@inheritDoc}
     */
    public Folder getDefaultFolder() throws MessagingException {
        JcrFolder delegate = jcrom.fromNode(JcrFolder.class, getNode());
        delegate.setConnector(this);
        if (!delegate.exists()) {
            delegate.create(Folder.HOLDS_FOLDERS);
        }
        return new MStorFolder(store, delegate);
    }

    /**
     * {@inheritDoc}
     */
    public Folder getFolder(String name) throws MessagingException {
        return getDefaultFolder().getFolder(name);
    }

    /**
     * {@inheritDoc}
     */
    public Folder getFolder(URLName url) throws MessagingException {
        return getDefaultFolder().getFolder(url.getFile());
    }

    /**
     * @return the jcrom instance
     */
    Jcrom getJcrom() {
        return jcrom;
    }

    /**
     * @return
     */
    Session getSession() {
        return session;
    }

    private void assertConnected() throws MessagingException {
        if (session == null) {
            throw new MessagingException("Not connected");
        }
    }
    
    private Node getNode() throws MessagingException {
        assertConnected();
        String path = mailSession.getProperty("mstor.repository.path");
        if (path != null) {
            try {
                try {
                    return session.getRootNode().getNode(path);
                }
                catch (RepositoryException e) {
                    if (e instanceof PathNotFoundException) {
                        throw (PathNotFoundException) e;
                    }
                    throw new MessagingException("Unexpected error", e);
                }
            }
            catch (PathNotFoundException pnfe) {
                if ("true".equals(mailSession.getProperty("mstor.repository.create"))) {
                    try {
                        session.getRootNode().addNode(path);
//                        session.save();
                        return session.getRootNode().getNode(path);
                    }
                    catch (RepositoryException e) {
                        throw new MessagingException("Unexpected error", e);
                    }
                }
            }
        }
        try {
            return session.getRootNode();
        }
        catch (RepositoryException e) {
            throw new MessagingException("Unexpected error", e);
        }
    }
}
