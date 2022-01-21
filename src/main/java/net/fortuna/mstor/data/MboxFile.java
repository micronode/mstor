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

import net.fortuna.mstor.util.CacheAdapter;
import net.fortuna.mstor.util.CapabilityHints;
import net.fortuna.mstor.util.Configurator;
import net.fortuna.mstor.util.EhCacheAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides access to an mbox-formatted file. To read an mbox file using a non-standard file
 * encoding you may specify the following system property:
 *
 * <pre>
 *        -Dmstor.mbox.encoding=&lt;some_encoding&gt;
 * </pre>
 *
 * If no encoding system property is specified the default file encoding will be used.
 *
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Contributors: Paul Legato - fix for purge() method,
 *  Michael G. Kaiser - add/strip of ">" characters from message content
 *  matching "From_" pattern (appendMessage()/getMessage())
 *  </pre>
 * 
 */
public class MboxFile {

    /**
     * A capability hint to indicate the preferred strategy for reading mbox files into a buffer.
     */
    static final String KEY_BUFFER_STRATEGY = "mstor.mbox.bufferStrategy";

    /**
     * Strategy for I/O buffers.
     *
     */
    public enum BufferStrategy {
        
        /**
         * Default strategy used in Java nio.
         */
        DEFAULT, 
        
        /**
         * Map buffers.
         */
        MAPPED, 
        
        /**
         * Use direct buffers.
         */
        DIRECT
    }
    
    /**
     * Indicates a file should be opened for reading only.
     */
    public static final String READ_ONLY = "r";

    /**
     * Indicates a file should be opened for reading and writing.
     */
    public static final String READ_WRITE = "rw";

    private static final String TEMP_FILE_EXTENSION = ".tmp";
    
    /**
     * The prefix for all "From_" lines in an mbox file.
     */
    public static final String FROM__PREFIX = "From ";

    /**
     * Max length of the "From_" prefix line (including whitespace)
     */
    private static final int MAX_MESSAGE_HEADER_LENGTH = FROM__PREFIX.length() + 4;

    /**
     * A pattern representing the format of the "From_" line for the first message in an mbox file.
     */
    private static final Pattern VALID_MBOX_PATTERN = Pattern.compile("^" + FROM__PREFIX + ".*",
            Pattern.DOTALL);

    /**
     * Pattern used to match the From_ line within a message buffer.
     */
    private static final Pattern FROM__LINE_PATTERN = Pattern.compile("(\\A|\\n{2}|(\\r\\n){2})^From .*$",
            Pattern.MULTILINE);

    /*
     * this differs from the FROM__LINE_PATTERN in that it supports
     *  - files where there is no blank line before the FROM_ line 
     *  - foxmail files
     *  - old outlook express .mbx files
     */
    private static final Pattern RELAXED_FROM__LINE_PATTERN = 
    	Pattern.compile("^(" +                                          // at the beginning of a line begins either a
    			"From .*" +                                             // normal From_ line 
    			")|(" +                                                 // or
    			"\\u0010\\u0010\\u0010\\u0010\\u0010\\u0010\\u0010" +   // 7 0x10 bytes 
    			"\\u0011\\u0011\\u0011\\u0011\\u0011\\u0011\\u0053" +   // 6 0x11 bytes and an 'S' (0x53) (a foxmail signature)
    			")$", Pattern.MULTILINE);                               // all of this up to the end of the line
    
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName(Configurator.getProperty(
            "mstor.mbox.encoding", "ISO-8859-1"));

    private final Log log = LogFactory.getLog(MboxFile.class);

    private final CharsetDecoder decoder = charset.newDecoder();

    private final CharsetEncoder encoder = charset.newEncoder();
    {
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    /**
     * Used primarily to provide information about the mbox file.
     */
    private final File file;

    private final String mode;

    private RandomAccessFile raf;

    /**
     * Used to access the mbox file in a random manner.
     */
    private FileChannel channel;

    /**
     * Tracks all message positions within the mbox file.
     */
    private Long[] messagePositions;

    /**
     * An adapter for the cache for buffers
     */
    private CacheAdapter cacheAdapter;

    /**
     * @param file a reference to an mbox data file
     */
    public MboxFile(final File file) {
        this(file, READ_ONLY);
    }

    /**
     * @param file a reference to an mbox data file
     * @param mode the mode used to open the file
     */
    public MboxFile(final File file, final String mode) {
        this.file = file;
        this.mode = mode;
    }

    /**
     * Returns a random access file providing access to the mbox file.
     *
     * @return a random access file
     * @throws FileNotFoundException
     */
    private RandomAccessFile getRaf() throws FileNotFoundException {
        if (raf == null) {
            raf = new RandomAccessFile(file, mode);
        }
        return raf;
    }

    /**
     * Returns a channel for reading and writing to the mbox file.
     *
     * @return a file channel
     * @throws FileNotFoundException
     */
    private FileChannel getChannel() throws FileNotFoundException {
        if (channel == null) {
            channel = getRaf().getChannel();
        }
        return channel;
    }

    /**
     * Reads from the mbox file using the most appropriate buffer strategy available. The buffer is
     * also flipped (for reading) prior to returning.
     * 
     * @param position
     * @param size
     * @return a ByteBuffer containing up to <em>size</em> bytes starting at the specified
     *         position in the file.
     */
    private ByteBuffer read(final long position, final int size) throws IOException {

        ByteBuffer buffer;
        try {
            BufferStrategy bufferStrategy = null;
            if (Configurator.getProperty(KEY_BUFFER_STRATEGY) != null) {
                bufferStrategy = BufferStrategy.valueOf(Configurator.getProperty(KEY_BUFFER_STRATEGY).toUpperCase());
            }
            
            if (BufferStrategy.MAPPED.equals(bufferStrategy)) {
                buffer = getChannel().map(FileChannel.MapMode.READ_ONLY, position, size);
            }
            else {
                if (BufferStrategy.DIRECT.equals(bufferStrategy)) {
                    buffer = ByteBuffer.allocateDirect(size);
                }
                else if (BufferStrategy.DEFAULT.equals(bufferStrategy) || bufferStrategy == null) {
                    buffer = ByteBuffer.allocate(size);
                }
                else {
                    throw new IllegalArgumentException("Unrecognised buffer strategy: " + Configurator.getProperty(KEY_BUFFER_STRATEGY));
                }
                getChannel().position(position);
                getChannel().read(buffer);
                buffer.flip();
            }
        } catch (IOException ioe) {
            log.warn("Error reading bytes using nio", ioe);
            getRaf().seek(position);
            byte[] buf = new byte[size];
            getRaf().read(buf);
            buffer = ByteBuffer.wrap(buf);
        }
        return buffer;
    }

    /**
     * Returns an initialised array of file positions for all messages in the mbox file.
     *
     * @return a long array
     * @throws IOException thrown when unable to read from the specified file channel
     */
    private Long[] getMessagePositions() throws IOException {
        if (messagePositions == null) {
            Set<Long> posList = new HashSet<>();

            // debugging..
            log.debug("Channel size [" + getChannel().size() + "] bytes");

            int bufferSize = (int) Math.min(getChannel().size(), DEFAULT_BUFFER_SIZE);

            // read mbox file to determine the message positions..
            ByteBuffer buffer = read(0, bufferSize);
            CharSequence cs = decoder.decode(buffer);

            // debugging..
            log.debug("Buffer [" + cs + "]");

            // indicates the offset of the current buffer..
            long offset = 0;

            for (;;) {
                Matcher matcher;
                if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_RELAXED_PARSING)) {
                    matcher = RELAXED_FROM__LINE_PATTERN.matcher(cs);
                } else {
                    matcher = FROM__LINE_PATTERN.matcher(cs);
                }

                while (matcher.find()) {
                    // debugging..
                    log.debug("Found match at [" + (offset + matcher.start()) + "]");

                    posList.add(offset + matcher.start());
                }

                if (offset + bufferSize >= getChannel().size()) {
                    break;
                } else {
                    // preserve the end of the buffer as it may contain
                    // part of a From_ pattern..
                    offset += bufferSize - MAX_MESSAGE_HEADER_LENGTH;

                    bufferSize = (int) Math.min(getChannel().size() - offset, DEFAULT_BUFFER_SIZE);
                    buffer = read(offset, bufferSize);
                    cs = decoder.decode(buffer);
                }
            }

            messagePositions = posList.toArray(new Long[0]);
        }
        return messagePositions;
    }

    /**
     * Returns the total number of messages in the mbox file.
     *
     * @return an int
     * @throws IOException where an error occurs reading messages
     */
    public final int getMessageCount() throws IOException {
        return getMessagePositions().length;
    }

    /**
     * Opens an input stream to the specified message data.
     *
     * @param index the index of the message to open a stream to
     * @return an input stream
     * @throws IOException where an error occurs reading the message
     */
    public final InputStream getMessageAsStream(final int index) throws IOException {
        
        ByteBuffer buffer = null;
        
        if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_CACHE_BUFFERS)) {
            buffer = retrieveBufferFromCache(index);
        }

        if (buffer == null) {
            long position = getMessagePositions()[index];
            long size;

            if (index < getMessagePositions().length - 1) {
                size = getMessagePositions()[index + 1] - getMessagePositions()[index];
            }
            else {
                size = getChannel().size() - getMessagePositions()[index];
            }
            
            buffer = read(position, (int) size);
            
            if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_CACHE_BUFFERS)) {
                // add buffer to cache..
                putBufferInCache(index,buffer);
            }
        }
        return new MessageInputStream(buffer);
    }

    /**
     * Convenience method that returns a message as a byte array containing the data for the message
     * at the specified index.
     *
     * @param index the index of the message to retrieve
     * @return a byte array
     * @throws IOException where an error occurs reading the message
     */
    public final byte[] getMessage(final int index) throws IOException {
        InputStream in = getMessageAsStream(index);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int read;
        while ((read = in.read()) != -1) {
            bout.write(read);
        }
        return bout.toByteArray();
    }

    /**
     * Appends the specified message (represented by a CharSequence) to the mbox file.
     *
     * @param message
     * @throws IOException where an error occurs writing the message data
     */
    public final void appendMessage(final byte[] message) throws IOException {
        // synchronise purging and appending to avoid message loss..
        synchronized (file) {
            
            MessageAppender appender = new MessageAppender(getChannel());
            long newMessagePosition = appender.appendMessage(message);
    
            // update message positions..
            if (messagePositions != null) {
                Long[] newMessagePositions = new Long[messagePositions.length + 1];
                System.arraycopy(messagePositions, 0, newMessagePositions, 0,
                        messagePositions.length);
                newMessagePositions[newMessagePositions.length - 1] = newMessagePosition;
                messagePositions = newMessagePositions;
            }
        }
        
        // clear cache..
        clearBufferCache();
    }

    /**
     * Purge the specified messages from the file.
     *
     * @param msgnums the indices of the messages to purge
     * @throws IOException where an error occurs updating the data file
     */
    public final void purge(final int[] msgnums) throws IOException {
        // create a new mailbox file..
        // Create the new file in the temp directory which is always read/write
        File newFile = new File(System.getProperty("java.io.tmpdir"),
                file.getName() + TEMP_FILE_EXTENSION);

        try (FileOutputStream newOut = new FileOutputStream(newFile)) {
            FileChannel newChannel = newOut.getChannel();
            MessageAppender appender = new MessageAppender(newChannel);

            // synchronise purging and appending to avoid message loss..
            synchronized (file) {

                loop:
                for (int i = 0; i < getMessagePositions().length; i++) {
                    for (int msgnum : msgnums) {
                        if (msgnum == i) {
                            // don't save message to file if in purge list..
                            continue loop;
                        }
                    }
                    // append current message to new file..
                    appender.appendMessage(getMessage(i));
                }

                // release system resources..
                close();

                // Create the new file in the temp directory which is always read/write
                File tempFile = new File(System.getProperty("java.io.tmpdir"),
                        file.getName() + "." + System.currentTimeMillis());

                if (!renameTo(file, tempFile)) {
                    throw new IOException("Unable to rename existing file");
                }
                // wait until exit to delete in case program terminates
                // abnormally and need to recover data..
                tempFile.deleteOnExit();

                // rename new file..
                renameTo(newFile, file);
            }
        }
    }

    /**
     * @param source
     * @param dest
     * @return
     */
    private boolean renameTo(final File source, final File dest) {
        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Renaming [" + source + "] to [" + dest + "]");
        }

        // remove any existing dest file..
        if (dest.exists()) {
            dest.delete();
        }
        boolean success = source.renameTo(dest);
        if (!success) {
            try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(dest)) {
                int length;
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                while ((length = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, length);
                }

                try {
                    success = source.delete();
                }
                catch (Exception e) {
                    log.warn("Error cleaning up", e);
                }
            }
            catch (IOException ioe) {
                log.error(
                        "Failed to rename [" + source + "] to [" + dest + "]",
                        ioe);
            }
        }
        return success;
    }

    /**
     * Close the mbox file and release any system resources.
     *
     * @throws IOException where an error occurs closing the data file
     */
    public final void close() throws IOException {
        if (messagePositions != null) {
            messagePositions = null;
        }
        if (raf != null) {
            raf.close();
            raf = null;
            channel = null;
        }
    }

    /**
     * Indicates whether the specified file appears to be a valid mbox file. Note that this method
     * does not check the entire file for validity, but rather checks the first line for indication
     * that this is an mbox file.
     * @param file an mbox file reference
     * @return true if the specified file is a valid mbox file
     */
    public static boolean isValid(final File file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                Charset.forName("UTF-8")))) {

            // check that first message is correct..
            String line = reader.readLine();

            return line == null || VALID_MBOX_PATTERN.matcher(line).matches();
        }
        catch (Exception e) {
            Log log = LogFactory.getLog(MboxFile.class);
            log.info("Not a valid mbox file [" + file + "]", e);
        }

        return false;
    }
    
    private void putBufferInCache(int index, ByteBuffer buffer) {
        getCacheAdapter().putObjectIntoCache(index, buffer);
    }
    
    private void clearBufferCache() {
        getCacheAdapter().clearCache();
    }
    
    private ByteBuffer retrieveBufferFromCache(int index) {
        return (ByteBuffer)getCacheAdapter().retrieveObjectFromCache(index);
    }
        
    private CacheAdapter getCacheAdapter() {
        if (cacheAdapter == null) {
            if (Configurator.getProperty("mstor.cache.disabled", "false").equals("true")) {
                this.cacheAdapter = new CacheAdapter();
            } else {
                this.cacheAdapter = new EhCacheAdapter("mstor.mbox." + file.getAbsolutePath().hashCode());
            }
        }
        return cacheAdapter;
    }
}
