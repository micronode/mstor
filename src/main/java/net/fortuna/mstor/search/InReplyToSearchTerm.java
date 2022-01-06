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
package net.fortuna.mstor.search;

import net.fortuna.mstor.model.MStorMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 22/02/2009
 * </pre>
 * 
 *
 */
public class InReplyToSearchTerm extends SearchTerm {

    /**
     * 
     */
    private static final long serialVersionUID = -4186790883587837877L;

    private static final Log LOG = LogFactory.getLog(InReplyToSearchTerm.class);

    private MStorMessage message;
    
    /**
     * 
     */
    public InReplyToSearchTerm(MStorMessage message) {
        this.message = message;
    }

    /* (non-Javadoc)
     * @see javax.mail.search.SearchTerm#match(javax.mail.Message)
     */
    @Override
    public boolean match(Message msg) {
        if (msg instanceof MStorMessage) {
            try {
                return message.equals(((MStorMessage) msg).getInReplyTo());
            }
            catch (UnsupportedOperationException e) {
                LOG.error("References not supported", e);
            }
        }
        return false;
    }

}
