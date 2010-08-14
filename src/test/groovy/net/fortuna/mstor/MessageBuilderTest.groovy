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
