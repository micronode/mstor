package net.fortuna.mstor

import groovy.util.FactoryBuilderSupport;

class MessageBuilder extends FactoryBuilderSupport {
    
    MessageBuilder(boolean init = true) {
        super(init)
    }

    void registerFactories() {
        registerFactory 'address', new InternetAddressFactory()
    }
}
