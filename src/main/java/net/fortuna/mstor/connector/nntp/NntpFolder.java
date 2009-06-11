/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.mstor.connector.nntp;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;

import net.fortuna.mstor.connector.AbstractFolderDelegate;
import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;

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

    private NewsgroupInfo newsgroupInfo;
    
    private NNTPClient client;
    
    /**
     * 
     */
    public NntpFolder(NewsgroupInfo newsgroupInfo, NNTPClient client) {
        this.newsgroupInfo = newsgroupInfo;
        this.client = client;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.AbstractFolderDelegate#createMessage(int)
     */
    protected MessageDelegate createMessage(int messageNumber)
            throws DelegateException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.AbstractFolderDelegate#setLastUid(long)
     */
    protected void setLastUid(long uid) throws UnsupportedOperationException,
            DelegateException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#appendMessages(javax.mail.Message[])
     */
    public void appendMessages(Message[] messages) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#close()
     */
    public void close() throws MessagingException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#create(int)
     */
    public boolean create(int type) throws MessagingException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#delete()
     */
    public boolean delete() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#exists()
     */
    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#expunge(javax.mail.Message[])
     */
    public void expunge(Message[] deleted) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate<MessageDelegate> getFolder(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getFullName()
     */
    public String getFullName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getLastModified()
     */
    public long getLastModified() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getLastUid()
     */
    public long getLastUid() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getMessage(int)
     */
    public MessageDelegate getMessage(int messageNumber)
            throws DelegateException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getMessageAsStream(int)
     */
    public InputStream getMessageAsStream(int index) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getMessageCount()
     */
    public int getMessageCount() throws MessagingException {
        return newsgroupInfo.getArticleCount();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getFolderName()
     */
    public String getFolderName() {
        return newsgroupInfo.getNewsgroup();
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getParent()
     */
    public FolderDelegate<MessageDelegate> getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getSeparator()
     */
    public char getSeparator() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getType()
     */
    public int getType() {
        return Folder.HOLDS_MESSAGES;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#getUidValidity()
     */
    public long getUidValidity() throws UnsupportedOperationException,
            MessagingException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate<MessageDelegate>[] list(String pattern) {
        // TODO Auto-generated method stub
        return new FolderDelegate[0];
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#open(int)
     */
    public void open(int mode) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#renameTo(java.lang.String)
     */
    public boolean renameTo(String name) {
        // TODO Auto-generated method stub
        return false;
    }

}
