/*
 * $Id$
 * 
 * Created: [7/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
package net.fortuna.mstor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Implementation of a message for the mstor javamail provider.
 * @author benfortuna
 */
public class MStorMessage extends MimeMessage implements Serializable {
    
    private static Log log = LogFactory.getLog(MStorMessage.class);

    /**
     * Additional metadata not support by MimeMessage.
     */
    private MetaMessage meta;

    private InputStream in;

    private boolean loaded;

    /**
     * @param arg0
     */
    public MStorMessage(Session session) {
        super(session);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(Session session, InputStream in)
            throws MessagingException {
        super(session);

        this.in = in;
    }

    /**
     * @param arg0
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(MimeMessage m) throws MessagingException {
        super(m);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MStorMessage(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(Folder folder, InputStream in, int msgnum)
            throws MessagingException {
        super(folder, msgnum);

        this.in = in;
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum)
            throws MessagingException {
        super(folder, headers, content, msgnum);
    }

    /**
     * @return Returns the meta.
     */
    private MetaMessage getMeta() {
        return meta;
    }

    /**
     * @param meta The meta to set.
     */
    public void setMeta(MetaMessage meta) {
        this.meta = meta;
        // update message from metadata..
        if (meta != null) {
            super.setExpunged(meta.isExpunged());
            try {
                super.setFlags(meta.getFlags(), true);
            }
            catch (MessagingException me) {
                log.warn("Error setting flags from metadata [" + meta.getMessageId() + "]", me);
            }
        }
    }

    /**
     * Checks if the input stream has been parsed, and if not the
     * parse is performed.
     * @throws MessagingException
     */
    private void checkParse() throws MessagingException {
        if (!loaded) {
            parse(in);
            loaded = true;
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimePart#getAllHeaderLines()
     */
    public Enumeration getAllHeaderLines() throws MessagingException {
        checkParse();
        return super.getAllHeaderLines();
    }
    /* (non-Javadoc)
     * @see javax.mail.Part#getAllHeaders()
     */
    public Enumeration getAllHeaders() throws MessagingException {
        checkParse();
        return super.getAllHeaders();
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimePart#getHeader(java.lang.String, java.lang.String)
     */
    public String getHeader(String arg0, String arg1) throws MessagingException {
        checkParse();
        return super.getHeader(arg0, arg1);
    }
    /* (non-Javadoc)
     * @see javax.mail.Part#getHeader(java.lang.String)
     */
    public String[] getHeader(String arg0) throws MessagingException {
        checkParse();
        return super.getHeader(arg0);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimePart#getMatchingHeaderLines(java.lang.String[])
     */
    public Enumeration getMatchingHeaderLines(String[] arg0)
            throws MessagingException {
        checkParse();
        return super.getMatchingHeaderLines(arg0);
    }
    /* (non-Javadoc)
     * @see javax.mail.Part#getMatchingHeaders(java.lang.String[])
     */
    public Enumeration getMatchingHeaders(String[] arg0)
            throws MessagingException {
        checkParse();
        return super.getMatchingHeaders(arg0);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimePart#getNonMatchingHeaderLines(java.lang.String[])
     */
    public Enumeration getNonMatchingHeaderLines(String[] arg0)
            throws MessagingException {
        checkParse();
        return super.getNonMatchingHeaderLines(arg0);
    }
    /* (non-Javadoc)
     * @see javax.mail.Part#getNonMatchingHeaders(java.lang.String[])
     */
    public Enumeration getNonMatchingHeaders(String[] arg0)
            throws MessagingException {
        checkParse();
        return super.getNonMatchingHeaders(arg0);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getContentStream()
     */
    protected InputStream getContentStream() throws MessagingException {
        checkParse();
        return super.getContentStream();
    }

    /* (non-Javadoc)
     * @see javax.mail.Message#setExpunged(boolean)
     */
    protected final void setExpunged(final boolean expunged) {
        if (getMeta() != null) {
            getMeta().setExpunged(expunged);
            try {
                getMeta().getFolder().save();
            }
            catch (IOException ioe) {
                log.warn("Error saving metadata [" + getMeta().getMessageId() + "]", ioe);
            }
        }
        super.setExpunged(expunged);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getReceivedDate()
     */
    public final Date getReceivedDate() throws MessagingException {
        if (getMeta() != null) {
            return getMeta().getReceived();
        }
        return super.getReceivedDate();
    }
    
    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#setFlags(javax.mail.Flags, boolean)
     */
    public final synchronized void setFlags(final Flags flags, final boolean set)
            throws MessagingException {
        super.setFlags(flags, set);
        
        // copy updated flags from mime message implementation..
        if (getMeta() != null) {
            getMeta().setFlags(getFlags());
            try {
                getMeta().getFolder().save();
            }
            catch (IOException ioe) {
                log.warn("Error saving metadata [" + getMeta().getMessageId() + "]", ioe);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.mail.Message#setFlag(javax.mail.Flags.Flag, boolean)
     */
    public void setFlag(Flag flag, boolean set) throws MessagingException {
        super.setFlag(flag, set);
        
        // copy updated flags from mime message implementation..
        if (getMeta() != null) {
            getMeta().setFlags(getFlags());
            try {
                getMeta().getFolder().save();
            }
            catch (IOException ioe) {
                log.warn("Error saving metadata [" + getMeta().getMessageId() + "]", ioe);
            }
        }
    }
}
