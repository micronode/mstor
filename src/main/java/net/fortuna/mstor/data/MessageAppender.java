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

import net.fortuna.mstor.util.Configurator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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
public class MessageAppender {

    /**
     * A pattern representing the format of the "From_" line for the first message in an mbox file.
     */
    private static final Pattern MESSAGE_WITH_FROM__LINE_PATTERN = Pattern.compile("^From .*", Pattern.DOTALL);
    
    /**
     * The prefix for all "From_" lines in an mbox file.
     */
    public static final String FROM__PREFIX = "From ";

    private static final char[] DEFAULT_LINE_SEPARATOR = {'\r', '\n'};

    private static final String FROM__DATE_PATTERN = "EEE MMM d HH:mm:ss yyyy";

    private final Log log = LogFactory.getLog(MessageAppender.class);

    private final CharsetDecoder decoder;

    private final CharsetEncoder encoder;

    private final DateFormat from_DateFormat = new SimpleDateFormat(FROM__DATE_PATTERN, Locale.US);
    {
        from_DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    private final FileChannel channel;
    
    /**
     * @param channel
     */
    public MessageAppender(FileChannel channel) {
        this(channel, Charset.forName(Configurator.getProperty("mstor.mbox.encoding", "ISO-8859-1")));
    }
    
    /**
     * @param channel
     * @param charset
     */
    public MessageAppender(FileChannel channel, Charset charset) {
        this.channel = channel;
        decoder = charset.newDecoder();
        encoder = charset.newEncoder();
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }
    
    /**
     * Appends the specified message (represented by a byte array) to the specified channel.
     * @param message
     * @return
     */
    public long appendMessage(byte[] message) throws IOException {
        long messagePosition = channel.size();

        ByteBuffer buffer = ByteBuffer.wrap(MboxEncoder.encode(message));
        CharBuffer decoded = decoder.decode(buffer);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Appending message [" + decoded + "]");
        }

        if (!hasFromLine(decoded)) {
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("No From_ line found - inserting..");
            }
            
            channel.write(encoder.encode(CharBuffer.wrap(createFromLine())), channel.size());
        }
        buffer.rewind();

        channel.write(buffer, channel.size());

        return messagePosition;
    }

    /**
     * Indicates whether the specified CharSequence representation of a message contains a "From_"
     * line.
     *
     * @param message a CharSequence representing a message
     * @return true if a "From_" line is found, otherwise false
     */
    private boolean hasFromLine(final CharSequence message) {
        return MESSAGE_WITH_FROM__LINE_PATTERN.matcher(message).matches();
    }
    
    /**
     * @return
     */
    private String createFromLine() throws IOException {
        StringBuilder from_Line = new StringBuilder();
        
        // if not first message add required newlines..
        if (channel.size() > 0) {
            from_Line.append(DEFAULT_LINE_SEPARATOR).append(DEFAULT_LINE_SEPARATOR);
        }
        from_Line.append(FROM__PREFIX).append("- ").append(from_DateFormat.format(new Date()))
            .append(DEFAULT_LINE_SEPARATOR);
        return from_Line.toString();
    }
}
