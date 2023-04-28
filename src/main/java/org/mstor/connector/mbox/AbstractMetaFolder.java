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
package org.mstor.connector.mbox;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.mstor.provider.mail.AbstractFolderDelegate;
import org.mstor.provider.mail.DelegateException;
import org.mstor.provider.mail.FolderDelegate;
import org.mstor.provider.mail.MessageDelegate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

/**
 * Base implementation of a meta folder.
 * 
 * @param <T> message delegate type
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: 18/08/2004
 * </pre>
 * 
 */
public abstract class AbstractMetaFolder<T extends MessageDelegate> extends AbstractFolderDelegate<T> {

    private static final Random UID_VALIDITY_GENERATOR = new Random();

    private final File file;
    
    /**
     * A delegate used by metafolder to perform operations not supported in metadata.
     */
    private final FolderDelegate<MessageDelegate> delegate;
    
    /**
     * Constructs a new meta folder instance.
     * 
     * @param file the meta folder file
     */
    AbstractMetaFolder(FolderDelegate<MessageDelegate> delegate) {
        this.file = getMetaFile(delegate);
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public final int getType() {
        return delegate.getType();
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getFolderName() {
        return delegate.getFolderName();
    }

    /**
     * {@inheritDoc}
     */
    public final String getFullName() {
        return delegate.getFullName();
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean exists() {
        return delegate.exists();
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean delete() {
        return delegate.delete() && file.delete();
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean renameTo(String name) {
        return delegate.renameTo(name)
            && (!file.exists() || file.renameTo(
                    new File(file.getParent(), name + getFileExtension())));
    }
    
    /**
     * {@inheritDoc}
     */
    public final void open(int mode) {
        delegate.open(mode);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void close() throws MessagingException {
        delegate.close();
    }
    
    /**
     * {@inheritDoc}
     */
    public final char getSeparator() {
        return delegate.getSeparator();
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getMessageCount() throws MessagingException {
        return delegate.getMessageCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public final InputStream getMessageAsStream(int index) throws IOException {
        return delegate.getMessageAsStream(index);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void appendMessages(Message[] messages) throws MessagingException {
        try {
            Date received = new Date();
            for (Message message : messages) {
                T md = getMessage(message.getMessageNumber());
                md.setReceived(received);
                md.setFlags(message.getFlags());
                md.setHeaders(message.getAllHeaders());
                allocateUid(md);
            }
            delegate.appendMessages(messages);
            save();
        }
        catch (DelegateException de) {
            throw new MessagingException("Error saving changes", de);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean create(int type) throws MessagingException {
        return delegate.create(type);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void expunge(Message[] deleted) throws MessagingException {
        delegate.expunge(deleted);
        removeMessages(deleted);
        try {
            save();
        }
        catch (DelegateException de) {
            throw new MessagingException("Error saving changes", de);
        }
    }
    
    /**
     * @return
     */
    protected int newUidValidity() {
    	return UID_VALIDITY_GENERATOR.nextInt(Integer.MAX_VALUE);
    }
    
    /**
     * @return the underlying meta file
     */
    protected final File getFile() {
    	return file;
    }
    
    /**
     * @return
     */
    protected final FolderDelegate<MessageDelegate> getDelegate() {
    	return delegate;
    }
    
    /**
     * @return
     */
    protected abstract String getFileExtension();
    
    /**
     * @param deleted
     */
    protected abstract T[] removeMessages(Message[] deleted);
    
    /**
     * @throws DelegateException
     */
    protected abstract void save() throws DelegateException;
    
    /**
     * @param delegate
     * @return
     */
    private File getMetaFile(FolderDelegate<MessageDelegate> delegate) {
        return new File(delegate.getFullName() + getFileExtension());
    }

    /**
     * {@inheritDoc}
     */
    public final long getLastModified() throws UnsupportedOperationException {
        return delegate.getLastModified();
    }
}
