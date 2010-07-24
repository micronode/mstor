package net.fortuna.mstor.search;
import javax.mail.Address;


import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.search.FromTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.internet.InternetAddress;

import net.fortuna.mstor.search.AndTermFactory.AndTermEx;

import org.junit.Test;

import static org.junit.Assert.*;

class SearchTermBuilderTest {

    @Test
    void testBuildFromTerm() {
        def addressString = 'test@example.com'
        def address = new InternetAddress(addressString)
        FromTerm term = new SearchTermBuilder().from(address)
        assert term.address == address
        
        term = new SearchTermBuilder().from(addressString)
        assert term.address == address

//        Message message = [from: address] as Message
//        assert term.match(message)
    }

    @Test    
    void testBuildToTerm() {
        def address = new InternetAddress('test@example.com')
        RecipientTerm term = new SearchTermBuilder().to(address)
        assert term.recipientType == RecipientType.TO
    }

    @Test    
    void testBuildCcTerm() {
        def address = new InternetAddress('test@example.com')
        RecipientTerm term = new SearchTermBuilder().cc(address)
        assert term.recipientType == RecipientType.CC
    }

    @Test    
    void testBuildBccTerm() {
        def address = new InternetAddress('test@example.com')
        RecipientTerm term = new SearchTermBuilder().bcc(address)
        assert term.recipientType == RecipientType.BCC
    }
    
    @Test
    void testBuildAndTerm() {
        def address = new InternetAddress('test@example.com')
        AndTermEx term = new SearchTermBuilder().and() {
            to(address)
            cc(address)
        }
    }
}
