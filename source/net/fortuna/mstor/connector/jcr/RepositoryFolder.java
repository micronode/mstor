/*
 * $Id$
 *
 * Created on 21/07/2007
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.connector.AbstractFolderDelegate;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.connector.jcr.RepositoryConnector.NodeNames;
import net.fortuna.mstor.connector.jcr.RepositoryConnector.PropertyNames;
import net.fortuna.mstor.connector.jcr.query.GetFolderQueryBuilder;
import net.fortuna.mstor.connector.jcr.query.GetMessageQueryBuilder;
import net.fortuna.mstor.connector.jcr.query.ListFoldersQueryBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 */
public class RepositoryFolder extends AbstractFolderDelegate {
    
    private Log log = LogFactory.getLog(RepositoryFolder.class);
    
    private Node node;
    
    private boolean root;

    /**
     * @param node
     */
    public RepositoryFolder(Node node) {
        this(node, false);
    }

    /**
     * @param node
     * @param root indicates whether this is the root folder
     */
    public RepositoryFolder(Node node, boolean root) {
        this.node = node;
        this.root = root;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getType()
     */
    public int getType() {
        try {
            return (int) node.getProperty(PropertyNames.TYPE).getLong();
        }
        catch (RepositoryException re) {
            log.error("Error retrieving folder type", re);
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getName()
     */
    public String getName() {
        try {
            return node.getProperty(PropertyNames.NAME).getString();
//            return node.getName();
        }
        catch (RepositoryException re) {
            log.error("Error retrieving folder name", re);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFullName()
     */
    public String getFullName() {
        StringBuffer b = new StringBuffer(getName());
        FolderDelegate parent = getParent();
        while (parent != null) {
            b.insert(0, getSeparator());
            b.insert(0, parent.getName());
            parent = parent.getParent();
        }
        b.insert(0, "/");
        return b.toString();

        /*
        try {
            return node.getPath();
        }
        catch (RepositoryException re) {
            log.error("Error retrieving folder name", re);
        }
        return null;
        */
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getParent()
     */
    public FolderDelegate getParent() {
        if (!root) {
            try {
//                Node parentNode = node.getParent();
//                if (!parentNode.equals(node.getSession().getRootNode())) {
                return new RepositoryFolder(node.getParent());
//                }
            }
            catch (RepositoryException re) {
                log.error("Error retrieving folder parent", re);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate getFolder(String name) throws MessagingException {
        try {
            Node folderNode = null;
            
//            String queryString = node.getPath() + getSeparator() + NodeNames.FOLDER
//                    + '[' + PropertyNames.NAME + '=' + name + ']';
            
//            Query folderQuery = queryManager.createQuery(queryString, Query.XPATH);
            Query query = new GetFolderQueryBuilder(node, name).build();
            NodeIterator nodes = query.execute().getNodes();
            if (nodes.hasNext()) {
                folderNode = nodes.nextNode();
            }
            else {
                folderNode = node.addNode(NodeNames.FOLDER); //, "nt:folder");
                folderNode.addMixin("mix:referenceable");
                folderNode.setProperty(PropertyNames.NAME, name);
            }
            return new RepositoryFolder(folderNode);
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error retrieving folder [" + name + "]", re);
        }
//        return null;
/*
        try {
            if (node.hasNode(name)) {
                return new RepositoryFolder(node.getNode(name));
            }
            return new RepositoryFolder(node.addNode(name));
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error retrieving folder [" + name + "]", re);
        }
*/
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate[] list(String pattern) {
        List folders = new ArrayList();
        try {
//            NodeIterator ni = node.getNodes(NodeNames.FOLDER);
//            NodeIterator ni = node.getNodes(pattern);
//            Query query = queryManager.createQuery(node.getPath()+ "//element(*," + NodeNames.FOLDER + ")",
//                    Query.XPATH);
//            Query query = queryManager.createQuery(node.getPath() + getSeparator() + NodeNames.FOLDER
//                    + '[' + PropertyNames.NAME + '=' + pattern + ']', Query.XPATH);
            Query query = new ListFoldersQueryBuilder(node, pattern).build();

            NodeIterator ni = query.execute().getNodes();
            while (ni.hasNext()) {
                folders.add(new RepositoryFolder(ni.nextNode()));
            }
        }
        catch (RepositoryException re) {
            log.error("Error listing folders", re);
        }
        return (FolderDelegate[]) folders.toArray(
                new FolderDelegate[folders.size()]);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#exists()
     */
    public boolean exists() {
        return !node.isNew();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#delete()
     */
    public boolean delete() {
        try {
            node.remove();
            return true;
        }
        catch (RepositoryException re) {
            log.error("Error deleting folder", re);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#renameTo(java.lang.String)
     */
    public boolean renameTo(String name) {
        try {
            node.setProperty(PropertyNames.NAME, name);
            return true;
        }
        catch (RepositoryException re) {
            log.error("Error renaming folder", re);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#open(int)
     */
    public void open(int mode) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#close()
     */
    public void close() throws MessagingException {
        try {
            node.getSession().save();
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error closing folder", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getSeparator()
     */
    public char getSeparator() {
        return '/';
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageCount()
     */
    public int getMessageCount() throws MessagingException {
        try {
            return (int) node.getNodes(NodeNames.MESSAGE).getSize();
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error retrieving message count", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageAsStream(int)
     */
    public InputStream getMessageAsStream(int index) throws IOException {
        try {
//            Node messageNode = node.getNode(NodeNames.MESSAGE + '[' + index + ']');
//            return messageNode.getProperty(NodeNames.CONTENT).getStream();
            NodeIterator messageNodes = node.getNodes(NodeNames.MESSAGE);
            Node messageNode = null;
            int i = 0;
            while (messageNodes.hasNext()) {
                messageNode = messageNodes.nextNode();
                if (i++ == index) {
                    break;
                }
            }
            if (messageNode != null) {
                Node contentNode = null;
                try {
                    contentNode = messageNode.getNode("jcr:content");
                } catch (PathNotFoundException e) {
                    contentNode = messageNode.getProperty("jcr:content").getNode();
                }
                return contentNode.getProperty("jcr:data").getStream();
            }
            return null;
        }
        catch (RepositoryException re) {
            log.error("Error retrieving message stream", re);
            throw new IOException("Error retrieving message stream", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#appendMessages(javax.mail.Message[])
     */
    public void appendMessages(Message[] messages) throws MessagingException {
        try {
            for (int i = 0; i < messages.length; i++) {
                Node messageNode = node.addNode(NodeNames.MESSAGE); //, "nt:file");
                Node contentNode = messageNode.addNode("jcr:content", "nt:resource");
                contentNode.setProperty("jcr:mimeType", messages[i].getContentType());
//                contentNode.setProperty("jcr:encoding", arg1);
                ByteArrayOutputStream contentOut = new ByteArrayOutputStream();
                messages[i].writeTo(contentOut);
                ByteArrayInputStream contentIn = new ByteArrayInputStream(contentOut.toByteArray());
                contentNode.setProperty("jcr:data", contentIn);
                contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
                node.save();
            }
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error appending messages", re);
        }
        catch (IOException ioe) {
            throw new MessagingException("Error appending messages", ioe);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#create(int)
     */
    public boolean create(int type) throws MessagingException {
        try {
            node.setProperty(PropertyNames.TYPE, type);
            node.getParent().save();
            return true;
        }
        catch (RepositoryException re) {
            throw new MessagingException("Error creating folder", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#expunge(javax.mail.Message[])
     */
    public void expunge(Message[] deleted) throws MessagingException {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.AbstractFolderDelegate#createMetaMessage(int)
     */
    protected MessageDelegate createMessage(int messageNumber) throws DelegateException {
        try {
            Node messageNode = node.addNode(NodeNames.MESSAGE); //, "nt:file");
            messageNode.setProperty(PropertyNames.MESSAGE_NUMBER, messageNumber);
            return new RepositoryMessage(node);
        }
        catch (RepositoryException re) {
            throw new DelegateException("Error intitialising message", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.AbstractFolderDelegate#getMetaMessage(int)
     */
    public MessageDelegate getMessage(int messageNumber) throws DelegateException {
        try {
            /*
            NodeIterator messageNodes = node.getNodes(NodeNames.MESSAGE);
            while (messageNodes.hasNext()) {
                Node messageNode = messageNodes.nextNode();
                if (messageNode.getProperty("messageNumber").getLong() == messageNumber) {
                    return new RepositoryMessage(messageNode);
                }
            }
            */
            Query query = new GetMessageQueryBuilder(node, messageNumber).build();
            NodeIterator nodes = query.execute().getNodes();
            if (nodes.hasNext()) {
                return new RepositoryMessage(nodes.nextNode());
            }
        }
        catch (RepositoryException re) {
            throw new DelegateException("Error retrieving message", re);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.AbstractFolderDelegate#setLastUid(long)
     */
    protected void setLastUid(long uid) {
        try {
            node.setProperty(PropertyNames.LAST_UID, uid);
        }
        catch (RepositoryException re) {
            log.error("Error updating last UID", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getLastUid()
     */
    public long getLastUid() {
        try {
            try {
                return node.getProperty(PropertyNames.LAST_UID).getLong();
            }
            catch (PathNotFoundException pnfe) {
                setLastUid(0);
            }
            return node.getProperty(PropertyNames.LAST_UID).getLong();
        }
        catch (RepositoryException re) {
            log.error("Error retreiving last UID", re);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getUidValidity()
     */
    public long getUidValidity() {
        try {
            return node.getProperty(PropertyNames.UID_VALIDITY).getLong();
        }
        catch (RepositoryException re) {
            log.error("Error retreiving UID validity", re);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getLastModified()
     */
    public long getLastModified() throws UnsupportedOperationException {
//        throw new UnsupportedOperationException();
        try {
            try {
                node = node.getNode("jcr:content");
            } catch (PathNotFoundException e) {
                node = node.getProperty("jcr:content").getNode();
            }
            return node.getProperty("jcr:lastModified").getDate().getTime().getTime();
        }
        catch (RepositoryException re) {
            log.error("Error retrieving timestamp", re);
        }
        return 0;
    }
}
