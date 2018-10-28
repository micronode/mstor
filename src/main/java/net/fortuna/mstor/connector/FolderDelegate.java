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
package net.fortuna.mstor.connector;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementors support delegation of specific functions from Javamail {@link Folder} implementations.
 * 
 * @param <T> the type of message delegate supported
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
public interface FolderDelegate<T extends MessageDelegate> {

    /**
     * @return Returns the name.
     */
    String getFolderName();
    
    /**
     * @return the full name of the folder.
     */
    String getFullName();

    /**
     * @return the parent folder delegate of this delegate.
     */
    FolderDelegate<T> getParent();
    
    /**
     * Indicates whether the folder represented by this delegate exists.
     * @return true if the folder exists, otherwise false
     */
    boolean exists();
    
    /**
     * @return the type of folder this delegate represents.
     * @see javax.mail.Folder#HOLDS_FOLDERS
     * @see javax.mail.Folder#HOLDS_MESSAGES
     */
    int getType();
    
    /**
     * Returns a list of child folder delegates matching the specified pattern.
     * @param pattern a pattern to match folders against
     * @return a list of folder delegates
     */
    FolderDelegate<T>[] list(String pattern);
    
    /**
     * @return the folder separator for this delegate type.
     */
    char getSeparator();
    
    /**
     * Initialise the delegate based on the specified folder type.
     * @param type a folder type
     * @return true if the delegate is created, otherwise false
     * @throws MessagingException where an unexpected error occurs creating the delegate
     */
    boolean create(final int type) throws MessagingException;
    
    /**
     * Returns a child folder delegate with the specified name.
     * @param name a folder name
     * @return the folder delegate with the specified name, or null if the folder doesn't exist
     * @throws MessagingException where an unexpected error occurs retrieving the folder
     */
    FolderDelegate<T> getFolder(String name) throws MessagingException;
    
    /**
     * Delete the folder delegate.
     * @return true if the delegate is deleted, otherwise false
     */
    boolean delete();
    
    /**
     * Rename the folder delegate.
     * @param name the new folder name
     * @return true if the folder is renamed, otherwise false
     */
    boolean renameTo(String name);
    
    /**
     * Open the folder delegate.
     * @param mode the mode to open the folder
     * @see javax.mail.Folder#READ_ONLY
     * @see javax.mail.Folder#READ_WRITE
     */
    void open(final int mode);
    
    /**
     * Close the folder delegate.
     * @throws MessagingException where an unexpected error occurs closing the folder
     */
    void close() throws MessagingException;
    
    /**
     * Returns the number of messages in the folder delegate.
     * @return the total message count
     * @throws MessagingException where an unexpected error occurs retrieving the message count
     */
    int getMessageCount() throws MessagingException;
    
    /**
     * Optional support for more efficient implementation.
     * @return the total deleted message count
     * @throws MessagingException where an unexpected error occurs
     * @throws UnsupportedOperationException if this method is not supported by the folder implementation
     */
    int getDeletedMessageCount() throws MessagingException, UnsupportedOperationException;
    
    /**
     * Returns an input stream from which to read the specified message.
     * @param index the index of the message to return
     * @return an input stream for the specified message
     * @throws IOException where an error occurs
     */
    InputStream getMessageAsStream(int index) throws IOException;
    
    /**
     * Append the specified messages to this delegate.
     * @param messages an array of messages to append to the folder
     * @throws MessagingException where an error occurs appending the messages
     */
    void appendMessages(Message[] messages) throws MessagingException;
    
    /**
     * Permanently delete the specified messages from this delegate.
     * @param deleted an array of deleted messages to expunge
     * @throws MessagingException where an error occurs expunging the messages
     */
    void expunge(Message[] deleted) throws MessagingException;

    /**
     * Returns message delegate corresponding to the specified message id. If no delegate exists a
     * new Message delegate is created.
     * 
     * @param messageNumber the message number of the message to retrieve
     * @return the message with the specified message number, or null if the messages doesn't exist
     * @throws DelegateException where an error occurs retrieving the message
     */
    T getMessage(int messageNumber) throws DelegateException;

    /**
     * Retrieves the last allocated message UID for the folder.
     * 
     * @return the latest UID for the folder
     * @throws UnsupportedOperationException if this method is not supported by the folder implementation
     */
    long getLastUid() throws UnsupportedOperationException;

    /**
     * Allocates a new message UID for the folder.
     * 
     * @param message the message to allocate a UID value to
     * @return the allocated UID for the specified message
     * @throws DelegateException where an error occurs allocating a UID
     * @throws UnsupportedOperationException if this method is not supported by the folder implementation
     */
    long allocateUid(MessageDelegate message) throws UnsupportedOperationException, DelegateException;

    /**
     * Returns the UID validity associated with the metadata. If no UID validity exist a new value
     * is initialised.
     * 
     * @return a long representation of the UID validity
     * @throws UnsupportedOperationException if this method is not supported by the folder implementation
     * @throws MessagingException if an error occurs retrieving the validity value
     */
    long getUidValidity() throws UnsupportedOperationException, MessagingException;
    
    /**
     * Returns the last modification timestamp of this folder.
     * @return a timestamp as a long value
     * @throws UnsupportedOperationException if this method is not supported by the folder implementation
     */
    long getLastModified() throws UnsupportedOperationException;
}
