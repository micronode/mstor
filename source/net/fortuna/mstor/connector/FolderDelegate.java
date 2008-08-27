/*
 * $Id$
 *
 * Created on 30/07/2007
 *
 * Copyright (c) 2007, Ben Fortuna
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
package net.fortuna.mstor.connector;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.MStorFolder;

/**
 * Implementors support delegation of specific functions from {@link MStorFolder}.
 * @author Ben
 *
 */
public interface FolderDelegate {

    /**
     * @return Returns the name.
     */
    String getName();
    
    /**
     * Returns the full name of the folder.
     * @return 
     */
    String getFullName();

    /**
     * Returns the parent folder delegate of this delegate.
     * @return
     */
    FolderDelegate getParent();
    
    /**
     * Indicates whether the folder represented by this delegate exists.
     * @return
     */
    boolean exists();
    
    /**
     * Returns the type of folder this delegate represents.
     * @return
     * @see Folder#HOLDS_FOLDERS
     * @see Folder#HOLDS_MESSAGES
     */
    int getType();
    
    /**
     * Returns a list of child folder delegates matching the specified pattern.
     * @param pattern
     * @return
     */
    FolderDelegate[] list(String pattern);
    
    /**
     * Returns the folder separator for this delegate type.
     * @return
     */
    char getSeparator();
    
    /**
     * Initialise the delegate based on the specified folder type.
     * @param type
     * @return
     * @throws MessagingException
     */
    boolean create(final int type) throws MessagingException;
    
    /**
     * Returns a child folder delegate with the specified name.
     * @param name
     * @return
     */
    FolderDelegate getFolder(String name) throws MessagingException;
    
    /**
     * Delete the folder delegate.
     * @return
     */
    boolean delete();
    
    /**
     * Rename the folder delegate.
     * @param name
     * @return
     */
    boolean renameTo(String name);
    
    /**
     * Open the folder delegate.
     * @param mode
     */
    void open(final int mode);
    
    /**
     * Close the folder delegate.
     * @throws MessagingException
     */
    void close() throws MessagingException;
    
    /**
     * Returns the number of messages in the folder delegate.
     * @return
     * @throws MessagingException
     */
    int getMessageCount() throws MessagingException;
    
    /**
     * Returns an input stream from which to read the specified message.
     * @param index
     * @return
     * @throws IOException
     */
    InputStream getMessageAsStream(int index) throws IOException;
    
    /**
     * Append the specified messages to this delegate.
     * @param messages
     * @throws MessagingException
     */
    void appendMessages(Message[] messages) throws MessagingException;
    
    /**
     * Permanently delete the specified messages from this delegate.
     * @param deleted
     * @return
     * @throws MessagingException
     */
    void expunge(Message[] deleted) throws MessagingException;

    /**
     * Returns message delegate corresponding to the specified message id. If no delegate exists a
     * new Message delegate is created.
     * 
     * @param messageId
     * @return
     */
    MessageDelegate getMessage(int messageNumber) throws DelegateException;

    /**
     * Retrieves the last allocated message UID for the folder.
     * 
     * @return
     */
    long getLastUid() throws UnsupportedOperationException;

    /**
     * Allocates a new message UID for the folder.
     * 
     * @return
     */
    long allocateUid(MessageDelegate message)
        throws UnsupportedOperationException, DelegateException;

    /**
     * Returns the UID validity associated with the metadata. If no UID validity exist a new value
     * is initialised.
     * 
     * @return a long representation of the UID validity
     */
    long getUidValidity() throws UnsupportedOperationException,
        MessagingException;
    
    /**
     * Returns the last modification timestamp of this folder.
     * @return
     * @throws UnsupportedOperationException
     */
    long getLastModified() throws UnsupportedOperationException;
}
