package net.fortuna.mstor


import java.util.Map;

import javax.mail.internet.InternetAddress;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

class InternetAddressFactory extends AbstractFactory {

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {

        InternetAddress address = new InternetAddress();                
        if (FactoryBuilderSupport.checkValueIsType(value, name, String)) {
            address.address = value
        }
        else {
            address.address = attributes['address']
        }
        String charset = attributes['charset']
        if (charset) {
            address.setPersonal attributes['personal'], charset
        }
        else {
            address.personal = attributes['personal']
        }
        return address;
    }

}
