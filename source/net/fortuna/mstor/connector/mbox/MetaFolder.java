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
package net.fortuna.mstor.connector.mbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.xml.DocumentBinding;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A JDOM-based implementation of a meta folder.
 * 
 * @author benfortuna
 */
public class MetaFolder extends AbstractMetaFolder {

    private static final String ELEMENT_FOLDER = "folder";

//    private static final String ATTRIBUTE_FOLDER_NAME = "name";

    private static final String ELEMENT_LAST_UID = "last-uid";

    private static final String ELEMENT_UID_VALIDITY = "uid-validity";

//    private Log log = LogFactory.getLog(MetaFolder.class);

    public static final String FILE_EXTENSION = ".emf";
    
    private DocumentBinding binding;
    
    /**
     * Constructs a new meta folder instance.
     * 
     * @param file the meta folder file
     */
    public MetaFolder(FolderDelegate delegate) {
    	super(delegate);
        binding = new DocumentBinding(getFile(), ELEMENT_FOLDER);
    }

    /**
     * Constructs a new meta folder instance with the specified namespace.
     * 
     * @param file the meta folder file
     * @param namespace the namespace for the metadata
     */
    public MetaFolder(final Namespace namespace, FolderDelegate delegate) {
    	super(delegate);
        binding = new DocumentBinding(getFile(), namespace, ELEMENT_FOLDER);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getParent()
     */
    public FolderDelegate getParent() {
        return new MetaFolder(getDelegate().getParent());
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate getFolder(String name) throws MessagingException {
        return new MetaFolder(getDelegate().getFolder(name));
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate[] list(String pattern) {
        List folders = new ArrayList();
        
        FolderDelegate[] delegateList = getDelegate().list(pattern);
        for (int i = 0; i < delegateList.length; i++) {
            folders.add(new MetaFolder(delegateList[i]));
        }
        
        return (FolderDelegate[]) folders.toArray(
                new FolderDelegate[folders.size()]);
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessage(int)
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
    protected final MessageDelegate createMessage(int messageNumber) {
        MessageDelegate delegate = new MetaMessage(messageNumber, this, binding.getNamespace());
        // only add the metadata if message is associated with folder..
        if (messageNumber > 0) {
            addMessage(delegate);
        }
        return delegate;
    }
    
    /**
     * @param message
     */
    private void addMessage(final MessageDelegate message) {
        binding.getDocument().getRootElement().addContent(
                ((MetaMessage) message).getElement());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.data.MetaFolder#removeMessage(java.lang.String)
     */
    /*
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
    */

    /**
     * @param messages
     * @return
     */
    protected MessageDelegate[] removeMessages(Message[] messages) {
        List metas = new ArrayList();

        for (Iterator i = binding.getDocument().getRootElement().getChildren(
                MetaMessage.ELEMENT_MESSAGE, binding.getNamespace()).iterator(); i
                .hasNext();) {

            Element messageElement = (Element) i.next();
            int messageNumber = Integer
                    .parseInt(messageElement
                            .getAttributeValue(MetaMessage.ATTRIBUTE_MESSAGE_NUMBER));

            for (int n = 0; n < messages.length; n++) {
                if (messages[n].getMessageNumber() == messageNumber) {
                    i.remove();
                    metas.add(new MetaMessage(messageElement, this,
                            binding.getNamespace()));
                    updateMessageNumbers(messageNumber, -1);
                    break;
                }
            }
        }
        return (MetaMessage[]) metas.toArray(new MetaMessage[metas.size()]);
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getLastUid()
     */
    public final long getLastUid() {
        Element lastUidElement = getLastUidElement();
        return Long.parseLong(lastUidElement.getText());
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getUidValidity()
     */
    public final long getUidValidity() throws UnsupportedOperationException,
        MessagingException {
        
        Element uidValidityElement = binding.getDocument().getRootElement().getChild(
                ELEMENT_UID_VALIDITY, binding.getNamespace());
        
        if (uidValidityElement == null) {
            uidValidityElement = new Element(ELEMENT_UID_VALIDITY, binding.getNamespace());
            uidValidityElement.setText(String.valueOf(newUidValidity()));
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.delegate.AbstractFolderDelegate#setLastUid(long)
     */
    protected final void setLastUid(long uid) throws DelegateException {
        Element lastUidElement = getLastUidElement();
        lastUidElement.setText(String.valueOf(uid));
        save();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#getFileExtension()
     */
    protected String getFileExtension() {
    	return FILE_EXTENSION;
    }
    
    /**
     * @throws DelegateException
     */
    public final void save() throws DelegateException {
        try {
            binding.save();
        }
        catch (IOException ioe) {
            throw new DelegateException("Error saving changes", ioe);
        }
    }
}
