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
        File f = new File("f:/development/workspace/mstor/etc/samples/samples.mbx");
//        File f = new File("c:/temp/mail/test/Copy/Copy2");

        mbox = new MboxFile(f, MboxFile.READ_WRITE);
    }

    public void testGetMessageCount() throws IOException {
        assertTrue(mbox.getMessageCount() >= 0);

        log.info("Message count: " + mbox.getMessageCount());
    }

    public void testGetMessage() throws IOException {
        for (int i=0; i<mbox.getMessageCount(); i++) {
//        for (int i=0; i<3; i++) {
            CharSequence buffer = mbox.getMessage(i);

            assertNotNull(buffer);

            log.info("Message [" + i + "]\n=================\n" + buffer);
        }
    }
    
    public void testPurge() throws IOException {
        mbox.purge(new int[] {1});
    }
}
