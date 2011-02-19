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
import javax.mail.Address;


import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.search.FromTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

import static org.junit.Assert.*;

class SearchTermBuilderTest {

    def addressString = 'test@example.com'
    def address = new InternetAddress(addressString)

    @Test
    void testBuildFromTerm() {
        FromTerm term = new SearchTermBuilder().from(address)
        assert term.address == address
        
        term = new SearchTermBuilder().from(addressString)
        assert term.address == address

        Message message = { [address] as Address[] } as Message
        assert term.match(message)
    }

    @Test    
    void testBuildToTerm() {
        RecipientTerm term = new SearchTermBuilder().to(address)
        assert term.recipientType == RecipientType.TO
        assert term.address == address
        
        term = new SearchTermBuilder().to(addressString)
        assert term.address == address
    }

    @Test    
    void testBuildCcTerm() {
        RecipientTerm term = new SearchTermBuilder().cc(address)
        assert term.recipientType == RecipientType.CC
        assert term.address == address
        
        term = new SearchTermBuilder().cc(addressString)
        assert term.address == address
    }

    @Test    
    void testBuildBccTerm() {
        RecipientTerm term = new SearchTermBuilder().bcc(address)
        assert term.recipientType == RecipientType.BCC
        assert term.address == address
        
        term = new SearchTermBuilder().bcc(addressString)
        assert term.address == address
    }
    
    @Test
    void testBuildAndTerm() {
        def term = new SearchTermBuilder().and() {
            from(address)
            subjectContains('test')
        }
        Message message = [getFrom:{ [address] as Address[] }, getSubject:{ 'This is a test' }] as Message
        assert term.match(message)
        
        message = [getFrom: {[address] as Address[]}, getSubject: {'This is a tset'}] as Message
        assert !term.match(message)
    }
}
