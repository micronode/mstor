/*
 * $Id$
 * 
 * Created: [11/07/2004]
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
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

import javax.mail.Message;

/**
 * Defines a set of metadata for an mstor folder.
 * @author benfortuna
 */
public interface MetaFolder {

    /**
     * @return Returns the name.
     */
    String getName();
    
    /**
     * @param name The name to set.
     */
    void setName(String name);

    /**
     * Returns message metadata corresponding to the specified
     * message id. If no metadata exists a new MetaMessage is
     * created.
     * @param messageId
     * @return
     */
    MetaMessage getMessage(Message message);
    
    /**
     * Adds the specified message metadata.
     * @param message
     */
    void addMessage(MetaMessage message);

    /**
     * Removes the message metadata corresponding to the
     * specified message number.
     * @param messageNumber the number of the message metadata to remove
     * @return the removed message metadata
     */
    MetaMessage removeMessage(int messageNumber);

    /**
     * Removes the message metadata corresponding to the
     * specified message numbers.
     * @param messageNumbers an array of numbers of the message metadata to remove
     * @return an array of the removed message metadata
     */
    MetaMessage[] removeMessages(int[] messageNumber);
    
    /**
     * Saves the meta data to the filesystem.
     */
    void save() throws IOException;
    
    /**
     * Retrieves the last allocated message UID for the folder.
     * @return
     */
    long getLastUid();
    
    /**
     * Allocates a new message UID for the folder.
     * @return
     */
    long allocateUid(MetaMessage message) throws IOException;
    
    /**
     * Returns the UID validity associated with the metadata. If no UID validity
     * exist a new value is initialised.
     * @return a long representation of the UID validity
     */
    long getUidValidity() throws IOException;
}
