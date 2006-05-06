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
 * 
 * @author benfortuna
 */
public class MStorMessage extends MimeMessage implements Serializable {

    private static final long serialVersionUID = 6413532435324648022L;
    
    private static Log log = LogFactory.getLog(MStorMessage.class);

    /**
     * Additional metadata not support by MimeMessage.
     */
    private MetaMessage meta;

    private InputStream in;

    private boolean loaded;
    
    private Tags tags;

    /**
     * @param arg0
     */
    public MStorMessage(final Session session) {
        super(session);
        tags = new Tags(flags);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final Session session, final InputStream in)
            throws MessagingException {
        
        super(session);
        this.in = in;
        tags = new Tags(flags);
    }

    /**
     * @param arg0
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final MimeMessage m) throws MessagingException {
        super(m);
        tags = new Tags(flags);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public MStorMessage(final Folder folder, final int msgnum) {
        super(folder, msgnum);
        tags = new Tags(flags);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final Folder folder, final InputStream in, final int msgnum)
            throws MessagingException {
        
        super(folder, msgnum);
        this.in = in;
        tags = new Tags(flags);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final Folder folder, final InternetHeaders headers, final byte[] content,
            final int msgnum) throws MessagingException {
        
        super(folder, headers, content, msgnum);
        tags = new Tags(flags);
    }

    /**
     * @param meta
     *            The meta to set.
     */
    public final void setMeta(final MetaMessage meta) {
        this.meta = meta;
        // update message from metadata..
        if (meta != null) {
            super.setExpunged(meta.isExpunged());
            try {
                super.setFlags(meta.getFlags(), true);
            }
            catch (MessagingException me) {
                log.warn("Error setting flags from metadata ["
                        + meta.getMessageNumber() + "]", me);
            }
        }
    }

    /**
     * Checks if the input stream has been parsed, and if not the parse is
     * performed.
     * 
     * @throws MessagingException
     */
    private void checkParse() throws MessagingException {
        if (!loaded) {
            parse(in);
            loaded = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimePart#getAllHeaderLines()
     */
    public final Enumeration getAllHeaderLines() throws MessagingException {
        if (meta != null) { return meta.getHeaders()
                .getAllHeaderLines(); }
        checkParse();
        return super.getAllHeaderLines();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Part#getAllHeaders()
     */
    public final Enumeration getAllHeaders() throws MessagingException {
        if (meta != null) { return meta.getHeaders().getAllHeaders(); }
        checkParse();
        return super.getAllHeaders();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimePart#getHeader(java.lang.String,
     *      java.lang.String)
     */
    public final String getHeader(final String arg0, final String arg1) throws MessagingException {
        if (meta != null) { return meta.getHeaders().getHeader(arg0,
                arg1); }
        checkParse();
        return super.getHeader(arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Part#getHeader(java.lang.String)
     */
    public final String[] getHeader(final String arg0) throws MessagingException {
        if (meta != null) { return meta.getHeaders().getHeader(arg0); }
        checkParse();
        return super.getHeader(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimePart#getMatchingHeaderLines(java.lang.String[])
     */
    public final Enumeration getMatchingHeaderLines(final String[] arg0)
            throws MessagingException {
        if (meta != null) { return meta.getHeaders()
                .getMatchingHeaderLines(arg0); }
        checkParse();
        return super.getMatchingHeaderLines(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Part#getMatchingHeaders(java.lang.String[])
     */
    public final Enumeration getMatchingHeaders(final String[] arg0)
            throws MessagingException {
        if (meta != null) { return meta.getHeaders()
                .getMatchingHeaders(arg0); }
        checkParse();
        return super.getMatchingHeaders(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimePart#getNonMatchingHeaderLines(java.lang.String[])
     */
    public final Enumeration getNonMatchingHeaderLines(final String[] arg0)
            throws MessagingException {
        if (meta != null) { return meta.getHeaders()
                .getNonMatchingHeaderLines(arg0); }
        checkParse();
        return super.getNonMatchingHeaderLines(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Part#getNonMatchingHeaders(java.lang.String[])
     */
    public final Enumeration getNonMatchingHeaders(final String[] arg0)
            throws MessagingException {
        if (meta != null) { return meta.getHeaders()
                .getNonMatchingHeaders(arg0); }
        checkParse();
        return super.getNonMatchingHeaders(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimeMessage#getContentStream()
     */
    protected final InputStream getContentStream() throws MessagingException {
        checkParse();
        return super.getContentStream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Message#setExpunged(boolean)
     */
    protected final void setExpunged(final boolean expunged) {
        if (meta != null) {
            meta.setExpunged(expunged);
            /*
             * try { getMeta().getFolder().save(); } catch (IOException ioe) {
             * log.warn("Error saving metadata [" + getMeta().getMessageId() +
             * "]", ioe); }
             */
        }
        super.setExpunged(expunged);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimeMessage#getReceivedDate()
     */
    public final Date getReceivedDate() throws MessagingException {
        if (meta != null) { return meta.getReceived(); }
        return super.getReceivedDate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.internet.MimeMessage#setFlags(javax.mail.Flags, boolean)
     */
    public final synchronized void setFlags(final Flags flags, final boolean set)
            throws MessagingException {
        super.setFlags(flags, set);
        // copy updated flags from mime message implementation..
        if (meta != null) {
            meta.setFlags(flags);
            /*
             * try { getMeta().getFolder().save(); } catch (IOException ioe) {
             * log.warn("Error saving metadata [" + getMeta().getMessageId() +
             * "]", ioe); }
             */
            // we must call explicility even if superclass also calls as
            // superclass will make
            // call before we have updated metadata..
            saveChanges();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Message#setFlag(javax.mail.Flags.Flag, boolean)
     */
    public final void setFlag(final Flag flag, final boolean set) throws MessagingException {
        super.setFlag(flag, set);
        // copy updated flags from mime message implementation..
        if (meta != null) {
            meta.setFlags(flags);
            /*
             * try { getMeta().getFolder().save(); } catch (IOException ioe) {
             * log.warn("Error saving metadata [" + getMeta().getMessageId() +
             * "]", ioe); }
             */
            // we must call explicility even if superclass also calls as
            // superclass will make
            // call before we have updated metadata..
            saveChanges();
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.Part#setHeader(java.lang.String, java.lang.String)
     */
    public final void setHeader(final String s, final String s1) throws MessagingException {
        // looks like we need to load the message before setting headers..
        checkParse();
        super.setHeader(s, s1);
        if (meta != null) {
            // update metadata..
            meta.setHeaders(headers);
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.Part#addHeader(java.lang.String, java.lang.String)
     */
    public final void addHeader(final String s, final String s1) throws MessagingException {
        super.addHeader(s, s1);
        if (meta != null) {
            // update metadata..
            meta.setHeaders(headers);
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.Part#removeHeader(java.lang.String)
     */
    public final void removeHeader(final String s) throws MessagingException {
        super.removeHeader(s);
        if (meta != null) {
            // update metadata..
            meta.setHeaders(headers);
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimePart#addHeaderLine(java.lang.String)
     */
    public final void addHeaderLine(final String s) throws MessagingException {
        super.addHeaderLine(s);
        if (meta != null) {
            // update metadata..
            meta.setHeaders(headers);
        }
    }
    
    /**
     * @param tag
     */
    public final void addTag(final String tag) throws MessagingException {
//        Flags flags = new Flags();
//        Tags tags = getTags();
        tags.add(tag);
//        setFlags(flags, true);
        if (meta != null) {
            meta.setFlags(getFlags());
            saveChanges();
        }
    }
    
    /**
     * @param tag
     * @throws MessagingException
     */
    public final void removeTag(final String tag) throws MessagingException {
//        Flags flags = new Flags();
//        Tags tags = getTags();
        tags.remove(tag);
//        tags.add(tag);
//        setFlags(flags, false);
        if (meta != null) {
            meta.setFlags(getFlags());
            saveChanges();
        }
    }

    /**
     * Attempts to save metadata after calling <code>saveChanges</code> in the
     * superclass.
     */
    public final void saveChanges() throws MessagingException {
        super.saveChanges();
        if (meta != null) {
            try {
                meta.getFolder().save();
            }
            catch (IOException ioe) {
                log.warn("Error saving metadata [" + meta.getMessageNumber()
                        + "]", ioe);
            }
        }
    }

    /**
     * Attempts to update headers in metadata after updating headers in
     * superclass.
     */
    protected final void updateHeaders() throws MessagingException {
        super.updateHeaders();
        if (meta != null) {
            meta.setHeaders(headers);
        }
    }

    /**
     * Returns tags associated with this message. Note that any changes made
     * to the returned instance will not affect this message.
     * @return Returns the tags.
     */
    public final Tags getTags() throws MessagingException {
        return new Tags(getFlags());
    }
}
