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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import net.fortuna.mstor.MetaFolder;
import net.fortuna.mstor.MetaMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * A JDOM-based implementation of a meta folder.
 * @author benfortuna
 */
public class MetaFolderImpl implements MetaFolder {

    private static final String ELEMENT_FOLDER = "folder";

    private static final String ATTRIBUTE_FOLDER_NAME = "name";

    private static final String ELEMENT_MESSAGE = "message";

    private static final String ATTRIBUTE_MESSAGE_ID = "id";

    private static Log log = LogFactory.getLog(MetaFolderImpl.class);

    private File file;
    
    private Document document;

    public static final String FILE_EXTENSION = ".emf";
    
    /**
     * Constructs a new meta folder instance.
     * @param file the meta folder file
     */
    public MetaFolderImpl(final File file) {
        this.file = file;
    }
    
    /**
     * Returns the JDOM document associated with this
     * meta folder.
     * @return a JDOM document
     * @throws JDOMException thrown if the specified file
     * is not a valid XML document
     * @throws IOException thrown if an error occurs reading
     * the specified file
     */
    private Document getDocument() {
        if (document == null) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(file);
            }
            catch (Exception e) {
                // create an empty document if unable to read
                // from filesystem..
                document = new Document(new Element(ELEMENT_FOLDER));
            }
        }
        return document;
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
            String messageId = ((MimeMessage) message).getMessageID();
            
            for (Iterator i = getDocument().getRootElement().getChildren(ELEMENT_MESSAGE).iterator(); i.hasNext();) {
                Element messageElement = (Element) i.next();
                if (messageId.equals(messageElement.getAttributeValue(ATTRIBUTE_MESSAGE_ID))) {
                    return new MetaMessageImpl(messageElement, this);
                }
            }

            MetaMessageImpl mm = new MetaMessageImpl(messageId, this);
            getDocument().getRootElement().addContent(mm.getElement());
            return mm;
        }
        catch (Exception e) {
            log.warn("Message not MIME message - no metadata available", e);
        }

        return null;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#addMessage(net.fortuna.mstor.data.MetaMessage)
     */
    public final void addMessage(final MetaMessage message) {
        getDocument().getRootElement().addContent(((MetaMessageImpl)message).getElement());
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#removeMessage(java.lang.String)
     */
    public final MetaMessage removeMessage(final String messageId) {
        for (Iterator i = getDocument().getRootElement().getChildren(ELEMENT_MESSAGE).iterator(); i.hasNext();) {
            Element messageElement = (Element) i.next();
            if (messageId.equals(messageElement.getAttributeValue(ATTRIBUTE_MESSAGE_ID))) {
                getDocument().getRootElement().removeContent(messageElement);
                return new MetaMessageImpl(messageElement, this);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.data.MetaFolder#save()
     */
    public void save() throws IOException {
        XMLOutputter xmlOut = new XMLOutputter(Format.getCompactFormat());
        xmlOut.getFormat().setIndent("  ");
        xmlOut.output(document, new FileOutputStream(file));
    }
}
