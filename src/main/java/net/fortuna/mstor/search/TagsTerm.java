/*
 * $Id$
 *
 * Created on 14/05/2006
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.mstor.search;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

import net.fortuna.mstor.tag.Taggable;
import net.fortuna.mstor.tag.Tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Search Term that matches all messages with a given set of tags.
 * 
 * @author Ben Fortuna
 */
public class TagsTerm extends SearchTerm {

    private Log log = LogFactory.getLog(TagsTerm.class);

    private static final long serialVersionUID = 7893903141033644620L;

    private Tags tags;

    /**
     * Default constructor.
     */
    public TagsTerm() {
        this(new Tags());
    }

    /**
     * Creates a new term that matches messages that contain all the specified tags.
     * 
     * @param tags
     */
    public TagsTerm(final Tags tags) {
        this.tags = tags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.search.SearchTerm#match(javax.mail.Message)
     */
    public final boolean match(final Message m) {
        try {
            Taggable message = (Taggable) m;
            return message.getTags().containsAll(tags);
        }
        catch (ClassCastException cce) {
            log.error("Invalid message type", cce);
        }
        catch (MessagingException me) {
            log.error("Exception occured", me);
        }
        return false;
    }

    /**
     * @return Returns the tags.
     */
    public final Tags getTags() {
        return tags;
    }
}
