package net.fortuna.mstor

import javax.mail.Message.RecipientType;

import groovy.util.FactoryBuilderSupport;

class MessageBuilder extends FactoryBuilderSupport {
    
    MessageBuilder(boolean init = true) {
        super(init)
    }

    void registerFactories() {
        registerFactory 'to', new InternetAddressFactory(type: RecipientType.TO)
        registerFactory 'cc', new InternetAddressFactory(type: RecipientType.CC)
        registerFactory 'bcc', new InternetAddressFactory(type: RecipientType.BCC)
        registerFactory 'from', new InternetAddressFactory()
        
        registerFactory 'session', new SessionFactory()
        
    }
}
