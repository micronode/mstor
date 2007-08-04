/*
 * $Id$
 *
 * Created on 6/05/2006
 *
 * Copyright (c) 2005, Ben Fortuna
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
package net.fortuna.mstor.data.xml;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author Ben Fortuna
 */
public class ElementBinding extends XmlBinding {

    protected Element element;

    /**
     * Default constructor.
     */
    public ElementBinding(final Element element) {
        super();
        this.element = element;
    }

    /**
     * @param namespace
     */
    public ElementBinding(final Element element, final Namespace namespace) {
        super(namespace);
        this.element = element;
    }

    /**
     * Returns the specified child element of the element associated with this meta message. If the
     * child does not exist it is created and added to the message element.
     * 
     * @param name name of the child element
     * @return a JDOM element
     */
    public final Element getElement(final String name) {
        Element child = element.getChild(name, namespace);
        if (child == null) {
            child = new Element(name, namespace);
            element.addContent(child);
        }
        return child;
    }

    /**
     * @return the element
     */
    public final Element getElement() {
        return element;
    }
}
