/*
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.mstor.data.MboxFile.BufferStrategy;
import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit tests for {@link net.fortuna.mstor.data.MboxFile}.
 * @author Ben Fortuna
 */
public class MboxFileTest extends TestCase {

    private static Log log = LogFactory.getLog(MboxFileTest.class);

    private String filename;

    private File testFile;

    private MboxFile mbox;

    private BufferStrategy bufferStrategy;
    
    private boolean cacheEnabled;
    
    private boolean relaxedParsingEnabled;
    
    /**
     * @param method
     */
    public MboxFileTest(String method, String filename) {
        this(method, filename, null, false, false);
    }
    
    /**
     * @param method
     * @param filename
     * @param relaxedParsingEnabled
     */
    public MboxFileTest(String method, String filename, boolean relaxedParsingEnabled) {
        this(method, filename, null, false, relaxedParsingEnabled);
        this.filename = filename;
    }

    /**
     * @param method
     * @param filename
     * @param bufferStrategy
     * @param cacheEnabled
     */
    public MboxFileTest(String method, String filename, BufferStrategy bufferStrategy,
            boolean cacheEnabled) {
        this(method, filename, bufferStrategy, cacheEnabled, false);
    }
    
    /**
     * @param bufferStrategy
     * @param cacheStrategy
     */
    public MboxFileTest(String method, String filename, BufferStrategy bufferStrategy,
            boolean cacheEnabled, boolean relaxedParsingEnabled) {
        super(method);
        this.filename = filename;
        this.bufferStrategy = bufferStrategy;
        this.cacheEnabled = cacheEnabled;
        this.relaxedParsingEnabled = relaxedParsingEnabled;
    }

    /*
     * @see TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        super.setUp();
        // File f = new File("etc/samples/mailboxes/MboxFile/Inbox");
        testFile = createTestHierarchy(new File(filename));
        mbox = new MboxFile(testFile, MboxFile.READ_WRITE);
        
        if (bufferStrategy != null) {
            System.setProperty(MboxFile.KEY_BUFFER_STRATEGY, bufferStrategy.getName());
        }
        CapabilityHints.setHintEnabled(CapabilityHints.KEY_MBOX_CACHE_BUFFERS, cacheEnabled);
        CapabilityHints.setHintEnabled(CapabilityHints.KEY_MBOX_RELAXED_PARSING, relaxedParsingEnabled);
    }

    /**
     * @param url
     * @return
     */
    private File createTestHierarchy(File source) throws IOException {
        File testDir = new File(System.getProperty("java.io.tmpdir"),
                "mstor_test" + File.separator + super.getName() + File.separator + source.getName());
        FileUtils.copyFileToDirectory(source, testDir);
        return new File(testDir, source.getName());
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        mbox.close();
        testFile.delete();
    }

    /**
     * @throws IOException
     */
    public final void testGetMessageCount() throws IOException {
//        assertTrue(mbox.getMessageCount() >= 0);
        if (testFile.getName().equals("contenttype-semis.mbox")) {
            assertEquals(1, mbox.getMessageCount());
        }
        else if (testFile.getName().equals("imagined.mbox")) {
            if (relaxedParsingEnabled) {
                assertEquals(293, mbox.getMessageCount());
            }
            else {
                assertEquals(223, mbox.getMessageCount());
            }
        }
        else if (testFile.getName().equals("parseexception.mbox")) {
            assertEquals(1, mbox.getMessageCount());
        }
        else if (testFile.getName().equals("samples.mbx")) {
            if (relaxedParsingEnabled) {
                assertEquals(4, mbox.getMessageCount());
            }
            else {
                assertEquals(2, mbox.getMessageCount());
            }
        }
        else if (testFile.getName().equals("subject-0x1f.mbox")) {
            assertEquals(1, mbox.getMessageCount());
        }
        else if (testFile.getName().equals("received-0xc.mbox")) {
            assertEquals(1, mbox.getMessageCount());
        }
        else if (testFile.getAbsolutePath().endsWith("mail.internode.on.net/Inbox")) {
            assertEquals(28, mbox.getMessageCount());
        }
        else if (testFile.getAbsolutePath().endsWith("mail.modularity.net.au/Inbox")) {
            assertEquals(139, mbox.getMessageCount());
        }
        else if (testFile.getAbsolutePath().endsWith("pop.gmail.com/Inbox")) {
            assertEquals(1240, mbox.getMessageCount());
        }
        else if (testFile.getAbsolutePath().endsWith("pop.hotpop.com/Inbox")) {
            assertEquals(178, mbox.getMessageCount());
        }
        else if (testFile.getName().equals("error.mbox")) {
            if (relaxedParsingEnabled) {
                assertEquals(3, mbox.getMessageCount());
            }
            else {
                assertEquals(2, mbox.getMessageCount());
            }
        }

//        log.info("Message count: " + mbox.getMessageCount());
    }

    /**
     * @throws IOException
     */
    public final void testGetMessage() throws IOException {
        for (int i = 0; i < mbox.getMessageCount(); i++) {
            // for (int i=0; i<3; i++) {
            byte[] buffer = mbox.getMessage(i);

            assertNotNull(buffer);

            if (log.isDebugEnabled()) {
                log.debug("Message [" + i + "]\n=================\n"
                        + new String(buffer));
            }
        }
    }

    /**
     * Removes the second message from the mbox file.
     * @throws IOException
     */
    public final void testPurge() throws IOException {
        mbox.purge(new int[] {1});
    }

    /*
     * public void testRafChannel() throws IOException { File f = new
     * File("c:/temp/mstor_test/Inbox.tmp"); RandomAccessFile raf = new
     * RandomAccessFile(f, MboxFile.READ_WRITE); FileChannel channel =
     * raf.getChannel();
     * 
     * log.info("Channel size: " + channel.size());
     * 
     * ByteBuffer buffer = ByteBuffer.allocateDirect((int) channel.size());
     * channel.position(0); channel.read(buffer); buffer.flip();
     * 
     * CharSequence cs =
     * Charset.forName("ISO-8859-1").newDecoder().decode(buffer);
     * 
     * channel.close();
     * 
     * File tempFile = new File(f.getParent(), f.getName() + "." +
     * System.currentTimeMillis()); // remove any existing temporary files.. if
     * (tempFile.exists()) { tempFile.delete(); } f.renameTo(tempFile); }
     */

    /**
     * Overridden to return the current mbox file under test.
     */
    public final String getName() {
        return super.getName() + " [" + filename + "]";
    }

    /**
     * @return
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(MboxFileTest.class.getSimpleName());
        
//        suite.addTest(new MboxFileTest("testGetMessageCount", "/tmp/Inbox"));
        File[] testFiles = new File("etc/samples/mailboxes")
                .listFiles((FileFilter) new NotFileFilter(
                        DirectoryFileFilter.INSTANCE));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Sample [" + testFiles[i] + "]");
            suite.addTest(new MboxFileTest("testGetMessage", testFiles[i]
                    .getPath(),
                    BufferStrategy.DEFAULT,
                    false));
            suite.addTest(new MboxFileTest("testGetMessage", testFiles[i]
                    .getPath(),
                    BufferStrategy.DIRECT,
                    false));
            suite.addTest(new MboxFileTest("testGetMessage", testFiles[i]
                    .getPath(),
                    BufferStrategy.MAPPED,
                    false));
            suite.addTest(new MboxFileTest("testGetMessage", testFiles[i]
                    .getPath(),
                    BufferStrategy.DEFAULT,
                    true));
            suite.addTest(new MboxFileTest("testGetMessageCount", testFiles[i]
                    .getPath()));
            suite.addTest(new MboxFileTest("testGetMessageCount", testFiles[i].getPath(), true));
            suite
                    .addTest(new MboxFileTest("testPurge", testFiles[i]
                            .getPath()));
        }
        
        // real mailboxes..
//        suite.addTest(new MboxFileTest("testGetMessageCount",
//                "/home/fortuna/.mozilla-thunderbird/djm89os8.default/Mail/mail.internode.on.net/Inbox"));
//        suite.addTest(new MboxFileTest("testGetMessageCount",
//                "/home/fortuna/.mozilla-thunderbird/djm89os8.default/Mail/mail.modularity.net.au/Inbox"));
//        suite.addTest(new MboxFileTest("testGetMessageCount",
//                "/home/fortuna/.mozilla-thunderbird/djm89os8.default/Mail/pop.gmail.com/Inbox"));
//        suite.addTest(new MboxFileTest("testGetMessageCount",
//                "/home/fortuna/.mozilla-thunderbird/djm89os8.default/Mail/pop.hotpop.com/Inbox"));
        
        return suite;
    }
}
