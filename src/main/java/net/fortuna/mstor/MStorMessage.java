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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.tag.Taggable;
import net.fortuna.mstor.tag.Tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a message for the mstor javamail provider.
 *
 * @author Ben Fortuna
 */
public final class MStorMessage extends MimeMessage implements Serializable, Taggable {

    private static final long serialVersionUID = 6413532435324648022L;

    private Log log = LogFactory.getLog(MStorMessage.class);

    /**
     * Delegate for providing additional functions not supported by MimeMessage.
     */
    private MessageDelegate delegate;

    private InputStream in;

    private boolean loaded;

    private Tags tags;

    /**
     * @param arg0
     */
    public MStorMessage(final Session session) {
        this(session, null);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final Session session, final InputStream in) {
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
        this(folder, null, msgnum);
    }

    /**
     * @param folder
     * @param in
     * @param msgnum
     */
    public MStorMessage(final Folder folder, final InputStream in,
            final int msgnum) {
        
        this(folder, in, msgnum, null);
    }
    
    /**
     * @param folder
     * @param in
     * @param msgnum
     * @param delegate
     */
    public MStorMessage(final Folder folder, final InputStream in,
            final int msgnum, MessageDelegate delegate) {
        
        super(folder, msgnum);
        this.in = in;
        this.delegate = delegate;
        tags = new Tags(flags);
        
        // sync with delegate..
        if (delegate != null) {
            try {
                super.setExpunged(delegate.isExpunged());
                super.setFlags(delegate.getFlags(), true);
            }
            catch (Exception e) {
                log.warn("Error syncing with delegate", e);
            }
        }
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @throws javax.mail.MessagingException
     */
    public MStorMessage(final Folder folder, final InternetHeaders headers,
            final byte[] content, final int msgnum) throws MessagingException {

        super(folder, headers, content, msgnum);
        tags = new Tags(flags);
    }

    /**
     * Checks if the input stream has been parsed, and if not the parse is performed.
     *
     * @throws MessagingException
     */
    private void checkParse() throws MessagingException {
        if (!loaded) {
            parse(in);
            loaded = true;
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getAllHeaderLines()
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getAllHeaderLines() throws MessagingException {
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<String>) headers.getAllHeaderLines();
        }
        checkParse();
        return (Enumeration<String>) super.getAllHeaderLines();
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getAllHeaders()
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getAllHeaders() throws MessagingException {
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<Header>) headers.getAllHeaders();
        }
        checkParse();
        return (Enumeration<Header>) super.getAllHeaders();
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getHeader(java.lang.String, java.lang.String)
     */
    public String getHeader(final String name, final String delimiter)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getHeader(name, delimiter);
        }
        checkParse();
        return super.getHeader(name, delimiter);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getHeader(java.lang.String)
     */
    public String[] getHeader(final String name)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getHeader(name);
        }
        checkParse();
        return super.getHeader(name);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getMatchingHeaderLines(java.lang.String[])
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getMatchingHeaderLines(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<String>) headers.getMatchingHeaderLines(names);
        }
        checkParse();
        return (Enumeration<String>) super.getMatchingHeaderLines(names);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getMatchingHeaders(java.lang.String[])
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getMatchingHeaders(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<Header>) headers.getMatchingHeaders(names);
        }
        checkParse();
        return (Enumeration<Header>) super.getMatchingHeaders(names);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getNonMatchingHeaderLines(java.lang.String[])
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getNonMatchingHeaderLines(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<String>) headers.getNonMatchingHeaderLines(names);
        }
        checkParse();
        return (Enumeration<String>) super.getNonMatchingHeaderLines(names);
    }

    /* (non-Javadoc)
     * @see javax.mail.internet.MimeMessage#getNonMatchingHeaders(java.lang.String[])
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getNonMatchingHeaders(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return (Enumeration<Header>) headers.getNonMatchingHeaders(names);
        }
        checkParse();
        return (Enumeration<Header>) super.getNonMatchingHeaders(names);
    }

    /**
     * @return
     * @throws MessagingException
     */
    private InternetHeaders getHeaders() throws MessagingException {
        if (delegate != null) {
            try {
                return delegate.getHeaders();
            }
            catch (Exception e) {
                throw new MessagingException("Error retrieving headers", e);
            }
        }
        return null;
    }
    
    /*
     * (non-Javadoc)
     *
     * @see javax.mail.internet.MimeMessage#getContentStream()
     */
    protected InputStream getContentStream() throws MessagingException {
        checkParse();
        return super.getContentStream();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Message#setExpunged(boolean)
     */
    protected void setExpunged(final boolean expunged) {
        if (delegate != null) {
            try {
                delegate.setExpunged(expunged);
            }
            catch (Exception e) {
                log.error("Error updating expunged flag", e);
            }
        }
        super.setExpunged(expunged);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.internet.MimeMessage#getReceivedDate()
     */
    public Date getReceivedDate() throws MessagingException {
        if (delegate != null) {
            try {
                return delegate.getReceived();
            }
            catch (Exception e) {
                throw new MessagingException("Error retrieving received date", e);
            }
        }
        return super.getReceivedDate();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.internet.MimeMessage#setFlags(javax.mail.Flags, boolean)
     */
    public synchronized void setFlags(final Flags flags, final boolean set)
            throws MessagingException {
        super.setFlags(flags, set);
        // copy updated flags from mime message implementation..
        updateFlags();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Message#setFlag(javax.mail.Flags.Flag, boolean)
     */
    public void setFlag(final Flag flag, final boolean set)
            throws MessagingException {
        
        super.setFlag(flag, set);
        // copy updated flags from mime message implementation..
        updateFlags();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Part#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(final String s, final String s1)
            throws MessagingException {
        
        // looks like we need to load the message before setting headers..
        checkParse();
        super.setHeader(s, s1);
        updateHeaders(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Part#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(final String s, final String s1)
            throws MessagingException {
        
        super.addHeader(s, s1);
        updateHeaders(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Part#removeHeader(java.lang.String)
     */
    public void removeHeader(final String s) throws MessagingException {
        super.removeHeader(s);
        updateHeaders(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.internet.MimePart#addHeaderLine(java.lang.String)
     */
    public void addHeaderLine(final String s) throws MessagingException {
        super.addHeaderLine(s);
        updateHeaders(false);
    }

    /**
     * @param tag
     */
    public void addTag(final String tag) throws MessagingException {
        // Flags flags = new Flags();
        // Tags tags = getTags();
        tags.add(tag);
        // setFlags(flags, true);
        updateFlags();
    }

    /**
     * @param tag
     * @throws MessagingException
     */
    public void removeTag(final String tag) throws MessagingException {
        // Flags flags = new Flags();
        // Tags tags = getTags();
        tags.remove(tag);
        // tags.add(tag);
        // setFlags(flags, false);
        updateFlags();
    }

    /**
     * Attempts to save metadata after calling <code>saveChanges</code> in the superclass.
     */
    public void saveChanges() throws MessagingException {
        super.saveChanges();
        if (delegate != null) {
            try {
                delegate.saveChanges();
            }
            catch (Exception e) {
                throw new MessagingException(
                        "Error updating message metadata", e);
            }
        }
    }

    /**
     * Attempts to update headers in metadata after updating headers in superclass.
     */
    protected void updateHeaders() throws MessagingException {
        updateHeaders(true);
    }
    
    /**
     * Attempts to update headers in metadata after updating headers in superclass.
     */
    private void updateHeaders(boolean includeDefaults) throws MessagingException {
        if (includeDefaults) {
            super.updateHeaders();
        }
        if (delegate != null) {
            try {
                delegate.setHeaders(headers);
            }
            catch (Exception e) {
                throw new MessagingException("Error updating headers", e);
            }
        }
    }

    /**
     * @throws MessagingException
     */
    private void updateFlags() throws MessagingException {
        if (delegate != null) {
            try {
                delegate.setFlags(flags);
            }
            catch (Exception e) {
                throw new MessagingException("Error updating flags", e);
            }
            // we must call explicitly even if superclass also calls as
            // superclass will make
            // call before we have updated metadata..
            saveChanges();
        }
    }
    
    /**
     * Returns tags associated with this message. Note that any changes made to the returned
     * instance will not affect this message.
     *
     * @return Returns the tags.
     */
    public Tags getTags() throws MessagingException {
        return new Tags(getFlags());
    }

    /**
     * Returns the UID associated with the message.
     *
     * @return a long representation of a UID, or -1 if no UID is assigned
     */
    protected long getUid() {
        if (delegate != null) {
            try {
                return delegate.getUid();
            }
            catch (Exception e) {
                log.error("Error retrieving uid", e);
            }
        }
        return -1;
    }
}
