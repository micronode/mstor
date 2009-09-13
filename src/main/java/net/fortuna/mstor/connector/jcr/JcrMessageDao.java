/*
 *
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

import java.util.List;

import javax.jcr.Session;
import javax.mail.Flags.Flag;

import net.fortuna.mstor.util.MessageUtils;

import org.jcrom.Jcrom;
import org.jcrom.dao.AbstractJcrDAO;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 18/02/2009
 * </pre>
 * 
 *
 */
public class JcrMessageDao extends AbstractJcrDAO<JcrMessage> {

    /**
     * @param session
     * @param jcrom
     */
    public JcrMessageDao(Session session, Jcrom jcrom) {
        super(JcrMessage.class, session, jcrom, new String[] {"mix:versionable"});
    }

    /**
     * @param path
     * @param messageNumber
     * @return
     */
    public List<JcrMessage> findByMessageNumber(String path, int messageNumber) {
        return updateDao(super.findByXPath("/jcr:root" + path + "/*[@messageNumber=" + messageNumber + "]", "*", -1));
    }

    /**
     * @param path
     * @param messageId
     * @return
     */
    public List<JcrMessage> findByMessageId(String path, String messageId) {
        /*
        List<JcrMessage> messages = findByHeader(path, "Message-ID", messageId);
        if (messages.isEmpty()) {
            // try fallback message id..
            messages = findByHeader(path, "Message-Id", messageId);
        }
        if (messages.isEmpty()) {
            // try fallback message id..
            messages = findByHeader(path, "X-UIDL", messageId);
        }
        return messages;
        */
        return updateDao(super.findByXPath("/jcr:root" + path + "/*[@messageId='" + messageId + "']", "*", -1));
    }
    
    /**
     * @param path
     * @param name
     * @param value
     * @return
     */
    public List<JcrMessage> findByHeader(String path, String name, String value) {
        // parent axis not supported in jackrabbit.. hopefully will be soon.
        return updateDao(super.findByXPath("/jcr:root" + path + "/*/headers[@" + name + "='" + value + "']/..", "*", -1));
    }

    /**
     * @param path
     * @param flag
     * @return
     */
    public List<JcrMessage> findByFlag(String path, Flag flag) {
        return updateDao(super.findByXPath("/jcr:root" + path + "/*[jcr:like(@flags, '" + MessageUtils.getFlagName(flag) + "')]", "*", -1));
    }

    private List<JcrMessage> updateDao(List<JcrMessage> messages) {
        // store reference to DAO for saveChanges() support..
        for (JcrMessage message : messages) {
            message.setMessageDao(this);
        }
        return messages;
    }
}
