/*
 * $Id$
 *
 * Created on 21/07/2007
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
package net.fortuna.mstor.connector.jcr;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetHeaders;

import net.fortuna.mstor.connector.AbstractMessageDelegate;
import net.fortuna.mstor.connector.DelegateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 */
public class RepositoryMessage extends AbstractMessageDelegate {

    /**
     * 
     */
    private static final long serialVersionUID = -3173593874940508265L;
    
    private Log log = LogFactory.getLog(RepositoryMessage.class);
    
    private Node node;

    /**
     * @param node
     */
    public RepositoryMessage(Node node) {
//        super(folder);
        this.node = node;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getFlags()
     */
    public Flags getFlags() {
        Flags flags = new Flags();
        try {
            NodeIterator flagIterator = node.getNodes(NodeType.FLAG.toString());
            while (flagIterator.hasNext()) {
                Node flagNode = flagIterator.nextNode();
                Flag flag = getFlag(flagNode.getProperty(NodeProperty.NAME.toString()).getString());
                if (flag != null) {
                    flags.add(flag);
                }
                else {
                    // user flag..
                    flags.add(flagNode.getProperty(NodeProperty.NAME.toString()).getString());
                }
            }
        }
        catch (RepositoryException re) {
            log.error("Error retrieving flags", re);
        }
        return flags;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getForwarded()
     */
    public Date getForwarded() {
        try {
            return node.getProperty(NodeProperty.FOWARDED.toString()).getDate().getTime();
        }
        catch (PathNotFoundException pnfe) {
            log.info("Forwarded date not set", pnfe);
        }
        catch (RepositoryException re) {
            log.error("Error retrieving forwarded date", re);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getHeaders()
     */
    public InternetHeaders getHeaders() {
        InternetHeaders headers = new InternetHeaders();
        try {
            NodeIterator headerIterator = node.getNodes(NodeType.HEADER.toString());
            while (headerIterator.hasNext()) {
                Node headerNode = headerIterator.nextNode();
                headers.addHeader(headerNode.getProperty(NodeProperty.NAME.toString()).getString(),
                        headerNode.getProperty(NodeProperty.VALUE.toString()).getString());
            }
        }
        catch (RepositoryException re) {
            log.error("Error retrieving headers", re);
        }
        return headers;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getMessageNumber()
     */
    public int getMessageNumber() {
        try {
            return (int) node.getProperty(NodeProperty.MESSAGE_NUMBER.toString()).getLong();
        }
        catch (RepositoryException re) {
            log.error("Error retrieving message number", re);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getReceived()
     */
    public Date getReceived() {
        try {
            return node.getProperty(NodeProperty.RECEIVED.toString()).getDate().getTime();
        }
        catch (PathNotFoundException pnfe) {
            log.info("Received date not set", pnfe);
        }
        catch (RepositoryException re) {
            log.error("Error retrieving received date", re);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getReplied()
     */
    public Date getReplied() {
        try {
            return node.getProperty(NodeProperty.REPLIED.toString()).getDate().getTime();
        }
        catch (PathNotFoundException pnfe) {
            log.info("Replied date not set", pnfe);
        }
        catch (RepositoryException re) {
            log.error("Error retrieving replied date", re);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#getUid()
     */
    public long getUid() {
        try {
            return node.getProperty(NodeProperty.UID.toString()).getLong();
        }
        catch (PathNotFoundException pnfe) {
            log.info("UID not set", pnfe);
        }
        catch (RepositoryException re) {
            log.error("Error retrieving UID", re);
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#isExpunged()
     */
    public boolean isExpunged() {
        try {
            return node.getProperty(NodeProperty.EXPUNGED.toString()).getBoolean();
        }
        catch (PathNotFoundException pnfe) {
            log.info("Expunged flag not set", pnfe);
        }
        catch (RepositoryException re) {
            log.error("Error retrieving expunged flag", re);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setExpunged(boolean)
     */
    public void setExpunged(boolean expunged) {
        try {
            node.setProperty(NodeProperty.EXPUNGED.toString(), expunged);
        }
        catch (RepositoryException re) {
            log.error("Error setting expunged flag", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setFlags(javax.mail.Flags)
     */
    public void setFlags(Flags flags) {
        try {
            for (int i = 0; i < flags.getSystemFlags().length; i++) {
                String flagName = getFlagName(flags.getSystemFlags()[i]);
                if (flagName != null) {
                    Node flagNode = node.addNode(NodeType.FLAG.toString());
                    flagNode.setProperty(NodeProperty.NAME.toString(), flagName);
                }
            }
            for (int i = 0; i < flags.getUserFlags().length; i++) {
                Node flagNode = node.addNode(NodeType.FLAG.toString());
                flagNode.setProperty(NodeProperty.NAME.toString(), flags.getUserFlags()[i]);
            }
        }
        catch (RepositoryException re) {
            log.error("Error setting flags", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setForwarded(java.util.Date)
     */
    public void setForwarded(Date forwarded) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(forwarded);
            node.setProperty(NodeProperty.FOWARDED.toString(), cal);
        }
        catch (RepositoryException re) {
            log.error("Error setting forwarded date", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setHeaders(java.util.Enumeration)
     */
    public void setHeaders(Enumeration headers) {
        try {
            while (headers.hasMoreElements()) {
                Header header = (Header) headers.nextElement();
                Node headerNode = node.addNode(NodeType.HEADER.toString());
                headerNode.setProperty(NodeProperty.NAME.toString(), header.getName());
                headerNode.setProperty(NodeProperty.VALUE.toString(), header.getValue());
            }
        }
        catch (RepositoryException re) {
            log.error("Error setting headers", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setReceived(java.util.Date)
     */
    public void setReceived(Date received) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(received);
            node.setProperty(NodeProperty.RECEIVED.toString(), cal);
        }
        catch (RepositoryException re) {
            log.error("Error setting received date", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setReplied(java.util.Date)
     */
    public void setReplied(Date replied) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(replied);
            node.setProperty(NodeProperty.REPLIED.toString(), cal);
        }
        catch (RepositoryException re) {
            log.error("Error setting replied date", re);
        }
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.MetaMessage#setUid(long)
     */
    public void setUid(long uid) {
        try {
            node.setProperty(NodeProperty.UID.toString(), uid);
        }
        catch (RepositoryException re) {
            log.error("Error setting UID", re);
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.mstor.MessageDelegate#saveChanges()
     */
    public void saveChanges() throws DelegateException {
        try {
            node.getSession().save();
        }
        catch (RepositoryException re) {
            throw new DelegateException("Error saving changes", re);
        }
    }
}
