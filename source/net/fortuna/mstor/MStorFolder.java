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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;

import net.fortuna.mstor.data.MboxFile;
import net.fortuna.mstor.data.MetaFolderImpl;
import net.fortuna.mstor.util.Cache;
import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A folder implementation for the mstor javamail provider.
 * @author Ben Fortuna
 */
public class MStorFolder extends Folder implements UIDFolder {

    private static final String DIR_EXTENSION = ".sbd";
    
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private Log log = LogFactory.getLog(MStorFolder.class);

    /**
     * Indicates whether this folder holds messages or other folders.
     */
    private int type;

    /**
     * Indicates whether this folder is open.
     */
    private boolean open;

    /**
     * The file this folder is associated with.
     */
    private File file;

    /**
     * An mbox file where the folder holds messages. This variable is not
     * applicable (and therefore not initialised) for folders that hold other
     * folders.
     */
    private MboxFile mbox;

    /**
     * Additional metadata for an mstor folder that is not provided by the
     * standard mbox format.
     */
    private MetaFolder meta;

    /**
     * A cache for messages.
     */
    private Map messageCache;

    private MStorStore mStore;

    /**
     * Constructs a new mstor folder instance.
     * @param store
     * @param file
     */
    public MStorFolder(final MStorStore store, final File file) {
        super(store);
        this.mStore = store;
        this.file = file;
        if (file.isDirectory()) {
            type = HOLDS_FOLDERS;
        } else {
            type = HOLDS_FOLDERS | HOLDS_MESSAGES;
        }
        
        // automatically close (release resources) when the
        // store is closed..
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getName()
     */
    public final String getName() {
        return file.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getFullName()
     */
    public final String getFullName() {
        return file.getPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getParent()
     */
    public final Folder getParent() throws MessagingException {
        return new MStorFolder(mStore, file.getParentFile());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#exists()
     */
    public final boolean exists() throws MessagingException {
        return file.exists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#list(java.lang.String)
     */
    public final Folder[] list(final String pattern) throws MessagingException {
        if ((getType() & HOLDS_FOLDERS) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        List folders = new ArrayList();

        File[] files = null;

        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files = new File(file.getAbsolutePath() + DIR_EXTENSION)
                    .listFiles();
        }

        for (int i = 0; files != null && i < files.length; i++) {
            if (!files[i].getName().endsWith(MetaFolderImpl.FILE_EXTENSION)
                    && !files[i].getName().endsWith(DIR_EXTENSION)
                    && (files[i].isDirectory() || files[i].length() == 0
                            || MboxFile.isValid(files[i]))) {
                // && ((type & Folder.HOLDS_MESSAGES) == 0
                // || !files[i].isDirectory())) {
                folders.add(new MStorFolder(mStore, files[i]));
            }
        }

        return (Folder[]) folders.toArray(new Folder[folders.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getSeparator()
     */
    public final char getSeparator() throws MessagingException {
        assertExists();
        return File.separatorChar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getType()
     */
    public final int getType() throws MessagingException {
        assertExists();
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#create(int)
     */
    public final boolean create(final int type) throws MessagingException {
        if (file.exists()) {
            throw new MessagingException("Folder already exists");
        }

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Creating folder [" + file.getAbsolutePath() + "]");
        }

        boolean created = false;
        if ((type & HOLDS_MESSAGES) > 0) {
            this.type = type;

            try {
                file.getParentFile().mkdirs();
                created = file.createNewFile();
            } catch (IOException ioe) {
                throw new MessagingException("Unable to create folder [" + file
                        + "]", ioe);
            }
        } else if ((type & HOLDS_FOLDERS) > 0) {
            this.type = type;
            created = file.mkdirs();
        } else {
            throw new MessagingException("Invalid folder type");
        }
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
    public final boolean hasNewMessages() throws MessagingException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#getFolder(java.lang.String)
     */
    public final Folder getFolder(final String name) throws MessagingException {
        File file = null;

        // if path is absolute don't use relative file..
        if (name.startsWith("/")) {
            file = new File(name);
        }
        // default folder..
        // else if ("".equals(getName())) {
        // if a folder doesn't hold messages (ie. default
        // folder) we don't have a separate subdirectory
        // for sub-folders..
        else if ((getType() & HOLDS_MESSAGES) == 0) {
            file = new File(this.file, name);
        } else {
            file = new File(this.file.getAbsolutePath() + DIR_EXTENSION, name);
        }

        // we need to initialise the metadata name in case
        // the folder does not exist..
        return new MStorFolder(mStore, file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#delete(boolean)
     */
    public final boolean delete(final boolean recurse) throws MessagingException {
        assertClosed();

        if ((getType() & HOLDS_FOLDERS) > 0) {
            if (recurse) {
                Folder[] subfolders = list();

                for (int i = 0; i < subfolders.length; i++) {
                    subfolders[i].delete(recurse);
                }
            } else if (list().length > 0) {
                // cannot delete if has subfolders..
                return false;
            }
        }

        File metafile = new File(file.getAbsolutePath()
                + MetaFolderImpl.FILE_EXTENSION);
        metafile.delete();

        // attempt to delete the directory/file..
        boolean deleted = file.delete();
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
    public final boolean renameTo(final Folder folder) throws MessagingException {
        assertExists();
        assertClosed();

        boolean renamed = file.renameTo(new File(file.getParent(), folder.getName()));
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
    public final void open(final int mode) throws MessagingException {
        assertExists();
        assertClosed();

        if ((getType() & HOLDS_MESSAGES) > 0) {
            if (mode == READ_WRITE) {
                openMbox(MboxFile.READ_WRITE);
            }
            else {
                openMbox(MboxFile.READ_ONLY);
            }
        }

        this.mode = mode;
        open = true;
        
        // notify listeners only if successfully opened..
        notifyConnectionListeners(ConnectionEvent.OPENED);
    }
    
    /**
     * Create a new reference to mbox file.
     * @param mode
     */
    private void openMbox(final String mode) {
        mbox = new MboxFile(file, mode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#close(boolean)
     */
    public final void close(final boolean expunge) throws MessagingException {
        assertOpen();
        if (expunge) {
            expunge();
        }

        // notify listeners and mark as closed even if not successfully closed..
        notifyConnectionListeners(ConnectionEvent.CLOSED);
        open = false;

        try {
            closeMbox();
        }
        catch (IOException ioe) {
            throw new MessagingException("Error ocurred closing mbox file", ioe);
        }
    }

    /**
     * Close and clear mbox file reference.
     * @throws IOException
     */
    private void closeMbox() throws IOException {
        mbox.close();
        mbox = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#isOpen()
     */
    public final boolean isOpen() {
        return open;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getPermanentFlags()
     */
    public final Flags getPermanentFlags() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getMessageCount()
     */
    public final int getMessageCount() throws MessagingException {
        assertExists();
        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        if (!isOpen()) {
            return -1;
        } else {
            try {
                return mbox.getMessageCount();
            } catch (IOException ioe) {
                throw new MessagingException(
                        "Error ocurred reading message count", ioe);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Folder#getMessage(int)
     */
    public final Message getMessage(final int index) throws MessagingException {
        assertExists();
        assertOpen();

        if (index <= 0 || index > getMessageCount()) {
            throw new IndexOutOfBoundsException("Message does not exist");
        }

        if ((getType() & HOLDS_MESSAGES) == 0) {
            throw new MessagingException("Invalid folder type");
        }

        MStorMessage message = (MStorMessage) getMessageCache().get(
                String.valueOf(index));

        if (message == null) {
            try {
                // javamail uses 1-based indexing for messages..
                message = new MStorMessage(this, mbox
                        .getMessageAsStream(index - 1), index);
                if (mStore.isMetaEnabled()) {
                    message.setMeta(getMeta().getMessage(message));
                }
                getMessageCache().put(String.valueOf(index), message);
            } catch (IOException ioe) {
                throw new MessagingException("Error ocurred reading message ["
                        + index + "]", ioe);
            }
        }

        return message;
    }

    /**
     * Appends the specified messages to this folder. NOTE: The specified
     * message array is destroyed upon processing to alleviate memory concerns
     * with large messages. You should ensure the messages specified in this
     * array are referenced elsewhere if you want to retain them.
     */
    public final void appendMessages(final Message[] messages)
            throws MessagingException {
        assertExists();

        Date received = new Date();
        ByteArrayOutputStream out = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);

        // Messages may be appended to a closed folder. So if the folder is closed,
        // create a temporary reference to the mbox file to append messages..
        if (mbox == null) {
            openMbox(MboxFile.READ_WRITE);
        }
        
        for (int i = 0; i < messages.length; i++) {
            try {
                out.reset();

                if (CapabilityHints.VALUE_MOZILLA_COMPATIBILITY_ENABLED.equals(
                        CapabilityHints.getHint(CapabilityHints.KEY_MOZILLA_COMPATIBILITY))) {
                    
                    messages[i].setHeader("X-Mozilla-Status", "0000");
                    messages[i].setHeader("X-Mozilla-Status-2", "00000000");
                }
                
                messages[i].writeTo(out);
                mbox.appendMessage(out.toByteArray());

                // create metadata..
                if (mStore.isMetaEnabled()) {
                    MetaMessage messageMeta = getMeta().getMessage(messages[i]);
                    if (messageMeta != null) {
                        messageMeta.setReceived(received);
                        messageMeta.setFlags(messages[i].getFlags());
                        messageMeta.setHeaders(messages[i].getAllHeaders());
                        getMeta().allocateUid(messageMeta);
                    }
                }

                // prune messages as we go to allow for garbage
                // collection..
                messages[i] = null;
            } catch (IOException ioe) {
                log.debug("Error appending message [" + i + "]", ioe);
                throw new MessagingException("Error appending message [" + i
                        + "]", ioe);
            }
        }

        // save metadata..
        if (mStore.isMetaEnabled()) {
            try {
                getMeta().save();
            } catch (IOException ioe) {
                log.error("Error ocurred saving metadata", ioe);
            }
        }
        
        // if mbox is not really open, ensure it is closed again..
        if (mbox != null && !isOpen()) {
            try {
                closeMbox();
            }
            catch (IOException ioe) {
                throw new MessagingException("Error appending messages", ioe);
            }
        }
        
        // notify listeners..
        notifyMessageAddedListeners(messages);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Folder#expunge()
     */
    public final Message[] expunge() throws MessagingException {
        assertExists();

        assertOpen();

        if (Folder.READ_ONLY == getMode()) {
            throw new MessagingException("Folder is read-only");
        }

        int count = getDeletedMessageCount();

        List deletedList = new ArrayList();

        for (int i = 1; i <= getMessageCount() && deletedList.size() < count; i++) {
            Message message = getMessage(i);
            if (message.isSet(Flags.Flag.DELETED)) {
                deletedList.add(message);
            }
        }

        MStorMessage[] deleted = (MStorMessage[]) deletedList
                .toArray(new MStorMessage[deletedList.size()]);

        int[] mboxIndices = new int[deleted.length];
        int[] metaIndices = new int[deleted.length];

        for (int i = 0; i < deleted.length; i++) {
            // have to subtract one, because the raw storage array is 0-based,
            // but
            // the message numbers are 1-based
            mboxIndices[i] = deleted[i].getMessageNumber() - 1;
            metaIndices[i] = deleted[i].getMessageNumber();
        }

        try {
            mbox.purge(mboxIndices);
        }
        catch (IOException ioe) {
            throw new MessagingException("Error purging mbox file", ioe);
        }

        if (mStore.isMetaEnabled()) {
            try {
                getMeta().removeMessages(metaIndices);
                getMeta().save();
            }
            catch (IOException ioe) {
                throw new MessagingException("Error updating metadata", ioe);
            }
        }

        for (int i = 0; i < deleted.length; i++) {
            deleted[i].setExpunged(true);
        }
        
        // notify listeners..
        notifyMessageRemovedListeners(true, deleted);

        return deleted;
    }

    /**
     * @return Returns the messageCache.
     */
    private Map getMessageCache() {
        if (messageCache == null) {
            messageCache = new Cache();
        }

        return messageCache;
    }

    /**
     * @return Returns the metadata for this folder.
     */
    protected final MetaFolder getMeta() {
        if (meta == null) {
            meta = new MetaFolderImpl(new File(getFullName()
                    + MetaFolderImpl.FILE_EXTENSION));
        }
        return meta;
    }

    /**
     * Check if this folder is open.
     * 
     * @throws IllegalStateException
     *             thrown if the folder is not open
     */
    private void assertOpen() {
        if (!isOpen()) {
            throw new IllegalStateException("Folder not open");
        }
    }

    /**
     * Check if this folder is closed.
     * 
     * @throws IllegalStateException
     *             thrown if the folder is not closed
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
            throw new FolderNotFoundException(this, "File [" + file + " does not exist.");
        }
    }

    /* (non-Javadoc)
     * @see javax.mail.UIDFolder#getMessageByUID(long)
     */
    public Message getMessageByUID(long uid) throws MessagingException {
        for (int i = 1; i <= getMessageCount(); i++) {
            MStorMessage message = (MStorMessage) getMessage(i);
            if (message.getUid() == uid) {
                return message;
            }
        }
        throw new MessagingException(
                "Message with UID [" + uid + "] does not exist");
    }

    /* (non-Javadoc)
     * @see javax.mail.UIDFolder#getMessagesByUID(long, long)
     */
    public Message[] getMessagesByUID(long start, long end) throws MessagingException {
        
        if (!mStore.isMetaEnabled()) {
            throw new MessagingException("Metadata must be enabled for UIDFolder support");
        }
        
        long lastUid = end;
        if (end == LASTUID) {
            lastUid = getMeta().getLastUid();
        }
        List messages = new ArrayList();
        for (long uid = start; uid <= lastUid; uid++) {
            messages.add(getMessageByUID(uid)); 
        }
        return (Message[]) messages.toArray(new Message[messages.size()]);
    }

    /* (non-Javadoc)
     * @see javax.mail.UIDFolder#getMessagesByUID(long[])
     */
    public Message[] getMessagesByUID(long[] uids) throws MessagingException {
        List messages = new ArrayList();
        for (int i = 0; i < uids.length; i++) {
            messages.add(getMessageByUID(uids[i])); 
        }
        return (Message[]) messages.toArray(new Message[messages.size()]);
    }

    /* (non-Javadoc)
     * @see javax.mail.UIDFolder#getUID(javax.mail.Message)
     */
    public long getUID(Message message) throws MessagingException {
        if (!(message instanceof MStorMessage)) {
            throw new MessagingException("Incompatible message type");
        }
        return ((MStorMessage) message).getUid();
    }

    /* (non-Javadoc)
     * @see javax.mail.UIDFolder#getUIDValidity()
     */
    public long getUIDValidity() throws MessagingException {
        
        if (!mStore.isMetaEnabled()) {
            throw new MessagingException("Metadata must be enabled for UIDFolder support");
        }
        
        try {
            return getMeta().getUidValidity();
        }
        catch (IOException ioe) {
            throw new MessagingException(
                    "An error occurred retrieving UID validity", ioe);
        }
    }
}
