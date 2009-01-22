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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.connector.AbstractFolderDelegate;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;

/**
 * Base implementation of a meta folder.
 * 
 * @author benfortuna
 */
public abstract class AbstractMetaFolder<T extends MessageDelegate> extends AbstractFolderDelegate<T> {

    private static final Random UID_VALIDITY_GENERATOR = new Random();

    private File file;
    
    /**
     * A delegate used by metafolder to perform operations not supported in metadata.
     */
    private FolderDelegate<MessageDelegate> delegate;
    
    /**
     * Constructs a new meta folder instance.
     * 
     * @param file the meta folder file
     */
    public AbstractMetaFolder(FolderDelegate<MessageDelegate> delegate) {
        this.file = getMetaFile(delegate);
        this.delegate = delegate;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getType()
     */
    public final int getType() {
        return delegate.getType();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getName()
     */
    public final String getName() {
        return delegate.getName();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getFullName()
     */
    public final String getFullName() {
        return delegate.getFullName();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#exists()
     */
    public final boolean exists() {
        return delegate.exists();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#delete()
     */
    public final boolean delete() {
        return delegate.delete() && file.delete();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#renameTo(java.lang.String)
     */
    public final boolean renameTo(String name) {
        return delegate.renameTo(name)
            && (!file.exists() || file.renameTo(
                    new File(file.getParent(), name + getFileExtension())));
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#open(int)
     */
    public final void open(int mode) {
        delegate.open(mode);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#close()
     */
    public final void close() throws MessagingException {
        delegate.close();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getSeparator()
     */
    public final char getSeparator() {
        return delegate.getSeparator();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageCount()
     */
    public final int getMessageCount() throws MessagingException {
        return delegate.getMessageCount();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getMessageAsStream(int)
     */
    public final InputStream getMessageAsStream(int index) throws IOException {
        return delegate.getMessageAsStream(index);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#appendMessages(javax.mail.Message[])
     */
    @SuppressWarnings("unchecked")
    public final void appendMessages(Message[] messages) throws MessagingException {
        try {
            Date received = new Date();
            for (int i = 0; i < messages.length; i++) {
                T md = getMessage(messages[i].getMessageNumber());
                md.setReceived(received);
                md.setFlags(messages[i].getFlags());
                md.setHeaders(messages[i].getAllHeaders());
                allocateUid(md);
            }
            delegate.appendMessages(messages);
            save();
        }
        catch (DelegateException de) {
            throw new MessagingException("Error saving changes", de);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#create(int)
     */
    public final boolean create(int type) throws MessagingException {
        return delegate.create(type);
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#expunge(javax.mail.Message[])
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.FolderDelegate#getLastModified()
     */
    public final long getLastModified() throws UnsupportedOperationException {
        return delegate.getLastModified();
    }
}
