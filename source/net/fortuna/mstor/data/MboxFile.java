/*
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Contributors: Paul Legato - fix for purge() method,
 *  Michael G. Kaiser - add/strip of ">" characters from message content
 *  matching "From_" pattern (appendMessage()/getMessage())
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
package net.fortuna.mstor.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides access to an mbox-formatted file.
 * @author benfortuna
 */
public class MboxFile {

    public static final String READ_ONLY = "r";

    public static final String READ_WRITE = "rw";

    private static final String TEMP_FILE_EXTENSION = ".tmp";

    /**
     * The prefix for all "From_" lines in an mbox file.
     */
    private static final String FROM__PREFIX = "From ";

    /**
     * A pattern representing the format of the "From_" line
     * for the first message in an mbox file.
     */
    private static final String INITIAL_FROM__PATTERN = FROM__PREFIX + ".*";

    /**
     * A pattern representing the format of all "From_" lines
     * except for the first message in an mbox file.
     */
    private static final String FROM__PATTERN = "\n" + FROM__PREFIX;

    /**
     * A pattern representing the masked format of all message content
     * matching the "From_" line pattern
     */
    private static final String MASKED_FROM__PATTERN = "\n>" + FROM__PREFIX;

    private static final String FROM__DATE_FORMAT = "EEE MMM d HH:mm:ss yyyy";

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static DateFormat from_DateFormat = new SimpleDateFormat(FROM__DATE_FORMAT);

    static {
        from_DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // Charset and decoder for ISO-8859-15
//    private static Charset charset = Charset.forName("ISO-8859-1");
    private static Charset charset = Charset.forName(System.getProperty("file.encoding"));

    private static CharsetDecoder decoder = charset.newDecoder();

    private static CharsetEncoder encoder = charset.newEncoder();

    static {
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    private static Log log = LogFactory.getLog(MboxFile.class);

    /**
     * Used primarily to provide information about
     * the mbox file.
     */
    private File file;

    private String mode;

    /**
     * Used to access the mbox file in a random manner.
     */
    private FileChannel channel;

    /**
     * Tracks all message positions within the mbox file.
     */
    private long[] messagePositions;

    /**
     * Constructor.
     */
    public MboxFile(File file) throws FileNotFoundException {
        this(file, READ_ONLY);
    }

    /**
     * Constructor.
     */
    public MboxFile(File file, String mode) {
        this.file = file;
        this.mode = mode;
    }

    /**
     * Returns a channel for reading and writing to the mbox file.
     * @return a file channel
     * @throws FileNotFoundException
     */
    private FileChannel getChannel() throws FileNotFoundException {
        if (channel == null) {
            channel = new RandomAccessFile(file, mode).getChannel();
        }

        return channel;
    }


    /**
     * Returns an initialised array of file positions
     * for all messages in the mbox file.
     * @return a long array
     * @throws IOException thrown when unable to read
     * from the specified file channel
     */
    private long[] getMessagePositions() throws IOException {
        if (messagePositions == null) {
            List posList = new ArrayList();

            // debugging..
            log.debug("Channel size [" + getChannel().size() + "] bytes");

            // read mbox file to determine the message
            // positions..
            /*
            ByteBuffer buffer = getChannel().map(FileChannel.MapMode.READ_ONLY, 0, getChannel().size());

            CharBuffer cb = decoder.decode(buffer);

            // check that first message is correct..
            if (Pattern.compile(INITIAL_FROM__PATTERN, Pattern.DOTALL).matcher(cb).matches()) {
                // debugging..
                log.debug("Matched first message..");

                posList.add(new Long(0));
            }

            //Pattern fromPattern = Pattern.compile("\n\r\n" + FROM_);
            Pattern fromPattern = Pattern.compile(FROM__PATTERN);

            //Matcher matcher = fromPattern.matcher(buffer.asCharBuffer());
            Matcher matcher = fromPattern.matcher(cb);

            while (matcher.find()) {
                // debugging..
                log.debug("Found match at [" + matcher.start() + "]");

                // add one (1) to position to account for newline..
                posList.add(new Long(matcher.start() + 1));
            }
            */

			Reader fileReader = Channels.newReader((ReadableByteChannel) getChannel(), decoder, DEFAULT_BUFFER_SIZE);

			BufferedReader reader = new BufferedReader(fileReader);

			// check that first message is correct..
			String line = reader.readLine();

			if (Pattern.compile(INITIAL_FROM__PATTERN, Pattern.DOTALL).matcher(line).matches()) {
				// debugging..
				log.debug("Matched first message..");

				posList.add(new Long(0));
			}

			long position = line.length() + 1;

			while ((line = reader.readLine()) != null) {

				if (line.startsWith(FROM__PREFIX)) {
					// debugging..
					log.debug("Found match at [" + position + "]");

					posList.add(new Long(position));
				}
				position += line.length()+1;
			}

            messagePositions = new long[posList.size()];

            int count = 0;

            for (Iterator i = posList.iterator(); i.hasNext(); count++) {
                messagePositions[count] = ((Long) i.next()).longValue();
            }
        }

        return messagePositions;
    }

    /**
     * Returns the total number of messages in the mbox
     * file.
     * @return an int
     */
    public int getMessageCount() throws IOException {
        return getMessagePositions().length;
    }

    /**
     * Returns a CharSequence containing the data for
     * the message at the specified index.
     * @param index the index of the message to retrieve
     * @return a CharSequence
     */
    public CharSequence getMessage(final int index) throws IOException {
        long position = getMessagePositions()[index];
        long size;

        if (index < getMessagePositions().length - 1) {
            size = getMessagePositions()[index + 1] - getMessagePositions()[index];
        }
        else {
            size = getChannel().size() - getMessagePositions()[index];
        }

        CharSequence message = decoder.decode(getChannel().map(FileChannel.MapMode.READ_ONLY, position, size));

        // remove extraneous ">" characters added to maintain integrity of mbox file..
//        Pattern maskedFromPattern = Pattern.compile("\\n>From ");
        Pattern maskedFromPattern = Pattern.compile(MASKED_FROM__PATTERN);

        //Matcher matcher = fromPattern.matcher(buffer.asCharBuffer());
        Matcher matcher = maskedFromPattern.matcher(message);
        if (matcher.find()) {
            matcher.reset();
            message = matcher.replaceAll(FROM__PATTERN);
        }

        return message;
    }

    /**
     * Opens an input stream to the specified message
     * data.
     * @param index the index of the message to open
     * a stream to
     * @return an input stream
     */
    public InputStream getMessageAsStream(int index) throws IOException {
        return new ByteArrayInputStream(getMessage(index).toString().getBytes());
    }

    /**
     * Appends the specified message (represented by a CharSequence) to the
     * mbox file.
     * @param message
     */
    public final void appendMessage(final CharSequence message) throws IOException {
        appendMessage(message, getChannel());
    }

    /**
     * Appends the specified message (represented by a CharSequence)
     * to the specified channel.
     * @param message
     * @param channel
     * @throws IOException
     */
    private void appendMessage(final CharSequence message, final FileChannel channel) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(message.length());

        // copy message to avoid modifying method arguments directly..
        CharSequence newMessage = message.toString();

//        encoder.reset();

        // add ">" characters to message content matching the "From_" line pattern
        // to maintain integrity of mbox file..
        // NOTE: This shouldn't replace any existing "From_" line as the from pattern
        // contains a newline..

        //Pattern fromPattern = Pattern.compile("\n\r\n" + FROM_);
        Pattern fromPattern = Pattern.compile(FROM__PATTERN);

        //Matcher matcher = fromPattern.matcher(buffer.asCharBuffer());
        Matcher matcher = fromPattern.matcher(newMessage);
        if (matcher.find()) {
            matcher.reset();
            newMessage = matcher.replaceAll(MASKED_FROM__PATTERN);
        }

        if (!hasFrom_Line(newMessage)) {
            // if not first message add required newlines..
            if (channel.size() > 0) {
                channel.write(encoder.encode(CharBuffer.wrap("\n\n")), channel.size());
//                encoder.encode(CharBuffer.wrap("\n\n"), buffer, false);
            }
            channel.write(encoder.encode(CharBuffer.wrap(FROM__PREFIX + "- " + from_DateFormat.format(new Date()) + "\n")), channel.size());
//            encoder.encode(CharBuffer.wrap(DEFAULT_FROM__LINE), buffer, false);
        }

        channel.write(encoder.encode(CharBuffer.wrap(newMessage)), channel.size());
//        encoder.encode(CharBuffer.wrap(message), buffer, true);
//        encoder.flush(buffer);

//        channel.write(buffer, channel.size());
    }

    /**
     * Purge the specified messages from the file.
     * @param msgnums the indices of the messages to purge
     */
    public void purge(int[] msgnums) throws IOException {
        // create a new mailbox file..
        File newFile = new File(file.getParent(), file.getName() + TEMP_FILE_EXTENSION);

        FileChannel newChannel = new FileOutputStream(newFile).getChannel();

        loop: for (int i=0; i<getMessagePositions().length; i++) {
            for (int j=0; j<msgnums.length; j++) {
                if (msgnums[j] == i) {
                    // don't save message to file if in purge list..
                    continue loop;
                }
            }

            // append current message to new file..
            appendMessage(getMessage(i), newChannel);
        }

        // ensure new file is properly written..
        newChannel.close();

        // release system resources..
        close();

        // delete old file..
        File tempFile = new File(file.getParent(), file.getName() + "." + System.currentTimeMillis());
        file.renameTo(tempFile);
        // wait until exit to delete in case program terminates
        // abnormally and need to recover data..
        tempFile.deleteOnExit();

        // rename new file..
        newFile.renameTo(file);

        this.file = newFile;
    }

    /**
     * Close the mbox file and release any system resources.
     * @throws IOException
     */
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

    /**
     * Indicates whether the specified CharSequence representation of
     * a message contains a "From_" line.
     * @param message a CharSequence representing a message
     * @return true if a "From_" line is found, otherwise false
     */
    private boolean hasFrom_Line(CharSequence message) {
        return Pattern.compile(FROM__PREFIX + ".*", Pattern.DOTALL).matcher(message).matches();
    }
}
