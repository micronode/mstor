/*
 * $Id$
 *
 * Created on 22/01/2009
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
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.MessageDelegate;

import org.jcrom.AbstractJcrEntity;
import org.jcrom.JcrDataProviderImpl;
import org.jcrom.JcrFile;
import org.jcrom.JcrDataProvider.TYPE;
import org.jcrom.annotations.JcrFileNode;
import org.jcrom.annotations.JcrProperty;

/**
 * @author Ben
 *
 */
public class JcrMessage extends AbstractJcrEntity implements MessageDelegate {

    /**
     * 
     */
    private static final long serialVersionUID = 3832397825180707796L;
    
    @JcrProperty private Integer messageNumber;

    @JcrProperty private Map<String, String> headers;

    @JcrProperty private List<String> flags;
    
    @JcrProperty private Date received;
    
    @JcrProperty private Date replied;
    
    @JcrProperty private Date forwarded;
    
    @JcrProperty private Long uid;
    
    @JcrProperty private Boolean expunged;
    
    @JcrFileNode private JcrFile file;
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getFlags()
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getForwarded()
     */
    public Date getForwarded() {
        return forwarded;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getHeaders()
     */
    public InternetHeaders getHeaders() {
        InternetHeaders retVal = new InternetHeaders();
        for (String name : headers.keySet()) {
            retVal.addHeader(name, headers.get(name));
        }
        return retVal;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getMessageNumber()
     */
    public int getMessageNumber() {
        return messageNumber;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getReceived()
     */
    public Date getReceived() {
        return received;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getReplied()
     */
    public Date getReplied() {
        return replied;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#getUid()
     */
    public long getUid() {
        return uid;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#isExpunged()
     */
    public boolean isExpunged() {
        return expunged;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#saveChanges()
     */
    public void saveChanges() throws DelegateException {
        // TODO Auto-generated method stub

    }

    /**
     * @param messageNumber
     */
    public void setMessageNumber(Integer messageNumber) {
        this.messageNumber = messageNumber;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setExpunged(boolean)
     */
    public void setExpunged(boolean expunged) {
        this.expunged = expunged;
    }

    /**
     * @param folder
     * @return
     */
    public InputStream getMessageAsStream() {
        return file.getDataProvider().getInputStream();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setFlags(javax.mail.Flags)
     */
    public void setFlags(Flags flags) {
        for (Flag flag : flags.getSystemFlags()) {
            this.flags.add(getFlagName(flag));
        }
        for (String flag : flags.getUserFlags()) {
            this.flags.add(flag);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setForwarded(java.util.Date)
     */
    public void setForwarded(Date forwarded) {
        this.forwarded = forwarded;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setHeaders(javax.mail.internet.InternetHeaders)
     */
    @SuppressWarnings("unchecked")
    public void setHeaders(InternetHeaders headers) {
        setHeaders(headers.getAllHeaders());
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setHeaders(java.util.Enumeration)
     */
    public void setHeaders(Enumeration<Header> headers) {
        while (headers.hasMoreElements()) {
            Header header = headers.nextElement();
            this.headers.put(header.getName(), header.getValue());
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setReceived(java.util.Date)
     */
    public void setReceived(Date received) {
        this.received = received;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setReplied(java.util.Date)
     */
    public void setReplied(Date replied) {
        this.replied = replied;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.MessageDelegate#setUid(long)
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
        
        file = new JcrFile();
        file.setName("data");
        file.setDataProvider(new JcrDataProviderImpl(TYPE.BYTES, mout.toByteArray()));
        file.setMimeType(message.getContentType());
        file.setLastModified(java.util.Calendar.getInstance());
        if (message instanceof MimeMessage) {
            setName(((MimeMessage) message).getMessageID());
        }
    }
}
