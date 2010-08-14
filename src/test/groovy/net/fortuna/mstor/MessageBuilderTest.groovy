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
package net.fortuna.mstor;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

import static org.junit.Assert.*;

class MessageBuilderTest {

    @Test
    void testBuildAddress() {
        InternetAddress address = new MessageBuilder().from('test@example.com')
        assert address.address == 'test@example.com'
        
        address = new MessageBuilder().from('test@example.com', personal: 'Test')
        assert address.address == 'test@example.com'
        assert address.personal == 'Test'
        
        address = new MessageBuilder().from(personal: 'Test', address: 'test@example.com')
        assert address.address == 'test@example.com'
        assert address.personal == 'Test'
    }
    
    @Test
    void testBuildSession() {
        def props = new Properties()
        props['mail.smtp.host'] = 'localhost'
        props['mail.smtp.port'] = '225'
        Session session = new MessageBuilder().session(props)
        
        assert session.getProperty('mail.smtp.host') == 'localhost'
    }
}
