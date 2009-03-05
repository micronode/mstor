/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.mstor.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.Message;

/**
 * Implements a very rudimentary cache.
 * 
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [8/07/2004]
 * </pre>
 * 
 */
public class Cache extends LinkedHashMap<String, Message> {

    private static final long serialVersionUID = 4000823529559716310L;

    private static final int DEFAULT_MAX_ENTRIES = 1000;

    private static final String MAX_ENTRIES_PROPERTY = "mstor.cache.maxentries";

    private int maxEntries;

    /**
     * Constructor.
     */
    public Cache() {
        try {
            this.maxEntries = Integer.parseInt(System
                    .getProperty(MAX_ENTRIES_PROPERTY));
        }
        catch (Exception e) {
            this.maxEntries = DEFAULT_MAX_ENTRIES;
        }
    }

    /**
     * Constructor.
     * 
     * @param maxEntries
     */
    public Cache(final int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity
     * @param maxEntries
     */
    public Cache(final int initialCapacity, final int maxEntries) {
        super(initialCapacity);
        this.maxEntries = maxEntries;
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity
     * @param loadFactor
     * @param maxEntries
     */
    public Cache(final int initialCapacity, final float loadFactor,
            final int maxEntries) {
        super(initialCapacity, loadFactor);
        this.maxEntries = maxEntries;
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity
     * @param loadFactor
     * @param accessOrder
     * @param maxEntries
     */
    public Cache(final int initialCapacity, final float loadFactor,
            final boolean accessOrder, final int maxEntries) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxEntries = maxEntries;
    }

    /**
     * Constructor.
     * 
     * @param m
     * @param maxEntries
     */
    public Cache(final Map<String, Message> m, final int maxEntries) {
        super(m);
        this.maxEntries = maxEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
     */
    protected final boolean removeEldestEntry(final Entry<String, Message> entry) {
        return size() > maxEntries;
    }
}
