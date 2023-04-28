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
package org.mstor.provider.mail;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.URLName;

/**
 * Implementors provide protocol-specific storage support.
 * 
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
public interface ProtocolConnector {

    /**
     * @return true if connection succeeded, otherwise false
     * @throws AuthenticationFailedException where connection authentication fails
     * @throws MessagingException where an error occurs connecting
     */
    boolean connect() throws AuthenticationFailedException, MessagingException;
    
    /**
     * @throws MessagingException where an error occurs disconnecting
     */
    void disconnect() throws MessagingException;
    
    /**
     * @return the default folder for the connector implementation
     * @throws MessagingException where an error occurs retrieving the default folder
     */
    Folder getDefaultFolder() throws MessagingException;
    
    /**
     * @param name the name of the folder to retrieve
     * @return the folder with the specified name, or null if such a folder doesn't exist
     * @throws MessagingException where an error occurs retrieving the folder
     */
    Folder getFolder(String name) throws MessagingException;
    
    /**
     * @param url a folder URL
     * @return the folder at the specified URL, or null if such a folder doesn't exist
     * @throws MessagingException where an error occurs retrieving the folder
     */
    Folder getFolder(URLName url) throws MessagingException;

    /**
     * Indicate the supported protocol string(s).
     * @param protocol
     * @return
     */
    boolean isProtocolSupported(String protocol);
}
