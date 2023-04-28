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
package org.mstor.connector.jcr;

import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.ArrayUtils;
import org.jcrom.AbstractJcrEntity;
import org.jcrom.JcrMappingException;
import org.jcrom.annotations.JcrProperty;
import org.mstor.provider.mail.FolderDelegate;
import org.mstor.provider.mail.MessageDelegate;
import org.mstor.provider.mail.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
public class JcrFolder extends AbstractJcrEntity implements FolderDelegate<JcrMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(JcrFolder.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 4533514532820747829L;

    @JcrProperty private String folderName;

    @JcrProperty private Integer type;

    @JcrProperty private Long lastUid;

    @JcrProperty private Long uidValidity;

//    @JcrChildNode private List<JcrFolder> folders;

//    @JcrChildNode private List<JcrMessage> messages;
    
    private JcrFolder parent;
    
    private transient JcrConnector connector;
    
    private volatile transient JcrFolderDao folderDao;
    
    private volatile transient JcrMessageDao messageDao;
    
    private int messageCount;
    
    /**
     * 
     */
    public JcrFolder() {
//        this.folders = new ArrayList<JcrFolder>();
//        this.messages = new ArrayList<JcrMessage>();
        messageCount = -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized long allocateUid(MessageDelegate message) throws UnsupportedOperationException {
        long uid = lastUid + 1;
        message.setUid(uid);
        lastUid = uid;
        return uid;
    }

    /**
     * {@inheritDoc}
     */
    public void appendMessages(Message[] messages) throws MessagingException {
    	final Date defaultReceivedDate = new Date();
    	
        for (Message message : messages) {
            try {
                JcrMessage jcrMessage = null;
                boolean update = false;
                
                String messageId = MessageUtils.getMessageId(message);
                if (messageId != null) {
                    List<JcrMessage> jcrMessages = getMessageDao().findByMessageId(getConnector().getJcrom().getPath(this) + "/messages", messageId);
                    if (jcrMessages.size() > 0) {
                        jcrMessage = jcrMessages.get(0);
                        update = true;
                    }
                }
                
                if (jcrMessage == null) {
                    jcrMessage = new JcrMessage();
                    jcrMessage.setMessageNumber((int) getMessageDao().getSize(getConnector().getJcrom().getPath(this) + "/messages") + 1);
                    allocateUid(jcrMessage);
                }
                jcrMessage.setFlags(message.getFlags());
                jcrMessage.setHeaders(message.getAllHeaders());
                if (message.getReceivedDate() != null) {
                    jcrMessage.setReceived(message.getReceivedDate());
                }
                else {
                    jcrMessage.setReceived(defaultReceivedDate);
                }
                jcrMessage.setExpunged(message.isExpunged());
                jcrMessage.setMessage(message);
                
                String[] inReplyTo = message.getHeader("In-Reply-To");
                if (!ArrayUtils.isEmpty(inReplyTo)) {
                    List<JcrMessage> inReplyToMessages = getMessageDao().findByMessageId(getConnector().getJcrom().getPath(getRootFolder()) + "/", inReplyTo[0]);
                    if (!inReplyToMessages.isEmpty()) {
                        jcrMessage.setInReplyTo(inReplyToMessages.get(0));
                        inReplyToMessages.get(0).getReferences().add(jcrMessage);
//                        getMessageDao().update(inReplyToMessages.get(0));
                    }
                }
                
                String[] references = message.getHeader("References");
                if (!ArrayUtils.isEmpty(references)) {
                    for (String referenced : references[0].split(",")) {
                        List<JcrMessage> referencedMessages = getMessageDao().findByMessageId(getConnector().getJcrom().getPath(getRootFolder()) + "/", referenced.trim());
                        if (!referencedMessages.isEmpty()) {
                            jcrMessage.getReferences().addAll(referencedMessages);
                        }
                    }
                }
                
//                this.messages.add(jcrMessage);
                if (update) {
                    getMessageDao().update(jcrMessage);
                }
                else {
                    getMessageDao().create(getConnector().getJcrom().getPath(this) + "/messages", jcrMessage);
                }
            }
            catch (IOException e) {
                LOG.error("Unexpected error", e);
            }
        }
        saveChanges();

        // reset cached message count..
        messageCount = -1;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        saveChanges();
    }

    /**
     * {@inheritDoc}
     */
    public boolean create(int type) throws MessagingException {
        this.type = type;
        
        // add non-root folders to parent..
        if (parent != null) {
//            try {
//                connector.getJcrom().addNode(parent.getNode(), this);
            getFolderDao().create(getConnector().getJcrom().getPath(parent) + "/folders", this);
//            }
//            catch (RepositoryException e) {
//                throw new MessagingException("Error initialising folder", e);
//            }
        }
        
        if ((Folder.HOLDS_FOLDERS & type) > 0) {
//            folders = new ArrayList<JcrFolder>();
            try {
                getNode().addNode("folders");
            }
            catch (RepositoryException re) {
                throw new MessagingException("Error initialising path", re);
            }
        }
        if ((Folder.HOLDS_MESSAGES & type) > 0) {
//            messages = new ArrayList<JcrMessage>();
            try {
                getNode().addNode("messages");
            }
            catch (RepositoryException re) {
                throw new MessagingException("Error initialising path", re);
            }
        }
        
        try {
            if (parent != null) {
                parent.saveChanges();
            }
            else {
                getConnector().getSession().save();
            }
            saveChanges();
        }
        catch (RepositoryException e) {
            throw new MessagingException("Unexpected error", e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete() {
//        return parent.folders.remove(this);
        getFolderDao().remove(getConnector().getJcrom().getPath(this));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        try {
            Node node = getNode();
            return node != null && !node.isNew();
        }
        catch (Exception e) {
            LOG.error("Unexpected error", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void expunge(Message[] deleted) {
//        for (JcrMessage jcrMessage : messages) {
            for (Message message : deleted) {
//                if (jcrMessage.getMessageNumber() == message.getMessageNumber()) {
//                    jcrMessage.setExpunged(true);
//                }
                List<JcrMessage> messages = getMessageDao().findByMessageNumber(getConnector().getJcrom().getPath(this) + "/messages",
                        message.getMessageNumber());
                for (JcrMessage jcrMessage : messages) {
                    jcrMessage.setExpunged(true);
                }
            }
//        }
            
        // reset cached message count..
        messageCount = -1;
    }

    /**
     * {@inheritDoc}
     */
    public FolderDelegate<JcrMessage> getFolder(String name) {
        JcrFolder retVal = null;

//        List<JcrFolder> folders = getFolderDao().findAll(getConnector().getJcrom().getPath(this) + "/folders");
//        for (JcrFolder folder : folders) {
//            if (folder.getName().equals(name)) {
//                retVal = folder;
//                break;
//            }
//        }
        List<JcrFolder> folders = getFolderDao().findByName(getConnector().getJcrom().getPath(this) + "/folders", name);
        if (!folders.isEmpty()) {
            retVal = folders.get(0);
        }
        
        if (retVal == null) {
            retVal = new JcrFolder();
            retVal.folderName = name;
            retVal.setName(name);
//            retVal.setPath("folders/" + name);
//            folders.add(retVal);
        }
        retVal.setParent(this);
        return retVal;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getFullName() {
        return getConnector().getJcrom().getPath(this);
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastUid() throws UnsupportedOperationException {
        if (lastUid == null) {
            lastUid = 1L;
        }
        return lastUid;
    }

    private void allocateUid(JcrMessage message) {
        long uid = getLastUid() + 1;
        message.setUid(uid);
        lastUid = uid;
    }
    
    /**
     * {@inheritDoc}
     */
    public JcrMessage getMessage(int messageNumber) {
        List<JcrMessage> messages = getMessageDao().findByMessageNumber(getConnector().getJcrom().getPath(this) + "/messages",
                messageNumber);
        
//        for (JcrMessage message : messages) {
//            if (message.getMessageNumber() == messageNumber) {
//                return message;
//            }
//        }
        if (messages.size() > 0) {
            return messages.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getMessageAsStream(int index) {
        List<JcrMessage> messages = getMessageDao().findAll(getConnector().getJcrom().getPath(this) + "/messages",
                index - 1, 1);
        if (!messages.isEmpty()) {
            return messages.get(0).getInputStream();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getMessageCount() {
//        return messages.size();
        if (messageCount < 0) {
            messageCount = (int) getMessageDao().getSize(getConnector().getJcrom().getPath(this) + "/messages");
        }
        return messageCount;
    }

    /**
     * {@inheritDoc}
     */
    public int getDeletedMessageCount() throws UnsupportedOperationException {
        return getMessageDao().findByFlag(getConnector().getJcrom().getPath(this) + "/messages", Flag.DELETED).size();
    }
    
    /**
     * {@inheritDoc}
     */
    public FolderDelegate<JcrMessage> getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public char getSeparator() {
        return '/';
    }

    /**
     * {@inheritDoc}
     */
    public int getType() {
    	if (type != null) {
            return type;
    	}
    	return -1;
    }

    /**
     * {@inheritDoc}
     */
    public long getUidValidity() throws UnsupportedOperationException {
        return uidValidity;
    }

    /**
     * {@inheritDoc}
     */
    public FolderDelegate<JcrMessage>[] list(String pattern) {
        List<JcrFolder> folders = getFolderDao().findByPattern(getConnector().getJcrom().getPath(this) + "/folders", pattern);
        for (JcrFolder folder : folders) {
            folder.setParent(this);
        }
        return folders.toArray(new JcrFolder[0]);
    }

    /**
     * {@inheritDoc}
     */
    public void open(int mode) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean renameTo(String name) {
        this.folderName = name;
        return true;
    }

    /**
     * @return the folderName
     */
    public final String getFolderName() {
        return folderName;
    }

    private Node getNode() throws JcrMappingException, RepositoryException {
        String path = getConnector().getJcrom().getPath(this);
        if (path != null) {
            return getConnector().getSession().getRootNode().getNode(path.substring(1));
        }
        return null;
    }

    private JcrFolderDao getFolderDao() {
        if (folderDao == null) {
            synchronized (this) {
                if (folderDao == null) {
                    folderDao = new JcrFolderDao(getConnector().getSession(), getConnector().getJcrom());
                }
            }
        }
        return folderDao;
    }
    
    private JcrMessageDao getMessageDao() {
        if (messageDao == null) {
            synchronized (this) {
                if (messageDao == null) {
                    messageDao = new JcrMessageDao(getConnector().getSession(), getConnector().getJcrom());
                }
            }
        }
        return messageDao;
    }
    
    private FolderDelegate<JcrMessage> getRootFolder() {
        FolderDelegate<JcrMessage> root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }
    
    /**
     * @param connector the connector to set
     */
    void setConnector(JcrConnector connector) {
        this.connector = connector;
    }
    
    private JcrConnector getConnector() {
        if (connector == null && parent != null) {
            return parent.getConnector();
        }
        return connector;
    }
    
    private void saveChanges() throws JcrMappingException {
//        getConnector().getJcrom().updateNode(getNode(), this);
//        getNode().save();
        getFolderDao().update(this);
    }

    /**
     * @param parent the parent to set
     */
    void setParent(JcrFolder parent) {
        this.parent = parent;
    }
}
