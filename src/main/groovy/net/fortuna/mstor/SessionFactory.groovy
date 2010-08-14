package net.fortuna.mstor

import java.util.Map;

import javax.mail.Session;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

class SessionFactory extends AbstractFactory {

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {

        Properties properties = value
        Session session = Session.getInstance(properties)
        return session
    }
}
