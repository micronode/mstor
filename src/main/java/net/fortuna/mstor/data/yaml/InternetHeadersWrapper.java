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
package net.fortuna.mstor.data.yaml;

import org.ho.yaml.wrapper.DelayedCreationBeanWrapper;

import javax.mail.Header;
import javax.mail.internet.InternetHeaders;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
public class InternetHeadersWrapper extends DelayedCreationBeanWrapper {

    /**
     * @param type
     */
    public InternetHeadersWrapper(Class type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPropertyNames() {
        return new String[] {"headers"};
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(Object obj, String name) {
        if ("headers".equals(name)) {
            Enumeration<Header> headersEnum = ((InternetHeaders) obj).getAllHeaders();
            List<Header> headers = new ArrayList<>();
            while (headersEnum.hasMoreElements()) {
                headers.add(headersEnum.nextElement());
            }
            return headers;
        }
        return super.getProperty(obj, name);
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object createObject() {
        InternetHeaders internetHeaders = new InternetHeaders();
        for (Header header : ((List<Header>) values.get("headers"))) {
            internetHeaders.setHeader(header.getName(), header.getValue());
        }
        return internetHeaders;
    }
}
