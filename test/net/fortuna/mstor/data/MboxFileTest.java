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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Type description.
 * @author benfortuna
 */
public class MboxFileTest extends TestCase {

    private static Log log = LogFactory.getLog(MboxFileTest.class);

    private MboxFile mbox;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        //File f = new File("d:/Mail/Eudora/In.mbx");
//        File f = new File("c:/temp/mail/store/Inbox");
//        File f = new File("f:/development/workspace/mstor/etc/samples/samples.mbx");
//        File f = new File("c:/temp/mstor_test/samples.mbx");
        File f = new File("c:/temp/mstor_test/Inbox");

        mbox = new MboxFile(f, MboxFile.READ_WRITE);
    }

    public void testGetMessageCount() throws IOException {
        assertTrue(mbox.getMessageCount() >= 0);

        log.info("Message count: " + mbox.getMessageCount());
        
        mbox.close();
    }

    public void testGetMessage() throws IOException {
        for (int i=0; i< mbox.getMessageCount(); i++) {
//        for (int i=0; i<3; i++) {
            byte[] buffer = mbox.getMessage(i);

            assertNotNull(buffer);

            log.info("Message [" + i + "]\n=================\n" + new String(buffer));
        }
        mbox.close();
    }

    /**
     * Removes the second message from the mbox file.
     * @throws IOException
     */
    public void testPurge() throws IOException {
        mbox.purge(new int[] {1});
        mbox.close();
    }
    
    /*
    public void testRafChannel() throws IOException {
    	File f = new File("c:/temp/mstor_test/Inbox.tmp");
    	RandomAccessFile raf = new RandomAccessFile(f, MboxFile.READ_WRITE);
    	FileChannel channel = raf.getChannel();
    	
    	log.info("Channel size: " + channel.size());
    	
        ByteBuffer buffer = ByteBuffer.allocateDirect((int) channel.size());
        channel.position(0);
        channel.read(buffer);
        buffer.flip();

        CharSequence cs = Charset.forName("ISO-8859-1").newDecoder().decode(buffer);

    	channel.close();
    	
        File tempFile = new File(f.getParent(), f.getName() + "." + System.currentTimeMillis());
        // remove any existing temporary files..
        if (tempFile.exists()) {
            tempFile.delete();
        }
    	f.renameTo(tempFile);
    }
    */
}
