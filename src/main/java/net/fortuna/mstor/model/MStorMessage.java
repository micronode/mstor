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
package net.fortuna.mstor.model;

import net.fortuna.mstor.connector.MessageDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Implementation of a message for the mstor javamail provider.
 *
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [7/07/2004]
 * </pre>
 * 
 */
public final class MStorMessage extends MimeMessage implements Serializable, Taggable {

    private final Log log = LogFactory.getLog(MStorMessage.class);

    /**
     * Delegate for providing additional functions not supported by MimeMessage.
     */
    private MessageDelegate delegate;

    private InputStream in;

    private boolean loaded;

    private final Tags tags;

    /**
     * @param session the session associated with the message
     */
    public MStorMessage(final Session session) {
        this(session, null);
    }

    /**
     * @param session the session associated with the message
     * @param in an input stream to read message data from
     */
    public MStorMessage(final Session session, final InputStream in) {
        super(session);
        this.in = in;
        tags = new Tags(flags);
    }

    /**
     * @param m a message instance to duplicate
     * @throws MessagingException where an unexpected error occurs duplicating the specified message
     */
    public MStorMessage(final MimeMessage m) throws MessagingException {
        super(m);
        tags = new Tags(flags);
    }

    /**
     * @param folder the folder associated with the message
     * @param msgnum the message number of the message in the specified folder
     */
    public MStorMessage(final Folder folder, final int msgnum) {
        this(folder, null, msgnum);
    }

    /**
     * @param folder the folder associated with the message
     * @param in an input stream to read message data from
     * @param msgnum the message number of the message in the specified folder
     */
    public MStorMessage(final Folder folder, final InputStream in,
            final int msgnum) {
        
        this(folder, in, msgnum, null);
    }
    
    /**
     * @param delegate a delegate providing implementation-specific message functionality
     */
    public MStorMessage(MessageDelegate delegate) {
        this(null, null, delegate.getMessageNumber(), delegate);
    }
    
    /**
     * @param folder the folder associated with the message
     * @param in an input stream to read message data from
     * @param msgnum the message number of the message in the specified folder
     * @param delegate a delegate providing implementation-specific message functionality
     */
    public MStorMessage(final Folder folder, final InputStream in,
            final int msgnum, MessageDelegate delegate) {
        
        super(folder, msgnum);
        if (in != null) {
            this.in = in;
        }
        else {
            this.in = delegate.getInputStream();
        }
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
     * @param folder the folder associated with the message
     * @param headers headers for the message instance
     * @param content message data
     * @param msgnum the message number of the message in the specified folder
     * @throws MessagingException where an unexpected error occurs
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getAllHeaderLines() throws MessagingException {
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getAllHeaderLines();
        }
        checkParse();
        return super.getAllHeaderLines();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getAllHeaders() throws MessagingException {
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getAllHeaders();
        }
        checkParse();
        return super.getAllHeaders();
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getMatchingHeaderLines(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getMatchingHeaderLines(names);
        }
        checkParse();
        return super.getMatchingHeaderLines(names);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getMatchingHeaders(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getMatchingHeaders(names);
        }
        checkParse();
        return super.getMatchingHeaders(names);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getNonMatchingHeaderLines(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getNonMatchingHeaderLines(names);
        }
        checkParse();
        return super.getNonMatchingHeaderLines(names);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Header> getNonMatchingHeaders(final String[] names)
            throws MessagingException {
        
        InternetHeaders headers = getHeaders();
        if (headers != null) {
            return headers.getNonMatchingHeaders(names);
        }
        checkParse();
        return super.getNonMatchingHeaders(names);
    }

    /**
     * {@inheritDoc}
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
    
    /**
     * {@inheritDoc}
     */
    protected InputStream getContentStream() throws MessagingException {
        checkParse();
        return super.getContentStream();
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    public synchronized void setFlags(final Flags flags, final boolean set)
            throws MessagingException {
        super.setFlags(flags, set);
        // copy updated flags from mime message implementation..
        updateFlags();
    }

    /**
     * {@inheritDoc}
     */
    public void setFlag(final Flag flag, final boolean set)
            throws MessagingException {
        
        super.setFlag(flag, set);
        // copy updated flags from mime message implementation..
        updateFlags();
    }

    /**
     * {@inheritDoc}
     */
    public void setHeader(final String s, final String s1)
            throws MessagingException {
        
        // looks like we need to load the message before setting headers..
        checkParse();
        super.setHeader(s, s1);
        updateHeaders(false);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeader(final String s, final String s1)
            throws MessagingException {
        
        super.addHeader(s, s1);
        updateHeaders(false);
    }

    /**
     * {@inheritDoc}
     */
    public void removeHeader(final String s) throws MessagingException {
        super.removeHeader(s);
        updateHeaders(false);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeaderLine(final String s) throws MessagingException {
        super.addHeaderLine(s);
        updateHeaders(false);
    }

    /**
     * @param tag a message model
     * @throws MessagingException where an error occurs updating tags
     */
    public void addTag(final String tag) throws MessagingException {
        // Flags flags = new Flags();
        // Tags tags = getTags();
        tags.add(tag);
        // setFlags(flags, true);
        updateFlags();
    }

    /**
     * @param tag a message model
     * @throws MessagingException where an error occurs updating tags
     */
    public void removeTag(final String tag) throws MessagingException {
        // Flags flags = new Flags();
        // Tags tags = getTags();
        tags.remove(tag);
        // tags.add(model);
        // setFlags(flags, false);
        updateFlags();
    }

    /**
     * Attempts to save metadata after calling <code>saveChanges</code> in the superclass.
     * @throws MessagingException where an error occurs in the delegate save
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
     * @throws MessagingException where an error occurs in the delegate update
     */
    protected void updateHeaders() throws MessagingException {
        updateHeaders(true);
    }
    
    /**
     * Attempts to update headers in metadata after updating headers in superclass.
     * @throws MessagingException where an error occurs in the delegate update
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
        if (delegate != null && !flags.equals(delegate.getFlags())) {
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
     * @throws MessagingException where an error occurs retrieving flags
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
    
    /**
     * @return a message instance such that this message is a reply to it 
     */
    public MStorMessage getInReplyTo() {
        if (delegate.getInReplyTo() != null) {
            return new MStorMessage(delegate.getInReplyTo());
        }
        return null;
    }
    
    /**
     * @return a list of message instances related to this message
     */
    public List<MStorMessage> getReferences() {
        List<MStorMessage> references = new ArrayList<>();
        for (MessageDelegate delegateRef : delegate.getReferences()) {
            references.add(new MStorMessage(delegateRef));
        }
        return Collections.unmodifiableList(references);
    }
}
