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
package org.mstor.mbox.data.yaml;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ben
 *
 */
public class FolderExt {

    private List<MessageExt> messages;

    private long lastUid;

    private long uidValidity;

    /**
     * Default constructor.
     */
    public FolderExt() {
        messages = new CopyOnWriteArrayList<>();
    }

    /**
     * @return the lastUid
     */
    public final long getLastUid() {
        return lastUid;
    }

    /**
     * @param lastUid the lastUid to set
     */
    public final void setLastUid(long lastUid) {
        this.lastUid = lastUid;
    }

    /**
     * @return the messages
     */
    public final List<MessageExt> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public final void setMessages(List<MessageExt> messages) {
        this.messages = messages;
    }

    /**
     * @return the uidValidity
     */
    public final long getUidValidity() {
        return uidValidity;
    }

    /**
     * @param uidValidity the uidValidity to set
     */
    public final void setUidValidity(long uidValidity) {
        this.uidValidity = uidValidity;
    }

}
