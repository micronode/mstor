package net.fortuna.mstor;

import javax.mail.internet.InternetAddress;

import org.junit.Test;

import static org.junit.Assert.*;

class MessageBuilderTest {

    @Test
    void testBuildAddress() {
        InternetAddress address = new MessageBuilder().address('test@example.com')
        assert address.address == 'test@example.com'
        
        address = new MessageBuilder().address('test@example.com', personal: 'Test')
        assert address.address == 'test@example.com'
        assert address.personal == 'Test'
        
        address = new MessageBuilder().address(personal: 'Test', address: 'test@example.com')
        assert address.address == 'test@example.com'
        assert address.personal == 'Test'
    }
}
