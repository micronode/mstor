/*
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Contributors: Paul Legato - fix for expunge() method
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
package net.fortuna.mstor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.FolderEvent;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * A folder implementation for the mstor javamail provider.
 * 
 * @author Ben Fortuna
 */
public final class MStorFolder extends Folder implements UIDFolder {

    /**
     * Indicates whether this folder is open.
     */
    private boolean open;

    /**
     * A delegate supporting additional functions not inherently supported by {@link Folder}.
     */
    private FolderDelegate<? extends MessageDelegate> delegate;

    /**
     * A cache for messages.
     */
//    private Map messageCache;

    private MStorStore mStore;

    /**
     * Constructs a new mstor folder instance.
     * 
     * @param store
     * @param file
     */
    public MStorFolder(final MStorStore store, final FolderDelegate<? extends MessageDelegate> delegate) {
        super(store);
        this.mStore = store;
        this.delegate = delegate;

        // automatically close (release resources) when the
        // store is closed..
        // XXX: This will not work as connection events are queued and not
        // guaranteed for delivery prior to completion of store.close()..
        /*
        store.addConnectionListener(new ConnectionListener() {

            public final void closed(final ConnectionEvent e) {
                try {
                    if (isOpen()) {
                        close(false);
                    }
                }
                catch (MessagingException me) {
                    log.error("Error closing folder [" + this + "]", me);
                }
            }

            public final void disconnected(final ConnectionEvent e) {
            }

            public final void opened(final ConnectionEvent e) {
            }
        });
        */
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getFolderName()
     */
    public String getName() {
        return delegate.getFolderName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getFullName()
     */
    public String getFullName() {
        return delegate.getFullName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getParent()
     */
    public Folder getParent() throws MessagingException {
        return new MStorFolder(mStore, delegate.getParent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#exists()
     */
    public boolean exists() throws MessagingException {
        return delegate.exists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#list(java.lang.String)
     */
    public Folder[] list(final String pattern) throws MessagingException {
        if ((getType() & HOLDS_FOLDERS) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        List<Folder> folders = new ArrayList<Folder>();

        FolderDelegate<? extends MessageDelegate>[] childDelegates = delegate.list(pattern);
        for (int i = 0; i < childDelegates.length; i++) {
            folders.add(new MStorFolder(mStore, childDelegates[i]));
        }
        return folders.toArray(new Folder[folders.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getSeparator()
     */
    public char getSeparator() throws MessagingException {
        assertExists();
        return delegate.getSeparator();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getType()
     */
    public int getType() throws MessagingException {
        assertExists();
        return delegate.getType();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#create(int)
     */
    public boolean create(final int type) throws MessagingException {
        if (exists()) {
            throw new MessagingException("Folder already exists");
        }

        boolean created = delegate.create(type);
        if (created) {
            notifyFolderListeners(FolderEvent.CREATED);
        }
        return created;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#hasNewMessages()
     */
    public boolean hasNewMessages() throws MessagingException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getFolder(java.lang.String)
     */
    public Folder getFolder(final String name) throws MessagingException {
        return new MStorFolder(mStore, delegate.getFolder(name));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#delete(boolean)
     */
    public boolean delete(final boolean recurse)
        throws MessagingException {
        
        assertClosed();

        if ((getType() & HOLDS_FOLDERS) > 0) {
            if (recurse) {
                Folder[] subfolders = list();
                for (int i = 0; i < subfolders.length; i++) {
                    subfolders[i].delete(recurse);
                }
            }
            else if (list().length > 0) {
                // cannot delete if has subfolders..
                return false;
            }
        }

        // attempt to delete the directory/file..
        boolean deleted = delegate.delete();
        if (deleted) {
            notifyFolderListeners(FolderEvent.DELETED);
        }
        return deleted;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#renameTo(javax.mail.Folder)
     */
    public boolean renameTo(final Folder folder)
            throws MessagingException {
        
        assertExists();
        assertClosed();

        boolean renamed = delegate.renameTo(folder.getName());
        if (renamed) {
            notifyFolderRenamedListeners(folder);
        }
        return renamed;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#open(int)
     */
    public void open(final int mode) throws MessagingException {
        assertExists();
        assertClosed();

        delegate.open(mode);
        this.mode = mode;
        open = true;

        // notify listeners only if successfully opened..
        notifyConnectionListeners(ConnectionEvent.OPENED);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#close(boolean)
     */
    public void close(final boolean expunge) throws MessagingException {
        assertOpen();
        if (expunge) {
            expunge();
        }

        // clear cache..
        getMessageCache().removeAll();
        
        // mark as closed and notify listeners even if not successfully closed..
        open = false;
        notifyConnectionListeners(ConnectionEvent.CLOSED);
        
        delegate.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#isOpen()
     */
    public boolean isOpen() {
        return open;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getPermanentFlags()
     */
    public Flags getPermanentFlags() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getMessageCount()
     */
    public int getMessageCount() throws MessagingException {
        assertExists();
        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        if (!isOpen()) {
            return -1;
        }
        else {
            return delegate.getMessageCount();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getMessage(int)
     */
    public Message getMessage(final int index) throws MessagingException {
        assertExists();
        assertOpen();

        if (index <= 0 || index > getMessageCount()) {
            throw new IndexOutOfBoundsException("Message [" + index + "] does not exist");
        }

        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        Message message = null;
        
        Element cacheElement = getMessageCache().get(index);
        if (cacheElement != null) {
            message = (Message) cacheElement.getValue();
        }

        if (message == null) {
            try {
                // javamail uses 1-based indexing for messages..
                message = new MStorMessage(this,
                        delegate.getMessageAsStream(index), index,
                        delegate.getMessage(index));

                getMessageCache().put(new Element(index, message));
            }
            catch (IOException ioe) {
                throw new MessagingException("Error ocurred reading message ["
                        + index + "]", ioe);
            }
            catch (DelegateException de) {
                throw new MessagingException("Error ocurred reading message ["
                        + index + "]", de);
            }
        }

        return message;
    }

    /**
     * Appends the specified messages to this folder. NOTE: The specified message array is destroyed
     * upon processing to alleviate memory concerns with large messages. You should ensure the
     * messages specified in this array are referenced elsewhere if you want to retain them.
     */
    public void appendMessages(final Message[] messages)
            throws MessagingException {
        
        assertExists();

        delegate.appendMessages(messages);

        // notify listeners..
        notifyMessageAddedListeners(messages);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#expunge()
     */
    public Message[] expunge() throws MessagingException {
        assertExists();
        assertOpen();

        if (Folder.READ_ONLY == getMode()) {
            throw new MessagingException("Folder is read-only");
        }

        int count = getDeletedMessageCount();

        List<Message> deletedList = new ArrayList<Message>();
        for (int i = 1; i <= getMessageCount() && deletedList.size() < count; i++) {
            Message message = getMessage(i);
            if (message.isSet(Flags.Flag.DELETED)) {
                deletedList.add(message);
            }
        }

        MStorMessage[] deleted = deletedList.toArray(new MStorMessage[deletedList.size()]);

        delegate.expunge(deleted);

        for (int i = 0; i < deleted.length; i++) {
            deleted[i].setExpunged(true);
        }

        // notify listeners..
        notifyMessageRemovedListeners(true, deleted);
        
        // reset cache..
//        messageCache = null;
        getMessageCache().removeAll();

        return deleted;
    }

    /**
     * @return Returns the messageCache.
     */
    private Cache getMessageCache() {
        CacheManager manager = CacheManager.create();
        String cacheName = "mstor.folder." + getFullName().hashCode();
        if (manager.getCache(cacheName) == null) {
            manager.addCache(cacheName);
        }
        return manager.getCache(cacheName);
    }

    /**
     * Check if this folder is open.
     *
     * @throws IllegalStateException thrown if the folder is not open
     */
    private void assertOpen() {
        if (!isOpen()) {
            throw new IllegalStateException("Folder not open");
        }
    }

    /**
     * Check if this folder is closed.
     *
     * @throws IllegalStateException thrown if the folder is not closed
     */
    private void assertClosed() {
        if (isOpen()) {
            throw new IllegalStateException("Folder not closed");
        }
    }

    /**
     * Asserts that this folder exists.
     *
     * @throws FolderNotFoundException
     * @throws MessagingException
     */
    private void assertExists() throws MessagingException {
        if (!exists()) {
            throw new FolderNotFoundException(this, "Folder [" + getName()
                    + "] does not exist.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.UIDFolder#getMessageByUID(long)
     */
    public Message getMessageByUID(long uid) throws MessagingException {
        for (int i = 1; i <= getMessageCount(); i++) {
            MStorMessage message = (MStorMessage) getMessage(i);
            if (message.getUid() == uid) {
                return message;
            }
        }
        throw new MessagingException("Message with UID [" + uid
                + "] does not exist");
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.UIDFolder#getMessagesByUID(long, long)
     */
    public Message[] getMessagesByUID(long start, long end)
            throws MessagingException {

        long lastUid = end;
        if (end == LASTUID) {
            try {
                lastUid = delegate.getLastUid();
            }
            catch (UnsupportedOperationException uoe) {
                throw new MessagingException("Error retrieving UID", uoe);
            }
        }
        List<Message> messages = new ArrayList<Message>();
        for (long uid = start; uid <= lastUid; uid++) {
            messages.add(getMessageByUID(uid));
        }
        return messages.toArray(new Message[messages.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.UIDFolder#getMessagesByUID(long[])
     */
    public Message[] getMessagesByUID(long[] uids) throws MessagingException {
        List<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < uids.length; i++) {
            messages.add(getMessageByUID(uids[i]));
        }
        return messages.toArray(new Message[messages.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.UIDFolder#getUID(javax.mail.Message)
     */
    public long getUID(Message message) throws MessagingException {
        if (!(message instanceof MStorMessage)) {
            throw new MessagingException("Incompatible message type");
        }
        return ((MStorMessage) message).getUid();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.UIDFolder#getUIDValidity()
     */
    public long getUIDValidity() throws MessagingException {
        try {
            return delegate.getUidValidity();
        }
        catch (UnsupportedOperationException uoe) {
            throw new MessagingException(
                    "An error occurred retrieving UID validity", uoe);
        }
    }
}
