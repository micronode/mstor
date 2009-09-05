/*
 *
 * Copyright (c) 2009, Ben Fortuna
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

import static net.fortuna.mstor.util.MessageUtils.getFlag;
import static net.fortuna.mstor.util.MessageUtils.getFlagName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import net.fortuna.mstor.MStorMessage;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.util.MessageUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jcrom.AbstractJcrEntity;
import org.jcrom.JcrDataProviderImpl;
import org.jcrom.JcrFile;
import org.jcrom.JcrDataProvider.TYPE;
import org.jcrom.annotations.JcrChildNode;
import org.jcrom.annotations.JcrFileNode;
import org.jcrom.annotations.JcrProperty;
import org.jcrom.annotations.JcrReference;

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
public class JcrMessage extends AbstractJcrEntity implements MessageDelegate {

    /**
     * 
     */
    private static final long serialVersionUID = 3832397825180707796L;
    
    @JcrProperty private Integer messageNumber;
    
    @JcrProperty private String messageId;

    @JcrProperty private Map<String, String> headers;

    @JcrProperty private List<String> flags;
    
    @JcrProperty private Date received;
    
    @JcrProperty private Date replied;
    
    @JcrProperty private Date forwarded;
    
    @JcrProperty private Long uid;
    
    @JcrProperty private Boolean expunged;
    
    @JcrFileNode(lazy=true) private JcrFile content;

    @JcrChildNode(lazy=true) private List<JcrMessage> messages;
    
    @JcrFileNode(lazy=true) private JcrFile body;
    
    @JcrFileNode(lazy=true) private List<JcrFile> attachments;
    
    @JcrReference(byPath=true, lazy=true) private JcrMessage inReplyTo;
    
    @JcrReference(byPath=true, lazy=true) private List<JcrMessage> references;
    
    private JcrMessageDao messageDao;
    
    /**
     * 
     */
    public JcrMessage() {
        headers = new HashMap<String, String>();
        flags = new ArrayList<String>();
        messages = new ArrayList<JcrMessage>();
        attachments = new ArrayList<JcrFile>();
        references = new ArrayList<JcrMessage>();
    }
    
    /**
     * {@inheritDoc}
     */
    public Flags getFlags() {
        Flags retVal = new Flags();
        for (String flag : flags) {
            Flag systemFlag = getFlag(flag);
            if (systemFlag != null) {
                retVal.add(systemFlag);
            }
            else {
                retVal.add(flag);
            }
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Date getForwarded() {
        return forwarded;
    }

    /**
     * {@inheritDoc}
     */
    public InternetHeaders getHeaders() {
        InternetHeaders retVal = new InternetHeaders();
        for (String name : headers.keySet()) {
            retVal.addHeader(name, headers.get(name));
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public int getMessageNumber() {
        return messageNumber;
    }

    /**
     * {@inheritDoc}
     */
    public Date getReceived() {
        return received;
    }

    /**
     * {@inheritDoc}
     */
    public Date getReplied() {
        return replied;
    }

    /**
     * {@inheritDoc}
     */
    public long getUid() {
        return uid;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpunged() {
        return expunged;
    }

    /**
     * {@inheritDoc}
     */
    public void saveChanges() throws DelegateException {
        if (messageDao != null) {
            // XXX: currently only flags needs updating so don't update children..
            messageDao.update(this, "none", -1);
        }
    }

    /**
     * @param messageNumber
     */
    public void setMessageNumber(Integer messageNumber) {
        this.messageNumber = messageNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setExpunged(boolean expunged) {
        this.expunged = expunged;
    }

    /**
     * @param folder
     * @return
     */
    public InputStream getInputStream() {
        return content.getDataProvider().getInputStream();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFlags(Flags flags) {
        this.flags.clear();
        for (Flag flag : flags.getSystemFlags()) {
            this.flags.add(getFlagName(flag));
        }
        for (String flag : flags.getUserFlags()) {
            this.flags.add(flag);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setForwarded(Date forwarded) {
        this.forwarded = forwarded;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void setHeaders(InternetHeaders headers) {
        setHeaders(headers.getAllHeaders());
    }

    /**
     * {@inheritDoc}
     */
    public void setHeaders(Enumeration<Header> headers) {
        this.headers.clear();
        // clear references..
        inReplyTo = null;
        getReferences().clear();
        
        while (headers.hasMoreElements()) {
            Header header = headers.nextElement();
            this.headers.put(header.getName(), header.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setReceived(Date received) {
        this.received = received;
    }

    /**
     * {@inheritDoc}
     */
    public void setReplied(Date replied) {
        this.replied = replied;
    }

    /**
     * {@inheritDoc}
     */
    public void setUid(long uid) {
        this.uid = uid;
    }

    /**
     * @param message
     * @throws MessagingException
     * @throws IOException
     */
    public void setMessage(Message message) throws MessagingException, IOException {
        ByteArrayOutputStream mout = new ByteArrayOutputStream();
        message.writeTo(mout);
        
        content = new JcrFile();
        content.setName("data");
        content.setDataProvider(new JcrDataProviderImpl(TYPE.BYTES, mout.toByteArray()));
        content.setMimeType(message.getContentType());
        content.setLastModified(java.util.Calendar.getInstance());

        messageId = MessageUtils.getMessageId(message);
        if (messageId != null) {
            setName(messageId);
        }
        else {
            setName("message");
        }
        
        if (message instanceof MimeMessage) {
            MimeMessage mimeMessage = (MimeMessage) message;
            messages.clear();
            attachments.clear();
            appendBody(mimeMessage);
            appendAttachments(mimeMessage);
        }
    }
    
    /**
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    private void appendBody(final Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/html")) {
            body = createBody(part);
        }
        else if (part.isMimeType("text/plain") && body == null) {
            body = createBody(part);
        }
        else if (part.isMimeType("multipart/*")) {
            Multipart multi = (Multipart) part.getContent();
            for (int i = 0; i < multi.getCount(); i++) {
                appendBody(multi.getBodyPart(i));
            }
        }
    }
    
    /**
     * @param part
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    JcrFile createBody(Part part) throws IOException, MessagingException {
        JcrFile body = new JcrFile();
        body.setName("part");
//        body.setDataProvider(new JcrDataProviderImpl(TYPE.BYTES, ((String) primaryBodyPart.getContent()).getBytes()));
        ByteArrayOutputStream pout = new ByteArrayOutputStream();
        IOUtils.copy(part.getInputStream(), pout);
        body.setDataProvider(new JcrDataProviderImpl(TYPE.BYTES, pout.toByteArray()));
        body.setMimeType(part.getContentType());
        body.setLastModified(java.util.Calendar.getInstance());
        return body;
    }
    
    /**
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void appendAttachments(final Part part) throws MessagingException, IOException {
        if (part.isMimeType("message/*")) {
            JcrMessage jcrMessage = new JcrMessage();
            
            Message attachedMessage = null;
            if (part.getContent() instanceof Message) {
                attachedMessage = (Message) part.getContent();
            }
            else {
                attachedMessage = new MStorMessage(null, (InputStream) part.getContent());
            }
            jcrMessage.setFlags(attachedMessage.getFlags());
            jcrMessage.setHeaders(attachedMessage.getAllHeaders());
            jcrMessage.setReceived(attachedMessage.getReceivedDate());
            jcrMessage.setExpunged(attachedMessage.isExpunged());
            jcrMessage.setMessage(attachedMessage);
            
            messages.add(jcrMessage);
        }
        else if (part.isMimeType("multipart/*")) {
            Multipart multi = (Multipart) part.getContent();
            for (int i = 0; i < multi.getCount(); i++) {
                appendAttachments(multi.getBodyPart(i));
            }
        }
        else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
                || StringUtils.isNotEmpty(part.getFileName())) {
            JcrFile attachment = new JcrFile();
            
            String name = null;
            if (StringUtils.isNotEmpty(part.getFileName())) {
                name = part.getFileName();
                for (JcrFile attach : attachments) {
                    if (attach.getName().equals(name)) {
                        // file already exists assume it's the same and we don't need
                        // to save it again..
                        return;
                    }
                }
            }
            else {
                String[] contentId = part.getHeader("Content-Id");
                if (contentId != null && contentId.length > 0) {
                    name = contentId[0];
                }
                else {
                    name = "attachment";
                }
            }
            
            int count = 0;
            for (JcrFile attach : attachments) {
                if (attach.getName().equals(name)) {
                    count++;
                }
            }
            if (count > 0) {
                name += "_" + count;
            }

            attachment.setName(name);
            
            ByteArrayOutputStream pout = new ByteArrayOutputStream();
//            part.writeTo(pout);
            IOUtils.copy(part.getInputStream(), pout);
            attachment.setDataProvider(new JcrDataProviderImpl(TYPE.BYTES, pout.toByteArray()));
            attachment.setMimeType(part.getContentType());
            attachment.setLastModified(java.util.Calendar.getInstance());
            attachments.add(attachment);
        }
    }

    /**
     * @return the inReplyTo
     */
    public final JcrMessage getInReplyTo() {
        return inReplyTo;
    }

    /**
     * @param inReplyTo the inReplyTo to set
     */
    public final void setInReplyTo(JcrMessage inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    /**
     * @return the references
     */
    public final List<JcrMessage> getReferences() {
        return references;
    }

    /**
     * @param messageDao the messageDao to set
     */
    final void setMessageDao(JcrMessageDao messageDao) {
        this.messageDao = messageDao;
    }
}
