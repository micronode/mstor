/*
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Contributors: Paul Legato - fix for purge() method,
 *  Michael G. Kaiser - add/strip of ">" characters from message content
 *  matching "From_" pattern (appendMessage()/getMessage())
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.mstor.util.CapabilityHints;
import net.fortuna.mstor.util.Configurator;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 */
public class MboxFile {

    /**
     * A capability hint to indicate the preferred strategy for reading mbox files into a buffer.
     */
    public static final String KEY_BUFFER_STRATEGY = "mstor.mbox.bufferStrategy";

    /**
     * @author Ben
     *
     */
    public static final class BufferStrategy extends org.apache.commons.lang.enums.Enum {
        
        /**
         * 
         */
        private static final long serialVersionUID = -8365766175291020044L;

        public static final BufferStrategy DEFAULT = new BufferStrategy("default");
        
        public static final BufferStrategy MAPPED = new BufferStrategy("mapped");
        
        public static final BufferStrategy DIRECT = new BufferStrategy("direct");
        
        /**
         * @param name
         */
        private BufferStrategy(String name) {
            super(name);
        }
        
        /**
         * @param name
         * @return
         */
        public static BufferStrategy getBufferStrategy(String name) {
            return (BufferStrategy) getEnum(BufferStrategy.class, name);
        }
    }
    
    public static final String READ_ONLY = "r";

    public static final String READ_WRITE = "rw";

    private static final String TEMP_FILE_EXTENSION = ".tmp";
    
    /**
     * The prefix for all "From_" lines in an mbox file.
     */
    public static final String FROM__PREFIX = "From ";

    /**
     * A pattern representing the format of the "From_" line for the first message in an mbox file.
     */
    private static final Pattern VALID_MBOX_PATTERN = Pattern.compile("^" + FROM__PREFIX + ".*",
            Pattern.DOTALL);

    /**
     * Pattern used to match the From_ line within a message buffer.
     */
//    private static final Pattern FROM__LINE_PATTERN = Pattern.compile("[\\n\\n|\\r\\n\\r\\n]" + FROM__PREFIX + ".*$",
//            Pattern.MULTILINE);
    static final Pattern FROM__LINE_PATTERN = Pattern.compile("(\\A|\\n{2}|(\\r\\n){2})^From .*$", Pattern.MULTILINE);

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    // Charset and decoder for ISO-8859-15
    // private static Charset charset =
    // Charset.forName(System.getProperty("mstor.mbox.encoding",
    // System.getProperty("file.encoding")));
    // private static Charset charset =
    // Charset.forName(System.getProperty("mstor.mbox.encoding", "US-ASCII"));
    private static Charset charset = Charset.forName(Configurator.getProperty(
            "mstor.mbox.encoding", "ISO-8859-1"));

    private Log log = LogFactory.getLog(MboxFile.class);

    private CharsetDecoder decoder = charset.newDecoder();

    private CharsetEncoder encoder = charset.newEncoder();
    {
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        // decoder.onMalformedInput(CodingErrorAction.REPLACE);
    }

    /**
     * Used primarily to provide information about the mbox file.
     */
    private File file;

    private String mode;

    private RandomAccessFile raf;

    /**
     * Used to access the mbox file in a random manner.
     */
    private FileChannel channel;

    /**
     * Tracks all message positions within the mbox file.
     */
    private long[] messagePositions;

    /**
     * A cache used to store mapped regions of the mbox file representing message data.
     */
//    private Cache messageCache;

    /**
     * Constructor.
     */
    public MboxFile(final File file) throws FileNotFoundException {
        this(file, READ_ONLY);
    }

    /**
     * Constructor.
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
    private ByteBuffer read(final long position, final int size)
            throws IOException {

        ByteBuffer buffer = null;
        try {
            if (BufferStrategy.MAPPED.equals(BufferStrategy.getBufferStrategy(Configurator.getProperty(KEY_BUFFER_STRATEGY)))) {
                buffer = getChannel().map(FileChannel.MapMode.READ_ONLY, position, size);
            }
            else {
                if (BufferStrategy.DIRECT.equals(BufferStrategy.getBufferStrategy(Configurator.getProperty(KEY_BUFFER_STRATEGY)))) {
                    buffer = ByteBuffer.allocateDirect(size);
                }
                else if (BufferStrategy.DEFAULT.equals(BufferStrategy.getBufferStrategy(Configurator.getProperty(KEY_BUFFER_STRATEGY)))) {
                    buffer = ByteBuffer.allocate(size);
                }
                else {
                    throw new IllegalArgumentException("Unrecognised buffer strategy: " + Configurator.getProperty(KEY_BUFFER_STRATEGY));
                }
                getChannel().position(position);
                getChannel().read(buffer);
                buffer.flip();
            }
        }
        catch (IOException ioe) {
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
    private long[] getMessagePositions() throws IOException {
        if (messagePositions == null) {
            List posList = new ArrayList();

            // debugging..
            log.debug("Channel size [" + getChannel().size() + "] bytes");

            int bufferSize = (int) Math.min(getChannel().size(),
                    DEFAULT_BUFFER_SIZE);

            // read mbox file to determine the message positions..
            CharSequence cs = null;

            ByteBuffer buffer = read(0, bufferSize);

            cs = decoder.decode(buffer);

            // debugging..
            log.debug("Buffer [" + cs + "]");

            // check that first message is correct..
//            if (VALID_MBOX_PATTERN.matcher(cs).matches()) {
//                // debugging..
//                log.debug("Matched first message..");
//
//                posList.add(new Long(0));
//            }

            // indicates the offset of the current buffer..
            long offset = 0;

            for (;;) {
                // Matcher matcher = fromPattern.matcher(buffer.asCharBuffer());
                Matcher matcher = FROM__LINE_PATTERN.matcher(cs);

                while (matcher.find()) {
                    // debugging..
                    log.debug("Found match at [" + (offset + matcher.start()) + "]");

                    // add one (1) to position to account for newline..
//                    int fromPrefixOffset = matcher.group().length() - FROM__PREFIX.length();
//                    Matcher nlMatcher = Pattern.compile("^\\s*").matcher(matcher.group());
//                    if (nlMatcher.find()) {
//                        posList.add(new Long(offset + matcher.start() + nlMatcher.end())); // + fromPrefixOffset));
//                    }
//                    else {
//                        posList.add(new Long(offset + matcher.start())); // + fromPrefixOffset));
//                    }
                    posList.add(new Long(offset + matcher.start())); // + fromPrefixOffset));
                }

                // if (offset + cb.limit() >= getChannel().size()) {
                if (offset + bufferSize >= getChannel().size()) {
                    break;
                }
                else {
                    // preserve the end of the buffer as it may contain
                    // part of a From_ pattern..
                    // offset += cb.limit() - FROM__PATTERN.length();
                    offset += bufferSize - FROM__PREFIX.length() - 2;

                    bufferSize = (int) Math.min(getChannel().size() - offset,
                            DEFAULT_BUFFER_SIZE);

                    // buffer = readBytes(offset, (int) bufferSize);
                    // buffer.clear();
                    buffer = read(offset, bufferSize);
                    // buffer.flip();
                    cs = decoder.decode(buffer);
                }
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
     * Returns the total number of messages in the mbox file.
     *
     * @return an int
     */
    public final int getMessageCount() throws IOException {
        return getMessagePositions().length;
    }

    /**
     * Returns the message cache.
     */
    private Cache getMessageCache() {
        CacheManager manager = CacheManager.create();
        String cacheName = "mstor.mbox." + file.getAbsolutePath().hashCode();
        if (manager.getCache(cacheName) == null) {
            manager.addCache(cacheName);
        }
        return manager.getCache(cacheName);
    }

    /**
     * Opens an input stream to the specified message data.
     *
     * @param index the index of the message to open a stream to
     * @return an input stream
     */
    public final InputStream getMessageAsStream(final int index)
            throws IOException {
        
        ByteBuffer buffer = null;
        
        if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_CACHE_BUFFERS)) {
            Element cacheElement = getMessageCache().get(new Integer(index));
            if (cacheElement != null) {
                buffer = (ByteBuffer) cacheElement.getObjectValue();
            }
        }

        if (buffer == null) {
            long position = getMessagePositions()[index];
            long size;

            if (index < getMessagePositions().length - 1) {
                size = getMessagePositions()[index + 1]
                        - getMessagePositions()[index];
            }
            else {
                size = getChannel().size() - getMessagePositions()[index];
            }
            
            buffer = read(position, (int) size);
            
            if (CapabilityHints.isHintEnabled(CapabilityHints.KEY_MBOX_CACHE_BUFFERS)) {
                // add buffer to cache..
                getMessageCache().put(new Element(new Integer(index), buffer));
            }
            
            // adjust position to exclude the From_ line..
            /*
            Matcher matcher = null;
            if (index == 0) {
                matcher = INITIAL_FROM__LINE_PATTERN.matcher(decoder.decode(buffer));
                if (matcher.find()) {
                    buffer.rewind();
                    buffer.position(buffer.position() + matcher.end());
                    buffer.mark();
                }
            }
            else {
                matcher = FROM__LINE_PATTERN.matcher(decoder.decode(buffer));
                if (matcher.find()) {
                    buffer.rewind();
                    buffer.position(buffer.position() + matcher.end() + 1);
                    buffer.mark();
                }
            }
            */
        }
        
        // rewind for a re-read of buffer data..
        /*
        try {
            buffer.reset();
        }
        catch (InvalidMarkException ime) {
            buffer.rewind();
        }
        return new BufferInputStream(buffer);
        */
        return new MessageInputStream(buffer);
    }

    /**
     * Convenience method that returns a message as a byte array containing the data for the message
     * at the specified index.
     *
     * @param index the index of the message to retrieve
     * @return a byte array
     */
    public final byte[] getMessage(final int index) throws IOException {
        /*
         * long position = getMessagePositions()[index]; long size; if (index <
         * getMessagePositions().length - 1) { size = getMessagePositions()[index + 1] -
         * getMessagePositions()[index]; } else { size = getChannel().size() -
         * getMessagePositions()[index]; } ByteBuffer buffer = readBytes(position, (int) size);
         * CharSequence message = decoder.decode(buffer); // remove extraneous ">" characters added
         * to maintain integrity of mbox file.. // Pattern maskedFromPattern =
         * Pattern.compile("\\n>From "); Pattern maskedFromPattern =
         * Pattern.compile(MASKED_FROM__PATTERN); //Matcher matcher =
         * fromPattern.matcher(buffer.asCharBuffer()); Matcher matcher =
         * maskedFromPattern.matcher(message); if (matcher.find()) { matcher.reset(); message =
         * matcher.replaceAll(FROM__PATTERN); } return message;
         */
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
     */
    public final void appendMessage(final byte[] message) throws IOException {
//        long newMessagePosition = getChannel().size();

        // synchronise purging and appending to avoid message loss..
        synchronized (file) {
            
            MessageAppender appender = new MessageAppender(getChannel());
            long newMessagePosition = appender.appendMessage(message);
    
            // update message positions..
            if (messagePositions != null) {
                long[] newMessagePositions = new long[messagePositions.length + 1];
                System.arraycopy(messagePositions, 0, newMessagePositions, 0,
                        messagePositions.length);
                newMessagePositions[newMessagePositions.length - 1] = newMessagePosition;
                messagePositions = newMessagePositions;
            }
        }
        
        // clear cache..
        getMessageCache().removeAll();
    }

    /**
     * Purge the specified messages from the file.
     *
     * @param msgnums the indices of the messages to purge
     */
    public final void purge(final int[] msgnums) throws IOException {
        // create a new mailbox file..
        // Create the new file in the temp directory which is always read/write
        File newFile = new File(System.getProperty("java.io.tmpdir"),
                file.getName() + TEMP_FILE_EXTENSION);

        FileOutputStream newOut = new FileOutputStream(newFile);
        FileChannel newChannel = newOut.getChannel();
        MessageAppender appender = new MessageAppender(newChannel);

        // synchronise purging and appending to avoid message loss..
        synchronized (file) {
            
            loop: for (int i = 0; i < getMessagePositions().length; i++) {
                for (int j = 0; j < msgnums.length; j++) {
                    if (msgnums[j] == i) {
                        // don't save message to file if in purge list..
                        continue loop;
                    }
                }
                // append current message to new file..
                appender.appendMessage(getMessage(i));
            }
            // ensure new file is properly written..
            newOut.close();

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
            try {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(dest);
                int length;
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                while ((length = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
                
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
     * @throws IOException
     */
    public final void close() throws IOException {
//        if (messageCache != null) {
//            messageCache.clear();
//        }
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
     */
    public static boolean isValid(final File file) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            // check that first message is correct..
            String line = reader.readLine();

            return line == null || VALID_MBOX_PATTERN.matcher(line).matches();
        }
        catch (Exception e) {
            Log log = LogFactory.getLog(MboxFile.class);
            log.info("Not a valid mbox file [" + file + "]", e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ioe) {
                    Log log = LogFactory.getLog(MboxFile.class);
                    log.info("Error closing stream [" + file + "]", ioe);
                }
            }
        }

        return false;
    }
}
