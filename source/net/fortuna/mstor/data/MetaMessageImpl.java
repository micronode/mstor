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

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.internet.InternetHeaders;

import net.fortuna.mstor.MetaFolder;
import net.fortuna.mstor.MetaMessage;
import net.fortuna.mstor.util.MetaDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;


/**
 * A JDOM-based implementation of a meta message.
 * @author benfortuna
 */
public class MetaMessageImpl implements MetaMessage {

    private static final String ELEMENT_MESSAGE = "message";

    private static final String ATTRIBUTE_MESSAGE_ID = "id";

    private static final String ELEMENT_FLAGS = "flags";

    private static final String ELEMENT_HEADERS = "headers";

    private static final String ELEMENT_EXPUNGED = "expunged";

    private static final String ELEMENT_RECEIVED = "received";

    private static final String ELEMENT_REPLIED = "replied";

    private static final String ELEMENT_FORWARDED = "forwarded";

    private static Log log = LogFactory.getLog(MetaMessageImpl.class);

    private Element element;

    private MetaFolder folder;

    /**
     * Constructs a new meta message instance based on
     * a new JDOM element with the specified message id.
     * @param messageId the message id of the new meta message
     */
    public MetaMessageImpl(final String messageId, final MetaFolder folder) {
        this(new Element(ELEMENT_MESSAGE).setAttribute(ATTRIBUTE_MESSAGE_ID, messageId), folder);
    }

    /**
     * Constructs a new meta message instance based on the
     * specified JDOM element.
     * @param element a JDOM element for the meta message
     */
    public MetaMessageImpl(final Element element, final MetaFolder folder) {
        this.element = element;
        this.folder = folder;
    }

    /**
     * Returns the underlying JDOM element.
     * @return a JDOM element
     */
    protected Element getElement() {
        return element;
    }

    /**
     * Returns the specified child element of the
     * element associated with this meta message. If
     * the child does not exist it is created and added
     * to the message element.
     * @param name name of the child element
     * @return a JDOM element
     */
    private Element getElement(final String name) {
        Element child = element.getChild(name);
        if (child == null) {
            child = new Element(name);
            element.addContent(child);
        }
        return child;
    }

    public final String getMessageId() {
        return element.getAttributeValue(ATTRIBUTE_MESSAGE_ID);
    }

    public final Date getReceived() {
        String received = getElement(ELEMENT_RECEIVED).getText();
        try {
            return MetaDateFormat.getInstance().parse(received);
        }
        catch (Exception e) {
            log.info("Invalid received date [" + received + "]", e);
        }
        return null;
    }

    public final void setReceived(final Date date) {
        Element received = getElement(ELEMENT_RECEIVED);
        received.setText(MetaDateFormat.getInstance().format(date));
    }

    public final Date getForwarded() {
        String forwarded = getElement(ELEMENT_FORWARDED).getText();
        try {
            return MetaDateFormat.getInstance().parse(forwarded);
        }
        catch (Exception e) {
            log.info("Invalid forwarded date [" + forwarded + "]", e);
        }
        return null;
    }

    public final void setForwarded(final Date date) {
        Element forwarded = getElement(ELEMENT_FORWARDED);
        forwarded.setText(MetaDateFormat.getInstance().format(date));
    }

    public final Date getReplied() {
        String replied = getElement(ELEMENT_REPLIED).getText();
        try {
            return MetaDateFormat.getInstance().parse(replied);
        }
        catch (Exception e) {
            log.info("Invalid replied date [" + replied + "]", e);
        }
        return null;
    }

    public final void setReplied(final Date date) {
        Element replied = getElement(ELEMENT_REPLIED);
        replied.setText(MetaDateFormat.getInstance().format(date));
    }

    public final boolean isExpunged() {
        Element expunged = getElement(ELEMENT_EXPUNGED);
        return Boolean.valueOf(expunged.getText()).booleanValue();
    }

    public final void setExpunged(final boolean flag) {
        Element expunged = getElement(ELEMENT_EXPUNGED);
        expunged.setText(String.valueOf(flag));
    }

    public final Flags getFlags() {
        Flags flags = new Flags();
        for (Iterator i = getElement(ELEMENT_FLAGS).getChildren().iterator(); i.hasNext();) {
            Element flag = (Element) i.next();
            if ("answered".equals(flag.getName())) {
                flags.add(Flags.Flag.ANSWERED);
            }
            else if ("deleted".equals(flag.getName())) {
                flags.add(Flags.Flag.DELETED);
            }
            else if ("draft".equals(flag.getName())) {
                flags.add(Flags.Flag.DRAFT);
            }
            else if ("flagged".equals(flag.getName())) {
                flags.add(Flags.Flag.FLAGGED);
            }
            else if ("recent".equals(flag.getName())) {
                flags.add(Flags.Flag.RECENT);
            }
            else if ("seen".equals(flag.getName())) {
                flags.add(Flags.Flag.SEEN);
            }
            else if ("user".equals(flag.getName())) {
                flags.add(Flags.Flag.USER);
            }
            else {
                // user flag..
                flags.add(flag.getName());
            }
        }
        return flags;
    }

    public void setFlags(final Flags flags) {
        Element flagsElement = getElement(ELEMENT_FLAGS);
        flagsElement.removeContent();
        for (int i = 0; i < flags.getSystemFlags().length; i++) {
            Element flag = null;
            if (Flags.Flag.ANSWERED.equals(flags.getSystemFlags()[i])) {
                flag = new Element("answered");
            }
            else if (Flags.Flag.DELETED.equals(flags.getSystemFlags()[i])) {
                flag = new Element("deleted");
            }
            else if (Flags.Flag.DRAFT.equals(flags.getSystemFlags()[i])) {
                flag = new Element("draft");
            }
            else if (Flags.Flag.FLAGGED.equals(flags.getSystemFlags()[i])) {
                flag = new Element("flagged");
            }
            else if (Flags.Flag.RECENT.equals(flags.getSystemFlags()[i])) {
                flag = new Element("recent");
            }
            else if (Flags.Flag.SEEN.equals(flags.getSystemFlags()[i])) {
                flag = new Element("seen");
            }
            else if (Flags.Flag.USER.equals(flags.getSystemFlags()[i])) {
                flag = new Element("user");
            }
            flagsElement.addContent(flag);
        }

        for (int i = 0; i < flags.getUserFlags().length; i++) {
            // XML node names cannot have spaces, so for now as
            // a workaround replace spaces with underscore..
            String flag = flags.getUserFlags()[i].replaceAll(" ", "_");
            flagsElement.addContent(new Element(flag));
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getHeaders()
     */
    public final InternetHeaders getHeaders() {
		InternetHeaders headers = new InternetHeaders();

        for (Iterator i = getElement(ELEMENT_HEADERS).getChildren().iterator(); i.hasNext();) {
            Element header = (Element) i.next();
            headers.addHeader(header.getName(), header.getText());
		}

		return headers;
	}

	/* (non-Javadoc)
	 * @see net.fortuna.mstor.MetaMessage#setHeaders(javax.mail.internet.InternetHeaders)
	 */
	public final void setHeaders(final InternetHeaders headers) {
	    setHeaders(headers.getAllHeaders());
	}

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setHeaders(java.util.Enumeration)
     */
    public final void setHeaders(final Enumeration headers) {
        Element headersElement = getElement(ELEMENT_HEADERS);
        headersElement.removeContent();
        for (;headers.hasMoreElements();) {
            Header header = (Header) headers.nextElement();
            if (!header.getName().startsWith(MboxFile.FROM__PREFIX)) {
                headersElement.addContent(new Element(header.getName()).setText(header.getValue()));
            }
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getFolder()
     */
    public MetaFolder getFolder() {
        return folder;
    }
}
