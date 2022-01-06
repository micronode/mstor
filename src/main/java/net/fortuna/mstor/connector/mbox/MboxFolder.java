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
package net.fortuna.mstor.connector.mbox;

import net.fortuna.mstor.connector.AbstractFolderDelegate;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.MboxFile;
import net.fortuna.mstor.util.CapabilityHints;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 30/07/2007
 * </pre>
 * 
 *
 */
public class MboxFolder extends AbstractFolderDelegate<MessageDelegate> {

    private static final String DIR_EXTENSION = ".sbd";

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static final FileFilter SUBFOLDER_FILTER = new FileFilter() {
        
        public boolean accept(File pathname) {
            if (pathname.getName().endsWith(MetaFolder.FILE_EXTENSION)
                    || pathname.getName().endsWith(YamlMetaFolder.FILE_EXTENSION)) {
                return false;
            }
            else if (pathname.getName().endsWith(DIR_EXTENSION)) {
                return false;
            }
            else return pathname.isDirectory() || pathname.getName().startsWith(".")
                        || pathname.length() < 0 || MboxFile.isValid(pathname);
        }
    };
    
    private Log log = LogFactory.getLog(MboxFolder.class);
    
    private File file;
    
    private MboxFile mbox;
    
    private int type;
    
    /**
     * @param file an mbox data file
     */
    public MboxFolder(File file) {
        this.file = file;
        if (file.isDirectory()) {
            type = Folder.HOLDS_FOLDERS;
        }
        else {
            type = Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getType() {
        return type;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getFolderName() {
        return file.getName();
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getFullName() {
        return file.getPath();
    }
    
    /**
     * {@inheritDoc}
     */
    public final FolderDelegate<MessageDelegate> getParent() {
        return new MboxFolder(file.getParentFile());
    }
    
    /**
     * {@inheritDoc}
     */
    public final FolderDelegate<MessageDelegate> getFolder(final String name) {
        File file;

        // if path is absolute don't use relative file..
        if (name.startsWith("/")) {
            file = new File(name);
        }
        // default folder..
        // else if ("".equals(getName())) {
        // if a folder doesn't hold messages (ie. default
        // folder) we don't have a separate subdirectory
        // for sub-folders..
        else if ((getType() & Folder.HOLDS_MESSAGES) == 0) {
            file = new File(this.file, name);
        }
        else {
            file = new File(this.file.getAbsolutePath() + DIR_EXTENSION, name);
        }

        // we need to initialise the metadata name in case
        // the folder does not exist..
        return new MboxFolder(file);
    }
    
    /**
     * {@inheritDoc}
     */
    public final FolderDelegate<MessageDelegate>[] list(final String pattern) {
        List<MboxFolder> folders = new ArrayList<MboxFolder>();

        File[] files;
        if (file.isDirectory()) {
            files = file.listFiles(SUBFOLDER_FILTER);
        }
        else {
            files = new File(file.getAbsolutePath() + DIR_EXTENSION)
                    .listFiles(SUBFOLDER_FILTER);
        }

        for (int i = 0; files != null && i < files.length; i++) {
            folders.add(new MboxFolder(files[i]));
        }
        return folders.toArray(new MboxFolder[folders.size()]);
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean exists() {
        return file.exists();
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean delete() {
        return file.delete();
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean renameTo(String name) {
        return file.renameTo(new File(file.getParent(), name));
    }
    
    /**
     * {@inheritDoc}
     */
    public final void open(final int mode) {
        if ((getType() & Folder.HOLDS_MESSAGES) > 0) {
            if (mode == Folder.READ_WRITE) {
                mbox = new MboxFile(file, MboxFile.READ_WRITE);
            }
            else {
                mbox = new MboxFile(file, MboxFile.READ_ONLY);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void close() throws MessagingException {
        try {
            mbox.close();
        }
        catch (IOException ioe) {
            throw new MessagingException("Error ocurred closing mbox file", ioe);
        }
        finally {
            mbox = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final char getSeparator() {
        return File.separatorChar;
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getMessageCount() throws MessagingException {
        try {
            return mbox.getMessageCount();
        }
        catch (IOException ioe) {
            throw new MessagingException("Error ocurred reading message count", ioe);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final InputStream getMessageAsStream(int index) throws IOException {
        return mbox.getMessageAsStream(index - 1);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void appendMessages(Message[] messages) throws MessagingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(
                DEFAULT_BUFFER_SIZE);

        boolean folderClosed = false;
        // Messages may be appended to a closed folder. So if the folder is closed,
        // create a temporary reference to the mbox file to append messages..
        if (mbox == null) {
            folderClosed = true;
            mbox = new MboxFile(file, MboxFile.READ_WRITE);
        }

        for (int i = 0; i < messages.length; i++) {
            try {
                out.reset();

                if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_MOZILLA_COMPATIBILITY)) {

                    messages[i].setHeader("X-Mozilla-Status", "0000");
                    messages[i].setHeader("X-Mozilla-Status-2", "00000000");
                }

                messages[i].writeTo(out);
                mbox.appendMessage(out.toByteArray());

                // prune messages as we go to allow for garbage
                // collection..
                messages[i] = null;
            }
            catch (Exception e) {
                throw new MessagingException("Error appending message [" + i + "]", e);
            }
        }

        // if mbox is not really open, ensure it is closed again..
        if (mbox != null && folderClosed) {
            try {
                mbox.close();
            }
            catch (IOException ioe) {
                throw new MessagingException("Error appending messages", ioe);
            }
            finally {
                mbox = null;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean create(final int type) throws MessagingException {

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Creating folder [" + file.getAbsolutePath() + "]");
        }

        boolean created;
        if ((type & Folder.HOLDS_MESSAGES) > 0) {
            this.type = type;

            try {
                file.getParentFile().mkdirs();
                created = file.createNewFile();
            }
            catch (IOException ioe) {
                throw new MessagingException("Unable to create folder ["
                        + file.getAbsolutePath() + "]", ioe);
            }
        }
        else if ((type & Folder.HOLDS_FOLDERS) > 0) {
            this.type = type;
            created = file.mkdirs();
        }
        else {
            throw new MessagingException("Invalid folder type");
        }
        return created;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void expunge(Message[] deleted) throws MessagingException {
        
        // No need to do anything if no messages have been deleted..
        if (deleted.length == 0) {
            return;
        }
        
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
    }
    
    /**
     * {@inheritDoc}
     */
    protected MessageDelegate createMessage(int messageNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public MessageDelegate getMessage(int messageNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void setLastUid(long uid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Mbox format does not support UID folders");
    }

    /**
     * {@inheritDoc}
     */
    public long getLastUid() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Mbox format does not support UID folders");
    }

    /**
     * {@inheritDoc}
     */
    public long getUidValidity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Mbox format does not support UID folders");
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified() throws UnsupportedOperationException {
        return file.lastModified();
    }
}
