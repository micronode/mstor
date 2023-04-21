/**
 * Copyright (c) 2011, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mstor.data.yaml;

import jakarta.mail.Flags;
import jakarta.mail.Flags.Flag;
import org.ho.yaml.wrapper.DelayedCreationBeanWrapper;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 10/05/2008
 * </pre>
 * 
 *
 */
public class FlagsWrapper extends DelayedCreationBeanWrapper {

    /**
     * @param type
     */
    public FlagsWrapper(Class<?> type) {
        super(type);
    }

    /* (non-Javadoc)
     * @see org.ho.yaml.wrapper.DelayedCreationBeanWrapper#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[] {"systemFlags", "userFlags"};
    }

    protected Object createObject() {
        Flags flags = (Flags) createPrototype();
        Flag[] systemFlags = (Flag[]) values.get("systemFlags");
        for (Flag systemFlag : systemFlags) {
            flags.add(systemFlag);
        }
        String[] userFlags = (String[]) values.get("userFlags");
        for (String userFlag : userFlags) {
            flags.add(userFlag);
        }
        return flags;
    }
    
    public Object createPrototype() {
        return new Flags();
    }
}
