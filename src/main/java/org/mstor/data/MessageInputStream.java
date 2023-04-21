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
package org.mstor.data;

import org.mstor.util.Configurator;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ben
 * 
 * <pre>
 * $Id$
 *
 * Created on 03/03/2008
 * </pre>
 * 
 *
 */
public class MessageInputStream extends InputStream {

    /**
     * Pattern used to match the From_ line within a message buffer.
     */
    static final Pattern FROM__LINE_PATTERN = Pattern.compile("(\\A|\\n{2}|(\\r\\n){2})^From .*$\\s*^", Pattern.MULTILINE);

    private final ByteBuffer buffer;

    /**
     * @param b
     */
    public MessageInputStream(final ByteBuffer b) throws CharacterCodingException {
        this(b, Charset.forName(Configurator.getProperty("mstor.mbox.encoding", "ISO-8859-1")));
    }
    
    /**
     * @param b
     * @param charset
     * @throws CharacterCodingException
     */
    public MessageInputStream(final ByteBuffer b, Charset charset) throws CharacterCodingException {
        this.buffer = b;
        CharsetDecoder decoder = charset.newDecoder();
        
        // adjust position to exclude the From_ line..
        Matcher matcher = FROM__LINE_PATTERN.matcher(decoder.decode(buffer));
        if (matcher.find()) {
            buffer.rewind();
            buffer.position(buffer.position() + matcher.end());
            buffer.mark();
        }
        
        // rewind for a re-read of buffer data..
        try {
            buffer.reset();
        } catch (InvalidMarkException ime) {
            buffer.rewind();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int read() {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int read(final byte[] bytes, final int offset, final int length) {
        
        if (!buffer.hasRemaining()) {
            return -1;
        }
        int read = Math.min(length, buffer.remaining());
        buffer.get(bytes, offset, read);
        return read;
    }
}
