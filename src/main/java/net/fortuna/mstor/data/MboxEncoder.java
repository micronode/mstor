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
package net.fortuna.mstor.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Implements encoding for writing a message to an mbox file. Encoding involves automatically
 * escaping any "From_" patterns in the body of the message.
 *
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 10/04/2005
 * </pre>
 * 
 */
public final class MboxEncoder {

    private static final byte[] FROM__PATTERN = {'\n', '\n', 'F', 'r', 'o', 'm', ' '};

    private static final byte[] MASKED_FROM__PATTERN = {'\n', '\n', '>', 'F', 'r', 'o', 'm', ' '};

    /**
     * Constructor made private to prevent instantiation.
     */
    private MboxEncoder() {
    }

    /**
     * @param bytes raw data to encode
     * @return an mbox-encoded data array
     * @throws IOException where an error occurs generating encoded data
     */
    public static byte[] encode(final byte[] bytes) throws IOException {
        byte[] buffer = new byte[FROM__PATTERN.length];
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {

            for (int i = 0; i < bytes.length; i++) {
                // read ahead to see if we need to escape..
                for (int j = 0; j < buffer.length && i + j < bytes.length; j++) {
                    buffer[j] = bytes[i + j];
                }

                // insert mask if required..
                if (Arrays.equals(buffer, FROM__PATTERN)) {
                    bout.write(MASKED_FROM__PATTERN);
                    i += MASKED_FROM__PATTERN.length - 2;
                } else {
                    bout.write(new byte[]{bytes[i]});
                }
            }
            return bout.toByteArray();
        }
    }
}
