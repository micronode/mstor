/*
 * $Id$
 *
 * Created on 21/09/2008
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
package net.fortuna.mstor.connector.jcr;

import org.apache.commons.lang.enums.Enum;

/**
 * @author Ben
 *
 */
public final class NodeProperty extends Enum {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3019029775415398021L;

    public static final NodeProperty NAME = new NodeProperty("mstor:name");
    
    // folder-specific properties..
    public static final NodeProperty TYPE = new NodeProperty("mstor:type");
    
    public static final NodeProperty LAST_UID = new NodeProperty("mstor:last-uid");
    
    public static final NodeProperty UID_VALIDITY = new NodeProperty("mstor:uid-validity");
    
    // message-specific properties..
    public static final NodeProperty VALUE = new NodeProperty("mstor:value");
    
    public static final NodeProperty MESSAGE_NUMBER = new NodeProperty("mstor:messageNumber");
    
    public static final NodeProperty RECEIVED = new NodeProperty("mstor:received");
    
    public static final NodeProperty REPLIED = new NodeProperty("mstor:replied");
    
    public static final NodeProperty FOWARDED = new NodeProperty("mstor:forwarded");
    
    public static final NodeProperty UID = new NodeProperty("mstor:uid");
    
    public static final NodeProperty EXPUNGED = new NodeProperty("mstor:expunged");

    /**
     * @param name
     */
    public NodeProperty(String name) {
        super(name);
    }

}
