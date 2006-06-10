/*
 * $Id$
 *
 * Created: [6/07/2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.mstor.data;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.mstor.util.CapabilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit tests for {@link net.fortuna.mstor.data.MboxFile}.
 * @author Ben Fortuna
 */
public class MboxFileTest extends TestCase {

    private static Log log = LogFactory.getLog(MboxFileTest.class);

    private MboxFile mbox;

    /**
     * @param method
     */
    public MboxFileTest(String method) {
        super(method);
    }
    
    /**
     * @param bufferStrategy
     * @param cacheStrategy
     */
    public MboxFileTest(String method, String bufferStrategy, String cacheStrategy) {
        super(method);
        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_BUFFER_STRATEGY,
                bufferStrategy);
        CapabilityHints.setHint(CapabilityHints.KEY_MBOX_CACHE_BUFFERS,
                cacheStrategy);
    }
    
    /*
     * @see TestCase#setUp()
     */
    protected final void setUp() throws Exception {
        super.setUp();
        File f = new File("etc/samples/MboxFile/Inbox");
        mbox = new MboxFile(f, MboxFile.READ_WRITE);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        mbox.close();
    }

    /**
     * @throws IOException
     */
    public final void testGetMessageCount() throws IOException {
        assertTrue(mbox.getMessageCount() >= 0);

        log.info("Message count: " + mbox.getMessageCount());
    }

    /**
     * @throws IOException
     */
    public final void testGetMessage() throws IOException {
        for (int i = 0; i < mbox.getMessageCount(); i++) {
            // for (int i=0; i<3; i++) {
            byte[] buffer = mbox.getMessage(i);

            assertNotNull(buffer);

            log.info("Message [" + i + "]\n=================\n"
                    + new String(buffer));
        }
    }

    /**
     * Removes the second message from the mbox file.
     * 
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
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new MboxFileTest("testGetMessage",
                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DEFAULT,
                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED));
        suite.addTest(new MboxFileTest("testGetMessage",
                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DIRECT,
                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED));
        suite.addTest(new MboxFileTest("testGetMessage",
                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_MAPPED,
                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED));
        suite.addTest(new MboxFileTest("testGetMessage",
                CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DEFAULT,
                CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_ENABLED));
        suite.addTest(new MboxFileTest("testGetMessageCount"));
        suite.addTest(new MboxFileTest("testPurge"));
        return suite;
    }
}
