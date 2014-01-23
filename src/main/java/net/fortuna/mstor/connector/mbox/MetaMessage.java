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
package net.fortuna.mstor.connector.mbox;

import static net.fortuna.mstor.util.MessageUtils.getFlag;
import static net.fortuna.mstor.util.MessageUtils.getFlagName;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;

import net.fortuna.mstor.connector.AbstractMessageDelegate;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.MboxFile;
import net.fortuna.mstor.data.xml.ElementBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.IllegalNameException;
import org.jdom.Namespace;

/**
 * A JDOM-based implementation of a meta message.
 *
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: 18/08/2004
 * </pre>
 * 
 */
public class MetaMessage extends AbstractMessageDelegate {

    private static final long serialVersionUID = -3882112036857983804L;

    private static final String SPACE_SUBSTITUTE = "__SPACE__";

    protected static final String ELEMENT_MESSAGE = "message";

    protected static final String ATTRIBUTE_MESSAGE_NUMBER = "messageNumber";

    protected static final String ATTRIBUTE_UID = "uid";

    private static final String ELEMENT_FLAGS = "flags";

    private static final String ELEMENT_HEADERS = "headers";

    private static final String ELEMENT_EXPUNGED = "expunged";

    private static final String ELEMENT_RECEIVED = "received";

    private static final String ELEMENT_REPLIED = "replied";

    private static final String ELEMENT_FORWARDED = "forwarded";

    private static final String META_DATE_PATTERN = "EEE MMM d HH:mm:ss yyyy";

    private static final DateFormat MESSAGE_DATE_FORMAT = new SimpleDateFormat(
            META_DATE_PATTERN, Locale.US);

    private Log log = LogFactory.getLog(MetaMessage.class);

    private ElementBinding binding;
    
    private MetaFolder metaFolder;
    
    /**
     * Constructs a new meta message instance based on a new JDOM element with the specified message
     * id.
     *
     * @param messageId the message id of the new meta message
     */
    public MetaMessage(final int messageNumber, final MetaFolder folder,
            final Namespace namespace) {

        this(new Element(ELEMENT_MESSAGE, namespace).setAttribute(
                ATTRIBUTE_MESSAGE_NUMBER, String.valueOf(messageNumber)),
                folder, namespace);
        this.metaFolder = folder;
    }

    /**
     * Constructs a new meta message instance based on the specified JDOM element.
     *
     * @param element a JDOM element for the meta message
     */
    public MetaMessage(final Element element, final MetaFolder folder,
            final Namespace namespace) {

//        super(folder);
        binding = new ElementBinding(element, namespace);
        this.metaFolder = folder;
    }

    /**
     * Returns the underlying JDOM element.
     * 
     * @return a JDOM element
     */
    protected final Element getElement() {
        return binding.getElement();
    }

    /**
     * {@inheritDoc}
     */
    public final int getMessageNumber() {
        try {
            return Integer.parseInt(getElement()
                    .getAttributeValue(ATTRIBUTE_MESSAGE_NUMBER));
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Date getReceived() {
        String received = binding.getElement(ELEMENT_RECEIVED).getText();
        try {
            synchronized (MESSAGE_DATE_FORMAT) {
                return MESSAGE_DATE_FORMAT.parse(received);
            }
        }
        catch (Exception e) {
            log.warn("Invalid received date [" + received + "]");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final void setReceived(final Date date) {
        Element received = binding.getElement(ELEMENT_RECEIVED);
        synchronized (MESSAGE_DATE_FORMAT) {
            received.setText(MESSAGE_DATE_FORMAT.format(date));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Date getForwarded() {
        String forwarded = binding.getElement(ELEMENT_FORWARDED).getText();
        try {
            synchronized (MESSAGE_DATE_FORMAT) {
                return MESSAGE_DATE_FORMAT.parse(forwarded);
            }
        }
        catch (Exception e) {
            log.warn("Invalid forwarded date [" + forwarded + "]");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final void setForwarded(final Date date) {
        Element forwarded = binding.getElement(ELEMENT_FORWARDED);
        synchronized (MESSAGE_DATE_FORMAT) {
            forwarded.setText(MESSAGE_DATE_FORMAT.format(date));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Date getReplied() {
        String replied = binding.getElement(ELEMENT_REPLIED).getText();
        try {
            synchronized (MESSAGE_DATE_FORMAT) {
                return MESSAGE_DATE_FORMAT.parse(replied);
            }
        }
        catch (Exception e) {
            log.warn("Invalid replied date [" + replied + "]");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final void setReplied(final Date date) {
        Element replied = binding.getElement(ELEMENT_REPLIED);
        synchronized (MESSAGE_DATE_FORMAT) {
            replied.setText(MESSAGE_DATE_FORMAT.format(date));
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isExpunged() {
        Element expunged = binding.getElement(ELEMENT_EXPUNGED);
        try {
            return Boolean.valueOf(expunged.getText());
        }
        catch (Exception e) {
            log.warn("Invalid expunged value [" + expunged.getText() + "]");
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final void setExpunged(final boolean flag) {
        Element expunged = binding.getElement(ELEMENT_EXPUNGED);
        expunged.setText(String.valueOf(flag));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final Flags getFlags() {
        Flags flags = new Flags();
        for (Element flagElement : (Iterable<Element>) binding.getElement(ELEMENT_FLAGS).getChildren()) {

            Flag flag = getFlag(flagElement.getName());
            if (flag != null) {
                flags.add(flag);
            } else {
                // user flag..
                flags.add(flagElement.getName().replaceAll(SPACE_SUBSTITUTE, " "));
            }
        }
        return flags;
    }

    /**
     * {@inheritDoc}
     */
    public final void setFlags(final Flags flags) {
        Element flagsElement = binding.getElement(ELEMENT_FLAGS);
        flagsElement.removeContent();
        for (int i = 0; i < flags.getSystemFlags().length; i++) {
            String flagName = getFlagName(flags.getSystemFlags()[i]);
            if (flagName != null) {
                flagsElement.addContent(new Element(flagName));
            }
        }

        for (int i = 0; i < flags.getUserFlags().length; i++) {
            // XML node names cannot have spaces, so for now as
            // a workaround replace spaces with underscore..
            String flag = flags.getUserFlags()[i].replaceAll(" ",
                    SPACE_SUBSTITUTE);
            flagsElement.addContent(new Element(flag));
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final InternetHeaders getHeaders() {
        InternetHeaders headers = new InternetHeaders();

        for (Element header : (Iterable<Element>) binding.getElement(ELEMENT_HEADERS).getChildren()) {
            headers.addHeader(header.getName(), header.getText());
        }

        return headers;
    }

    /**
     * {@inheritDoc}
     */
    public final void setHeaders(final Enumeration<Header> headers) {
        Element headersElement = binding.getElement(ELEMENT_HEADERS);
        headersElement.removeContent();
        while (headers.hasMoreElements()) {
            Header header = headers.nextElement();
            try {
                if (!header.getName().startsWith(MboxFile.FROM__PREFIX)) {
                    headersElement.addContent(new Element(header.getName())
                            .setText(header.getValue()));
                }
            }
            catch (IllegalNameException ine) {
                log.warn("Invalid header (ignored): " + header.getName() + "="
                        + header.getValue());
                if (log.isDebugEnabled()) {
                    log.debug(ine);
                }
            }
            catch (IllegalDataException ide) {
                log.warn("Invalid header (ignored): " + header.getName() + "="
                        + header.getValue());
                if (log.isDebugEnabled()) {
                    log.debug(ide);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void saveChanges() throws DelegateException {
        metaFolder.save();
    }

    /**
     * {@inheritDoc}
     */
    public long getUid() {
        return Long.parseLong(getElement().getAttributeValue(ATTRIBUTE_UID));
    }

    /**
     * {@inheritDoc}
     */
    public void setUid(long uid) {
        getElement().setAttribute(ATTRIBUTE_UID, String.valueOf(uid));
    }
    
    /**
     * {@inheritDoc}
     */
    public MessageDelegate getInReplyTo() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Message references not supported");
    }
    
    /**
     * {@inheritDoc}
     */
    public List<? extends MessageDelegate> getReferences() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Message references not supported");
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream() {
        // TODO Auto-generated method stub
        return null;
    }
}
