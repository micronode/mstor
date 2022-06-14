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

import jakarta.mail.Flags;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 22/01/2009
 * </pre>
 * 
 *
 */
public final class MessageUtils {

    /**
     * Constructor made private to enforce static nature.
     */
    private MessageUtils() {
    }

    /**
     * @param flagName
     * @return
     */
    public static Flag getFlag(String flagName) {
        if ("answered".equals(flagName)) {
            return Flags.Flag.ANSWERED;
        }
        else if ("deleted".equalsIgnoreCase(flagName)) {
            return Flags.Flag.DELETED;
        }
        else if ("draft".equalsIgnoreCase(flagName)) {
            return Flags.Flag.DRAFT;
        }
        else if ("flagged".equalsIgnoreCase(flagName)) {
            return Flags.Flag.FLAGGED;
        }
        else if ("recent".equalsIgnoreCase(flagName)) {
            return Flags.Flag.RECENT;
        }
        else if ("seen".equalsIgnoreCase(flagName)) {
            return Flags.Flag.SEEN;
        }
        else if ("user".equalsIgnoreCase(flagName)) {
            return Flags.Flag.USER;
        }
        return null;
    }

    /**
     * @param flag
     * @return
     */
    public static String getFlagName(Flag flag) {
        if (Flags.Flag.ANSWERED.equals(flag)) {
            return "answered";
        }
        else if (Flags.Flag.DELETED.equals(flag)) {
            return "deleted";
        }
        else if (Flags.Flag.DRAFT.equals(flag)) {
            return "draft";
        }
        else if (Flags.Flag.FLAGGED.equals(flag)) {
            return "flagged";
        }
        else if (Flags.Flag.RECENT.equals(flag)) {
            return "recent";
        }
        else if (Flags.Flag.SEEN.equals(flag)) {
            return "seen";
        }
        else if (Flags.Flag.USER.equals(flag)) {
            return "user";
        }
        return null;
    }

    /**
     * Returns an appropriate message identifier for the specified message.
     * @param message
     * @return
     * @throws MessagingException
     */
    public static String getMessageId(Message message) throws MessagingException {
        String messageId = null;
        if (message instanceof MimeMessage) {
            MimeMessage mimeMessage = (MimeMessage) message;
            messageId = mimeMessage.getMessageID();
        }
        
        if (messageId == null) {
            String[] uids = message.getHeader("X-UIDL");
            if (uids != null && uids.length > 0) {
                messageId = uids[0];
            }
        }
        return messageId;
    }
}
