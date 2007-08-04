/*
 * $Id$
 *
 * Created: 18/08/2004
 *
 * Copyright (c) 2004, Ben Fortuna
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
package net.fortuna.mstor.delegate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.FolderDelegate;
import net.fortuna.mstor.MessageDelegate;
import net.fortuna.mstor.data.xml.DocumentBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A JDOM-based implementation of a meta folder.
 * 
 * @author benfortuna
 */
public class MetaFolder extends AbstractFolderDelegate {

    private static final String ELEMENT_FOLDER = "folder";

    private static final String ATTRIBUTE_FOLDER_NAME = "name";

    private static final String ELEMENT_LAST_UID = "last-uid";

    private static final String ELEMENT_UID_VALIDITY = "uid-validity";

    private Log log = LogFactory.getLog(MetaFolder.class);

    public static final String FILE_EXTENSION = ".emf";

    private static final Random UID_VALIDITY_GENERATOR = new Random();

    private File file;
    
    private DocumentBinding binding;
    
    /**
     * A delegate used by metafolder to perform operations not supported in metadata.
     */
    private FolderDelegate delegate;
    
    /**
     * Constructs a new meta folder instance.
     * 
     * @param file the meta folder file
     */
    public MetaFolder(FolderDelegate delegate) {
        this.file = getMetaFile(delegate);
        this.delegate = delegate;
        binding = new DocumentBinding(file, ELEMENT_FOLDER);
    }

    /**
     * Constructs a new meta folder instance with the specified namespace.
     * 
     * @param file the meta folder file
     * @param namespace the namespace for the metadata
     */
    public MetaFolder(final Namespace namespace, FolderDelegate delegate) {
        this.file = getMetaFile(delegate);
        this.delegate = delegate;
        binding = new DocumentBinding(file, namespace, ELEMENT_FOLDER);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getType()
     */
    public int getType() {
        return delegate.getType();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#getName()
     */
    public final String getName() {
//        return binding.getDocument().getRootElement().getAttributeValue(
//                ATTRIBUTE_FOLDER_NAME);
        return delegate.getName();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFullName()
     */
    public String getFullName() {
        return delegate.getFullName();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getParent()
     */
    public FolderDelegate getParent() {
        return new MetaFolder(delegate.getParent());
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate getFolder(String name) {
        return new MetaFolder(delegate.getFolder(name));
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate[] list(String pattern) {
        List folders = new ArrayList();
        
        FolderDelegate[] delegateList = delegate.list(pattern);
        for (int i = 0; i < delegateList.length; i++) {
            folders.add(new MetaFolder(delegateList[i]));
        }
        
        return (FolderDelegate[]) folders.toArray(
                new FolderDelegate[folders.size()]);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#exists()
     */
    public boolean exists() {
        return delegate.exists();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#delete()
     */
    public boolean delete() {
        return delegate.delete() && file.delete();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#renameTo(java.lang.String)
     */
    public boolean renameTo(String name) {
        return delegate.renameTo(name)
            && (!file.exists() || file.renameTo(
                    new File(file.getParent(), name + FILE_EXTENSION)));
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#open(int)
     */
    public void open(int mode) {
        delegate.open(mode);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#close()
     */
    public void close() throws MessagingException {
        delegate.close();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getSeparator()
     */
    public char getSeparator() {
        return delegate.getSeparator();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageCount()
     */
    public int getMessageCount() throws MessagingException {
        return delegate.getMessageCount();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageAsStream(int)
     */
    public InputStream getMessageAsStream(int index) throws IOException {
        return delegate.getMessageAsStream(index);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#appendMessages(javax.mail.Message[])
     */
    public void appendMessages(Message[] messages) throws MessagingException {
        try {
            Date received = new Date();
            for (int i = 0; i < messages.length; i++) {
                MessageDelegate md = getMessage(messages[i].getMessageNumber());
                md.setReceived(received);
                md.setFlags(messages[i].getFlags());
                md.setHeaders(messages[i].getAllHeaders());
                allocateUid(md);
            }
            delegate.appendMessages(messages);
            save();
        }
        catch (DelegateException de) {
            throw new MessagingException("Error saving changes", de);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#create(int)
     */
    public boolean create(int type) throws MessagingException {
        return delegate.create(type);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#expunge(javax.mail.Message[])
     */
    public void expunge(Message[] deleted) throws MessagingException {
        delegate.expunge(deleted);
        removeMessages(deleted);
    }
    
    /**
     * @param delegate
     * @return
     */
    private File getMetaFile(FolderDelegate delegate) {
        File metaFile = new File(delegate.getFullName() + FILE_EXTENSION);
        return metaFile;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#setName(java.lang.String)
     */
    /*
    private final void setName(final String name) {
        binding.getDocument().getRootElement()
                .setAttribute(ATTRIBUTE_FOLDER_NAME, name);
    }
    */

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#getMessage(javax.mail.Message)
     */
    public final MessageDelegate getMessage(int messageNumber)
        throws DelegateException {

        MessageDelegate md = null;
        
        for (Iterator i = binding.getDocument().getRootElement().getChildren(
                MetaMessage.ELEMENT_MESSAGE, binding.getNamespace()).iterator();
                i.hasNext();) {

            Element messageElement = (Element) i.next();
            try {
                if (Integer.parseInt(messageElement.getAttributeValue(
                        MetaMessage.ATTRIBUTE_MESSAGE_NUMBER)) == messageNumber) {
                    
                    md = new MetaMessage(messageElement, this, binding.getNamespace());
                    break;
                }
            }
            catch (Exception e) {
                throw new DelegateException("Caught exception parsing message number", e);
            }
        }
        
        if (md == null) {
            md = createMessage(messageNumber);
            // allocate a new UID for the message..
            allocateUid(md);
        }
        return md;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.AbstractFolderDelegate#createMetaMessage(int)
     */
    protected MessageDelegate createMessage(int messageNumber) {
        MessageDelegate delegate = new MetaMessage(messageNumber, this, binding.getNamespace());
        // only add the metadata if message is associated with folder..
        if (messageNumber > 0) {
            addMessage(delegate);
        }
        return delegate;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#addMessage(net.fortuna.mstor.data.MetaMessage)
     */
    private final void addMessage(final MessageDelegate message) {
        binding.getDocument().getRootElement().addContent(
                ((MetaMessage) message).getElement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#removeMessage(java.lang.String)
     */
    private final MessageDelegate removeMessage(final int messageNumber) {
        for (Iterator i = binding.getDocument().getRootElement().getChildren(
                MetaMessage.ELEMENT_MESSAGE, binding.getNamespace()).iterator(); i
                .hasNext();) {

            Element messageElement = (Element) i.next();
            if (Integer
                    .parseInt(messageElement
                            .getAttributeValue(MetaMessage.ATTRIBUTE_MESSAGE_NUMBER)) == messageNumber) {
                binding.getDocument().getRootElement().removeContent(messageElement);
                updateMessageNumbers(messageNumber, -1);
                return new MetaMessage(messageElement, this, binding.getNamespace());
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.MetaFolder#removeMessages(int[])
     */
    private final MessageDelegate[] removeMessages(Message[] messages) {
        List metas = new ArrayList();
        int delta = 0;
        int startIndex = Integer.MAX_VALUE;
        for (Iterator i = binding.getDocument().getRootElement().getChildren(
                MetaMessage.ELEMENT_MESSAGE, binding.getNamespace()).iterator(); i
                .hasNext();) {

            Element messageElement = (Element) i.next();
            int messageNumber = Integer
                    .parseInt(messageElement
                            .getAttributeValue(MetaMessage.ATTRIBUTE_MESSAGE_NUMBER));

            for (int n = 0; n < messages.length; n++) {
                if (messages[n].getMessageNumber() == messageNumber) {
                    binding.getDocument().getRootElement()
                            .removeContent(messageElement);
                    metas.add(new MetaMessage(messageElement, this,
                            binding.getNamespace()));
                    delta--;
                    if (messageNumber < startIndex) {
                        startIndex = messageNumber;
                    }
                }
            }
            updateMessageNumbers(startIndex, delta);
            return (MetaMessage[]) metas.toArray(new MetaMessage[metas.size()]);
        }
        return null;
    }

    /**
     * Updates all message numbers according to the specified arguments. Used when message metadata
     * is removed from the list.
     * 
     * @param startIndex anything greater than (or equal to) the start index is affected
     * @param delta amount to adjust relevant message numbers by
     */
    private void updateMessageNumbers(final int startIndex, final int delta) {
        for (Iterator i = binding.getDocument().getRootElement().getChildren(
                MetaMessage.ELEMENT_MESSAGE, binding.getNamespace()).iterator(); i
                .hasNext();) {
            Element messageElement = (Element) i.next();
            int messageNumber = Integer
                    .parseInt(messageElement
                            .getAttributeValue(MetaMessage.ATTRIBUTE_MESSAGE_NUMBER));
            if (messageNumber >= startIndex) {
                messageElement.setAttribute(
                        MetaMessage.ATTRIBUTE_MESSAGE_NUMBER, String
                                .valueOf(messageNumber + delta));
            }
        }
    }

    /**
     * Returns the element storing the last allocated message UID.
     * 
     * @return
     */
    private Element getLastUidElement() {
        Element lastUidElement = binding.getDocument().getRootElement().getChild(
                ELEMENT_LAST_UID, binding.getNamespace());
        if (lastUidElement == null) {
            lastUidElement = new Element(ELEMENT_LAST_UID, binding.getNamespace());
            lastUidElement.setText("0");
            binding.getDocument().getRootElement().addContent(lastUidElement);
        }
        return lastUidElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.MetaFolder#getLastUid()
     */
    public final long getLastUid() {
        Element lastUidElement = getLastUidElement();
        return Long.parseLong(lastUidElement.getText());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.MetaFolder#getUidValidity()
     */
    public final long getUidValidity() throws UnsupportedOperationException,
        MessagingException {
        
        Element uidValidityElement = binding.getDocument().getRootElement().getChild(
                ELEMENT_UID_VALIDITY, binding.getNamespace());
        
        if (uidValidityElement == null) {
            uidValidityElement = new Element(ELEMENT_UID_VALIDITY, binding.getNamespace());
            uidValidityElement.setText(String.valueOf(UID_VALIDITY_GENERATOR
                    .nextInt(Integer.MAX_VALUE)));
            binding.getDocument().getRootElement().addContent(uidValidityElement);
            
            try {
                save();
            }
            catch (DelegateException de) {
                throw new MessagingException("Error in delegate", de);
            }
        }
        return Long.parseLong(uidValidityElement.getText());
    }

    /**
     * @param uid
     */
    protected void setLastUid(long uid) throws DelegateException {
        Element lastUidElement = getLastUidElement();
        lastUidElement.setText(String.valueOf(uid));
        save();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaFolder#save()
     */
    public void save() throws DelegateException {
        try {
            binding.save();
        }
        catch (IOException ioe) {
            throw new DelegateException("Error saving changes", ioe);
        }
    }
}
