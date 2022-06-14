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
package org.mstor.mbox.connector.nntp;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;
import org.mstor.provider.mail.AbstractFolderDelegate;
import org.mstor.provider.mail.FolderDelegate;
import org.mstor.provider.mail.MessageDelegate;

import java.io.InputStream;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 10/08/2008
 * </pre>
 * 
 *
 */
public class NntpFolder extends AbstractFolderDelegate<MessageDelegate> {

    private final NewsgroupInfo newsgroupInfo;
    
    private final NNTPClient client;
    
    /**
     * 
     */
    public NntpFolder(NewsgroupInfo newsgroupInfo, NNTPClient client) {
        this.newsgroupInfo = newsgroupInfo;
        this.client = client;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.AbstractFolderDelegate#createMessage(int)
     */
    protected MessageDelegate createMessage(int messageNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.AbstractFolderDelegate#setLastUid(long)
     */
    protected void setLastUid(long uid) throws UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#appendMessages(jakarta.mail.Message[])
     */
    public void appendMessages(Message[] messages) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#create(int)
     */
    public boolean create(int type) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#delete()
     */
    public boolean delete() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#exists()
     */
    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#expunge(jakarta.mail.Message[])
     */
    public void expunge(Message[] deleted) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate<MessageDelegate> getFolder(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getFullName()
     */
    public String getFullName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getLastModified()
     */
    public long getLastModified() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getLastUid()
     */
    public long getLastUid() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getMessage(int)
     */
    public MessageDelegate getMessage(int messageNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getMessageAsStream(int)
     */
    public InputStream getMessageAsStream(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getMessageCount()
     */
    public int getMessageCount() {
        return newsgroupInfo.getArticleCount();
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getFolderName()
     */
    public String getFolderName() {
        return newsgroupInfo.getNewsgroup();
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getParent()
     */
    public FolderDelegate<MessageDelegate> getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getSeparator()
     */
    public char getSeparator() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getType()
     */
    public int getType() {
        return Folder.HOLDS_MESSAGES;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#getUidValidity()
     */
    public long getUidValidity() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate<MessageDelegate>[] list(String pattern) {
        // TODO Auto-generated method stub
        return new FolderDelegate[0];
    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#open(int)
     */
    public void open(int mode) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.mstor.mail.connector.FolderDelegate#renameTo(java.lang.String)
     */
    public boolean renameTo(String name) {
        // TODO Auto-generated method stub
        return false;
    }

}
