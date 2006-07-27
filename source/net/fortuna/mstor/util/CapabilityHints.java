/*
 * $Id$
 *
 * Created on 8/06/2006
 *
 * Copyright (c) 2006, Ben Fortuna
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
package net.fortuna.mstor.util;

import java.util.Properties;

/**
 * A set of keys used to enable compatibility features.
 * @author Ben Fortuna
 */
public final class CapabilityHints {

    /**
     * A capability hint to indicate the preferred strategy for reading mbox
     * files into a buffer.
     */
    public static final String KEY_MBOX_BUFFER_STRATEGY = "mstor.mbox.bufferStrategy";
    
    /**
     * A value to enable the use of Java NIO buffer mapping when
     * reading mbox files.
     */
    public static final String VALUE_MBOX_BUFFER_STRATEGY_MAPPED = "mapped";

    /**
     * A value to enable the use of direct buffers when reading
     * mbox files.
     */
    public static final String VALUE_MBOX_BUFFER_STRATEGY_DIRECT = "direct";

    /**
     * A value to enable the use of default buffers when reading
     * mbox files.
     */
    public static final String VALUE_MBOX_BUFFER_STRATEGY_DEFAULT = "default";

    
    /**
     * A capability hint to indicate whether buffers used to read mbox message
     * data are cached for faster access.
     */
    public static final String KEY_MBOX_CACHE_BUFFERS = "mstor.mbox.cacheBuffers";
    
    /**
     * A value to enable caching of message buffers when
     * reading mbox files.
     */
    public static final String VALUE_MBOX_CACHE_BUFFERS_ENABLED = "enabled";
    
    /**
     * A value to disable caching of message buffers when
     * reading mbox files.
     */
    public static final String VALUE_MBOX_CACHE_BUFFERS_DISABLED = "disabled";
    
    /**
     * A value to disable caching of message buffers when
     * reading mbox files.
     */
    public static final String VALUE_MBOX_CACHE_BUFFERS_DEFAULT = "default";

    
    /**
     * A capability hint to indicate whether metadata should be used when
     * accessing folders and messages. Note that if this property is specified
     * as a JavaMail session property it will override capability hint and
     * System property settings.
     */
    public static final String KEY_METADATA = "mstor.metadata";
    
    /**
     * A value to enable the use of folder and message metadata.
     */
    public static final String VALUE_METADATA_ENABLED = "enabled";
    
    /**
     * A value to disable the use of folder and message metadata.
     */
    public static final String VALUE_METADATA_DISABLED = "disabled";
    
    /**
     * A value to disable the use of folder and message metadata.
     */
    public static final String VALUE_METADATA_DEFAULT = "default";

    
    /**
     * A capability hint to enable mozilla mbox compatibility.
     */
    public static final String KEY_MOZILLA_COMPATIBILITY = "mstor.mozillaCompatibility";

    /**
     * A value used to enabled mozilla mbox compatibility.
     */
    public static final String VALUE_MOZILLA_COMPATIBILITY_ENABLED = "enbled";

    /**
     * A value used to disable mozilla mbox compatibility.
     */
    public static final String VALUE_MOZILLA_COMPATIBILITY_DISABLED = "enbled";
    
    
    private static final Properties HINTS = new Properties(System.getProperties());
    
    /**
     * Constructor made private to enforce static nature.
     */
    private CapabilityHints() {
    }
    
    /**
     * @param key
     * @param value
     */
    public static void setHint(final String key, final String value) {
        HINTS.setProperty(key, value);
    }

    /**
     * @param key
     * @return
     */
    public static String getHint(final String key) {
        return HINTS.getProperty(key);
    }
}
