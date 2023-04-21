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
package net.fortuna.mstor.data.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 6/05/2006
 * </pre>
 * 
 */
public class DocumentBinding extends XmlBinding {

    private final File file;

    private Document document;
    
    private final String rootElementName;

    /**
     * @param file
     */
    public DocumentBinding(final File file, String rootElementName) {
        this.file = file;
        this.rootElementName = rootElementName;
    }

    /**
     * @param file
     * @param namespace
     */
    public DocumentBinding(final File file, final Namespace namespace, String rootElementName) {
        super(namespace);
        this.file = file;
        this.rootElementName = rootElementName;
    }

    /**
     * Returns the JDOM document associated with this meta folder.
     * 
     * @return a JDOM document
     * @throws org.jdom.JDOMException thrown if the specified file is not a valid XML document
     * @throws IOException thrown if an error occurs reading the specified file
     */
    public final Document getDocument() {
        if (document == null) {
            try {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(file);
            }
            catch (Exception e) {
                // create an empty document if unable to read
                // from filesystem..
                document = new Document(new Element(rootElementName,
                        namespace));
            }
        }
        return document;
    }

    /**
     * @throws IOException
     */
    public final void save() throws IOException {
        XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
        xmlOut.getFormat().setIndent("  ");

        try (FileOutputStream fout = new FileOutputStream(file)) {
            xmlOut.output(getDocument(), fout);
        }
    }
}
