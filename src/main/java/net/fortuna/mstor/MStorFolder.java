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
package net.fortuna.mstor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.FolderEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.util.CacheAdapter;
import net.fortuna.mstor.util.Configurator;
import net.fortuna.mstor.util.EhCacheAdapter;

/**
 * A folder implementation for the mstor javamail provider.
 * 
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Contributors: Paul Legato - fix for expunge() method
 * </pre>
 * 
 */
public final class MStorFolder extends Folder implements UIDFolder {

    private static final Log LOG = LogFactory.getLog(MStorFolder.class);
    
    private static final String INVALID_FOLDER_TYPE_MESSAGE = "Invalid folder type";
    
    /**
     * Indicates whether this folder is open.
     */
    private boolean open;

    /**
     * A delegate supporting additional functions not inherently supported by {@link Folder}.
     */
    private FolderDelegate<? extends MessageDelegate> delegate;
    
    /**
     * An adapter for the cache for messages.
     */
    private CacheAdapter cacheAdapter;

    /**
     * A cache for messages.
     */
//    private Map messageCache;

    private MStorStore mStore;

    /**
     * Constructs a new mstor folder instance.
     * 
     * @param store the mail store this folder belongs to
     * @param delegate a folder delegate that provides implementation-specific folder functionality
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

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return delegate.getFolderName();
    }

    /**
     * {@inheritDoc}
     */
    public String getFullName() {
        return delegate.getFullName();
    }

    /**
     * {@inheritDoc}
     */
    public Folder getParent() throws MessagingException {
        return new MStorFolder(mStore, delegate.getParent());
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists() throws MessagingException {
        return delegate.exists();
    }

    /**
     * {@inheritDoc}
     */
    public Folder[] list(final String pattern) throws MessagingException {
        if ((getType() & HOLDS_FOLDERS) == 0) {
            throw new MessagingException(INVALID_FOLDER_TYPE_MESSAGE);
        }

        final List<Folder> folders = new ArrayList<Folder>();

        final FolderDelegate<? extends MessageDelegate>[] childDelegates = delegate.list(pattern);
        for (FolderDelegate<? extends MessageDelegate> childDelegate : childDelegates) {
            folders.add(new MStorFolder(mStore, childDelegate));
        }
        return folders.toArray(new Folder[folders.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public char getSeparator() throws MessagingException {
        assertExists();
        return delegate.getSeparator();
    }

    /**
     * {@inheritDoc}
     */
    public int getType() throws MessagingException {
        assertExists();
        return delegate.getType();
    }

    /**
     * {@inheritDoc}
     */
    public boolean create(final int type) throws MessagingException {
        if (exists()) {
            throw new MessagingException("Folder already exists");
        }

        final boolean created = delegate.create(type);
        if (created) {
            notifyFolderListeners(FolderEvent.CREATED);
        }
        return created;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNewMessages() throws MessagingException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Folder getFolder(final String name) throws MessagingException {
        return new MStorFolder(mStore, delegate.getFolder(name));
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(final boolean recurse) throws MessagingException {
        assertClosed();

        boolean deleted = false;
        if ((getType() & HOLDS_FOLDERS) > 0) {
            if (recurse) {
                final CompletionService<Boolean> processor = new ExecutorCompletionService<Boolean>(
                        Executors.newCachedThreadPool());
                
                final Folder[] subfolders = list();
                for (Folder subfolder : subfolders) {
//                    subfolders[i].delete(recurse);
                    processor.submit(new DeleteFolderCommand(subfolder, recurse));
                }
                
                deleted = true;
                for (Folder subfolder : subfolders) {
                    try {
                        if (!processor.take().get()) {
                            deleted = false;
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            else if (list().length > 0) {
                // cannot delete if has subfolders..
                deleted = false;
            }
        }

        if (deleted) {
            // attempt to delete the directory/file..
            deleted = delegate.delete();
        }

        if (deleted) {
            notifyFolderListeners(FolderEvent.DELETED);
        }
        return deleted;
    }

    /**
     * {@inheritDoc}
     */
    public boolean renameTo(final Folder folder)
            throws MessagingException {
        
        assertExists();
        assertClosed();

        final boolean renamed = delegate.renameTo(folder.getName());
        if (renamed) {
            notifyFolderRenamedListeners(folder);
        }
        return renamed;
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    public void close(final boolean expunge) throws MessagingException {
        assertOpen();
        if (expunge) {
            expunge();
        }

        // clear cache..
        clearMessageCache();
        
        // mark as closed and notify listeners even if not successfully closed..
        open = false;
        notifyConnectionListeners(ConnectionEvent.CLOSED);
        
        delegate.close();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * {@inheritDoc}
     */
    public Flags getPermanentFlags() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getMessageCount() throws MessagingException {
        assertExists();
        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException(INVALID_FOLDER_TYPE_MESSAGE);
        }

        if (!isOpen()) {
            return -1;
        }
        else {
            return delegate.getMessageCount();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int getDeletedMessageCount() throws MessagingException {
        try {
            return delegate.getDeletedMessageCount();
        }
        catch (UnsupportedOperationException e) {
            LOG.debug(e.getMessage());
        }
        return super.getDeletedMessageCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage(final int index) throws MessagingException {
        assertExists();
        assertOpen();

        if (index <= 0 || index > getMessageCount()) {
            throw new IndexOutOfBoundsException("Message [" + index + "] does not exist");
        }

        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException(INVALID_FOLDER_TYPE_MESSAGE);
        }

        Message message;
    
        message = retrieveMessageFromCache(index);

        if (message == null) {
            try {
                // javamail uses 1-based indexing for messages..
                final MessageDelegate messageDelegate = delegate.getMessage(index);
                if (messageDelegate != null && messageDelegate.getInputStream() != null) {
                    message = new MStorMessage(this, messageDelegate.getInputStream(),
                            index, messageDelegate);
                }
                else {
                    message = new MStorMessage(this, delegate.getMessageAsStream(index),
                            index, messageDelegate);
                }
                putMessageIntoCache(index,message);
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
     * @param messages an array of messages to append to the folder
     * @throws MessagingException where an unexpected error occurs appending messages to the folder
     */
    public void appendMessages(final Message[] messages) throws MessagingException {
        
        assertExists();

        delegate.appendMessages(messages);

        // notify listeners..
        notifyMessageAddedListeners(messages);
    }

    /**
     * {@inheritDoc}
     */
    public Message[] expunge() throws MessagingException {
        assertExists();
        assertOpen();

        if (Folder.READ_ONLY == getMode()) {
            throw new MessagingException("Folder is read-only");
        }

        final int count = getDeletedMessageCount();

        final List<Message> deletedList = new ArrayList<Message>();
        for (int i = 1; i <= getMessageCount() && deletedList.size() < count; i++) {
            final Message message = getMessage(i);
            if (message.isSet(Flags.Flag.DELETED)) {
                deletedList.add(message);
            }
        }

        final MStorMessage[] deleted = deletedList.toArray(new MStorMessage[deletedList.size()]);

        delegate.expunge(deleted);

        for (MStorMessage aDeleted : deleted) {
            aDeleted.setExpunged(true);
        }

        // notify listeners..
        notifyMessageRemovedListeners(true, deleted);
        
        // reset cache..
//        messageCache = null;
        clearMessageCache();

        return deleted;
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

    /**
     * {@inheritDoc}
     */
    public Message getMessageByUID(long uid) throws MessagingException {
        for (int i = 1; i <= getMessageCount(); i++) {
            final MStorMessage message = (MStorMessage) getMessage(i);
            if (message.getUid() == uid) {
                return message;
            }
        }
        throw new MessagingException("Message with UID [" + uid
                + "] does not exist");
    }

    /**
     * {@inheritDoc}
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
        final List<Message> messages = new ArrayList<Message>();
        for (long uid = start; uid <= lastUid; uid++) {
            messages.add(getMessageByUID(uid));
        }
        return messages.toArray(new Message[messages.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public Message[] getMessagesByUID(long[] uids) throws MessagingException {
        final List<Message> messages = new ArrayList<Message>();
        for (long uid : uids) {
            messages.add(getMessageByUID(uid));
        }
        return messages.toArray(new Message[messages.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public long getUID(Message message) throws MessagingException {
        if (!(message instanceof MStorMessage)) {
            throw new MessagingException("Incompatible message type");
        }
        return ((MStorMessage) message).getUid();
    }

    /**
     * {@inheritDoc}
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
    
    private void clearMessageCache() {
        getCacheAdapter().clearCache();
    }
    
    private Message retrieveMessageFromCache(int index) {
        return (Message)getCacheAdapter().retrieveObjectFromCache(index);
    }
    
    private void putMessageIntoCache(int index, Message message) {
        getCacheAdapter().putObjectIntoCache(index, message);
    }
    
    private CacheAdapter getCacheAdapter() {
        if (cacheAdapter == null) {
            if (Configurator.getProperty("mstor.cache.disabled", "false").equals("true")) {
                this.cacheAdapter = new CacheAdapter();
            } else {
                this.cacheAdapter = new EhCacheAdapter("mstor.folder." + getFullName().hashCode());
            }
        }
        return cacheAdapter;
    }
    
    private static class DeleteFolderCommand implements Callable<Boolean> {
        
        private final Folder folder;
        
        private final boolean recurse;
        
        public DeleteFolderCommand(Folder folder, boolean recurse) {
            this.folder = folder;
            this.recurse = recurse;
        }
        
        /**
         * {@inheritDoc}
         */
        public Boolean call() throws Exception {
            return folder.delete(recurse);
        }
    }
}
