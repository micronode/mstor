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
 * You should have Sent a copy of the GNU General Public License
 * along with Base Modules.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fortuna.mstor.search

import java.util.Map;

import javax.mail.Flags;
import javax.mail.search.FlagTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SizeTerm;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

/**
 * @author Ben
 *
 */
class FlagTermFactory extends AbstractFactory {

    boolean set
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
            throws InstantiationException, IllegalAccessException {

        FlagTerm result
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, FlagTerm)) {
            result = value
        }
        else {
            Flags flags = attributes.remove('flags')
            result = new FlagTerm(flags, set)
        }
        return result;
    }
}
