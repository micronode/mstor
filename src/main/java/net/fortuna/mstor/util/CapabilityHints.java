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

import java.util.HashMap;
import java.util.Map;

/**
 * A set of keys used to enable capability features.
 * 
 * @author Ben Fortuna
 */
public final class CapabilityHints {

    /**
     * A capability hint to indicate whether buffers used to read mbox message data are cached for
     * faster access.
     */
    public static final String KEY_MBOX_CACHE_BUFFERS = "mstor.mbox.cacheBuffers";

    /**
     * A capability hint to enable mozilla mbox compatibility.
     */
    public static final String KEY_MBOX_MOZILLA_COMPATIBILITY = "mstor.mbox.mozillaCompatibility";

    private static final Map<String, Boolean> HINTS = new HashMap<String, Boolean>();

    /**
     * Constructor made private to enforce static nature.
     */
    private CapabilityHints() {
    }

    /**
     * @param key
     * @param value
     */
    public static void setHintEnabled(final String key, final boolean enabled) {
        HINTS.put(key, enabled);
    }

    /**
     * @param key
     * @return
     */
    public static boolean isHintEnabled(final String key) {
        if (Configurator.getProperty(key) != null) {
            return Boolean.valueOf(Configurator.getProperty(key));
        }
        return Boolean.TRUE.equals(HINTS.get(key));
    }
}
