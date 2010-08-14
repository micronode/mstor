package net.fortuna.mstor

import java.util.Map;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

class MimeMessageFactory extends AbstractFactory {

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {
                
        MimeMessage message
        if (FactoryBuilderSupport.checkValueIsType(value, name, Session)) {
            Session session = value
            message = new MimeMessage(session)
        }
        return message;
    }

}
