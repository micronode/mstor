/**
 * This file is part of Base Modules.
 *
 * Copyright (c) 2010, Ben Fortuna [fortuna@micronode.com]
 *
 * Base Modules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Base Modules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Base Modules.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fortuna.mstor.search

import java.util.Map;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.RecipientTerm;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

/**
 * @author Ben
 *
 */
class RecipientStringTermFactory extends AbstractFactory {

    RecipientType type
    
    /**
     * {@inheritDoc}
     */
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {

        RecipientStringTerm result
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, RecipientStringTerm)) {
            result = value
        }
        else {
            String pattern = attributes.remove('pattern')
            result = new RecipientStringTerm(type, pattern)
        }
        return result;
    }

}
