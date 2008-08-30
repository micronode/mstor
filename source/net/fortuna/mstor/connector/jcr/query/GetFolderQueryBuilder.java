/*
 * $Id$
 *
 * Created on 28/08/2008
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
package net.fortuna.mstor.connector.jcr.query;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import net.fortuna.mstor.connector.jcr.RepositoryConnector.NodeNames;
import net.fortuna.mstor.connector.jcr.RepositoryConnector.PropertyNames;

/**
 * @author Ben
 *
 */
public class GetFolderQueryBuilder extends AbstractQueryBuilder {

    private Node node;
    
    private String folderName;
    
    /**
     * @param manager
     * @param type
     */
    public GetFolderQueryBuilder(QueryManager manager, Node node, String folderName) {
        super(manager, Query.XPATH);
        this.node = node;
        this.folderName = folderName;
    }

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.jcr.query.AbstractQueryBuilder#getQueryString()
     */
    protected String getQueryString() throws RepositoryException {
        StringBuffer b = new StringBuffer();
//        b.append('/');
//        b.append(node.getPath());
        b.append("//*[@jcr:uuid='");
        b.append(node.getUUID());
        b.append("']");
        b.append('/');
        b.append(NodeNames.FOLDER);
        b.append("[@");
        b.append(PropertyNames.NAME);
        b.append('=');
        b.append(folderName);
        b.append(']');
        return b.toString();
    }

}
