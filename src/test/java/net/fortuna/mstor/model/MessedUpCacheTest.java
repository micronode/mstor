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
package net.fortuna.mstor.model;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

/**
 * @author Ben
 *
 */
public class MessedUpCacheTest extends AbstractMStorTest {
    
    private static Random random;

    /**
     * @param method
     * @param lifecycle
     * @param username
     * @param password
     */
    public MessedUpCacheTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method, lifecycle, username, password);
    }
    
    /**
     * @return
     * @throws MessagingException
     */
    public static MimeMessage generateMessage() throws MessagingException {
        MimeMessage mm = new MimeMessage((Session) null);
        int r = getRandom().nextInt() % 100000;
        int r2 = getRandom().nextInt() % 100000;
        mm.setSubject("good news" + r);
        mm.setFrom(new InternetAddress("user" + r + "@localhost"));
        mm.setRecipients(Message.RecipientType.TO,
                new InternetAddress[] {new InternetAddress("user" + r2
                        + "@localhost")});
        String text = "Hello User" + r2
                + "!\n\nhave a nice holiday.\r\n\r\ngreetings,\nUser" + r
                + "\n";
        mm.setText(text);
        return mm;
    }
    
    /**
     * @return
     */
    protected static synchronized Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;

    }
    
    /**
     * @throws MessagingException
     */
    public void testMessedUpCache() throws MessagingException {
        Folder inbox = store.getFolder("INBOX");
        if (!inbox.exists()) {
            inbox.create(Folder.HOLDS_MESSAGES);
        }
        inbox.open(Folder.READ_WRITE);
        inbox.appendMessages(new MimeMessage[] {generateMessage(), generateMessage(), generateMessage()});
        inbox.getMessage(1).setFlag(Flags.Flag.DELETED, true);
        assertEquals(3, inbox.getMessageCount());
        inbox.close(true);
        inbox.open(Folder.READ_WRITE);
        // this is correct
        assertEquals(2, inbox.getMessageCount());
        inbox.close(true);
        inbox.open(Folder.READ_WRITE);
        // there should still be 2, nothing has changed
        assertEquals(2, inbox.getMessageCount());
        inbox.close(true);
    }
}
