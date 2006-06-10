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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;

import net.fortuna.mstor.MetaFolder;
import net.fortuna.mstor.MetaMessage;
import net.fortuna.mstor.data.xml.DocumentBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;


/**
 * A JDOM-based implementation of a meta folder.
 * @author benfortuna
 */
public class MetaFolderImpl extends DocumentBinding implements MetaFolder {
    
    private static final String ELEMENT_FOLDER = "folder";

    private static final String ATTRIBUTE_FOLDER_NAME = "name";

    private Log log = LogFactory.getLog(MetaFolderImpl.class);

    public static final String FILE_EXTENSION = ".emf";

    /**
     * Constructs a new meta folder instance.
     * @param file the meta folder file
     */
    public MetaFolderImpl(final File file) {
        super(file);
    }

    /**
     * Constructs a new meta folder instance with the specified namespace.
     * @param file the meta folder file
     * @param namespace the namespace for the metadata
     */
    public MetaFolderImpl(final File file, final Namespace namespace) {
        super(file, namespace);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.xml.DocumentBinding#getRootElementName()
     */
    protected final String getRootElementName() {
        return ELEMENT_FOLDER;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#getName()
     */
    public final String getName() {
        return getDocument().getRootElement().getAttributeValue(ATTRIBUTE_FOLDER_NAME);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#setName(java.lang.String)
     */
    public final void setName(final String name) {
        getDocument().getRootElement().setAttribute(ATTRIBUTE_FOLDER_NAME, name);
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#getMessage(javax.mail.Message)
     */
    public final MetaMessage getMessage(final Message message) {
        try {
//            String messageId = ((MimeMessage) message).getMessageID();
            int messageNumber = message.getMessageNumber();

            for (Iterator i = getDocument().getRootElement().getChildren(
                    MetaMessageImpl.ELEMENT_MESSAGE, namespace).iterator();
                    i.hasNext();) {
                
                Element messageElement = (Element) i.next();
                try {
                    if (Integer.parseInt(messageElement.getAttributeValue(MetaMessageImpl.ATTRIBUTE_MESSAGE_NUMBER)) == messageNumber) {
                        return new MetaMessageImpl(messageElement, this, namespace);
                    }
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Caught exception parsing message number", e);
                    }
                }
            }

            MetaMessageImpl mm = new MetaMessageImpl(messageNumber, this, namespace);
            mm.setFlags(message.getFlags());
            mm.setHeaders(message.getAllHeaders());
            // only add the metadata if message is associated with folder..
            if (messageNumber > 0) {
                getDocument().getRootElement().addContent(mm.getElement());
            }
            return mm;
        }
        catch (Exception e) {
            log.warn("Error creating metadata - no metadata available", e);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#addMessage(net.fortuna.mstor.data.MetaMessage)
     */
    public final void addMessage(final MetaMessage message) {
        getDocument().getRootElement().addContent(((MetaMessageImpl) message).getElement());
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#removeMessage(java.lang.String)
     */
    public final MetaMessage removeMessage(final int messageNumber) {
        for (Iterator i = getDocument().getRootElement().getChildren(
                MetaMessageImpl.ELEMENT_MESSAGE, namespace).iterator();
                i.hasNext();) {
            
            Element messageElement = (Element) i.next();
            if (Integer.parseInt(messageElement.getAttributeValue(MetaMessageImpl.ATTRIBUTE_MESSAGE_NUMBER)) == messageNumber) {
                getDocument().getRootElement().removeContent(messageElement);
                updateMessageNumbers(messageNumber, -1);
                return new MetaMessageImpl(messageElement, this, namespace);
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaFolder#removeMessages(int[])
     */
    public final MetaMessage[] removeMessages(final int[] messageNumbers) {
        List metas = new ArrayList();
        int delta = 0;
        int startIndex = Integer.MAX_VALUE;
        for (Iterator i = getDocument().getRootElement().getChildren(
                MetaMessageImpl.ELEMENT_MESSAGE, namespace).iterator(); i.hasNext();) {
            
            Element messageElement = (Element) i.next();
            int messageNumber = Integer.parseInt(
                    messageElement.getAttributeValue(MetaMessageImpl.ATTRIBUTE_MESSAGE_NUMBER));
            
            for (int n = 0; n < messageNumbers.length; n++) {
                if (messageNumbers[n] == messageNumber) {
                    getDocument().getRootElement().removeContent(messageElement);
                    metas.add(new MetaMessageImpl(messageElement, this, namespace));
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
     * Updates all message numbers according to the specified arguments.
     * Used when message metadata is removed from the list.
     * @param startIndex anything greater than (or equal to) the start index is affected
     * @param delta amount to adjust relevant message numbers by
     */
    private void updateMessageNumbers(final int startIndex, final int delta) {
        for (Iterator i = getDocument().getRootElement().getChildren(
                MetaMessageImpl.ELEMENT_MESSAGE, namespace).iterator();
                i.hasNext();) {
            Element messageElement = (Element) i.next();
            int messageNumber = Integer.parseInt(messageElement.getAttributeValue(MetaMessageImpl.ATTRIBUTE_MESSAGE_NUMBER));
            if (messageNumber >= startIndex) {
                messageElement.setAttribute(MetaMessageImpl.ATTRIBUTE_MESSAGE_NUMBER, String.valueOf(messageNumber + delta));
            }
        }
    }
}
