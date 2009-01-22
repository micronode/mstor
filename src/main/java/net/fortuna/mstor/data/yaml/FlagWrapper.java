/*
 * $Id$
 *
 * Created on 10/05/2008
 *
 * Copyright (c) 2008, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.data.yaml;

import javax.mail.Flags.Flag;

import org.ho.yaml.wrapper.DelayedCreationBeanWrapper;

/**
 * @author Ben
 *
 */
public class FlagWrapper extends DelayedCreationBeanWrapper {

    /**
     * @param type
     */
    public FlagWrapper(Class<?> type) {
        super(type);
    }

    /* (non-Javadoc)
     * @see org.ho.yaml.wrapper.DelayedCreationBeanWrapper#getPropertyNames()
     */
    public String[] getPropertyNames() {
        return new String[] {"flag"};
    }

    public Object getProperty(Object obj, String name) {
        if ("flag".equals(name)) {
            if (Flag.ANSWERED.equals(obj)) {
                return "ANSWERED";
            }
            else if (Flag.DELETED.equals(obj)) {
                return "DELETED";
            }
            else if (Flag.DRAFT.equals(obj)) {
                return "DRAFT";
            }
            else if (Flag.FLAGGED.equals(obj)) {
                return "FLAGGED";
            }
            else if (Flag.RECENT.equals(obj)) {
                return "RECENT";
            }
            else if (Flag.SEEN.equals(obj)) {
                return "SEEN";
            }
            else if (Flag.USER.equals(obj)) {
                return "USER";
            }
        }
        return super.getProperty(obj, name);
    }
    
    protected Object createObject() {
        if ("ANSWERED".equals(values.get("flag"))) {
            return Flag.ANSWERED;
        }
        if ("DELETED".equals(values.get("flag"))) {
            return Flag.DELETED;
        }
        if ("DRAFT".equals(values.get("flag"))) {
            return Flag.DRAFT;
        }
        if ("FLAGGED".equals(values.get("flag"))) {
            return Flag.FLAGGED;
        }
        if ("RECENT".equals(values.get("flag"))) {
            return Flag.RECENT;
        }
        else if ("SEEN".equals(values.get("flag"))) {
            return Flag.SEEN;
        }
        else if ("USER".equals(values.get("flag"))) {
            return Flag.USER;
        }
        return createPrototype();
    }
    
    public Object createPrototype() {
        return Flag.USER;
    }
}
