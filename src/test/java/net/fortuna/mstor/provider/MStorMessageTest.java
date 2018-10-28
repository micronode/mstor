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
package net.fortuna.mstor.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import java.util.Enumeration;

/**
 * A test case for MStorMessage.
 *
 * @author benfortuna
 * 
 * <pre>
 * $Id$
 *
 * Created: [14/07/2004]
 * </pre>
 * 
 */
public class MStorMessageTest extends AbstractMStorTest {

    private static Log log = LogFactory.getLog(MStorFolderTest.class);

//    private String testFolder;

    /**
     * Default constructor.
     */
    public MStorMessageTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method, lifecycle, username, password);
    }

    /*
     * Class under test for Date getReceivedDate()
     */
    public final void testGetReceivedDate() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);
    
            Message message;
    
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                message = folder.getMessage(i);
    
                log.info("Message [" + i + "] received date ["
                        + message.getReceivedDate() + "]");
            }
    
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testSetFlag() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Flags flags = new Flags();
            flags.add(Flags.Flag.SEEN);
            flags.add(Flags.Flag.RECENT);
            flags.add("user 1");
            flags.add("user 2");
    
            Message message;
    
            for (int i = 1; i <= folder.getMessageCount() && i <= 10; i++) {
                message = folder.getMessage(i);
                message.setFlag(Flags.Flag.SEEN, false);
                message.setFlag(Flags.Flag.ANSWERED, true);
                message.setFlag(Flags.Flag.USER, true);
                message.setFlags(flags, true);
                folder.close(false);
                
                folder = store.getFolder(folderNames[n]);
                folder.open(Folder.READ_ONLY);
                message = folder.getMessage(i);
                assertTrue(message.getFlags().contains(Flags.Flag.SEEN));
                assertTrue(message.getFlags().contains(Flags.Flag.RECENT));
                assertTrue(message.getFlags().contains("user 1"));
                assertTrue(message.getFlags().contains("user 2"));
                assertFalse(message.getFlags().contains("user 3"));
            }
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    public final void testGetFlags() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            Message message;
    
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                message = folder.getMessage(i);
    
                log.info("System Flags ["
                        + message.getFlags().getSystemFlags().length + "]");
                log.info("User Flags [" + message.getFlags().getUserFlags().length
                        + "]");
            }
            folder.close(false);
        }
    }

    /**
     * @throws MessagingException
     */
    @SuppressWarnings("unchecked")
    public void testGetAllHeaders() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_ONLY);

            for (int i = 1; i < folder.getMessageCount(); i++) {
                Message message = folder.getMessage(i);
                for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
                    Header header = e.nextElement();
                    assertFalse("From_ line returned as header [" + folder.getFullName() + "]", header.getName()
                            .startsWith("From "));
                }
            }
            folder.close(false);
        }
    }

    /**
     * @return
     * @throws IOException
     */
    /*
    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();

        File[] samples = getSamples();
        for (int i = 0; i < samples.length; i++) {
            log.info("Sample [" + samples[i] + "]");

            suite.addTest(new MStorMessageTest("testGetReceivedDate",
                    samples[i]));
            suite.addTest(new MStorMessageTest("testSetFlag", samples[i]));
            suite.addTest(new MStorMessageTest("testGetFlags", samples[i]));
            suite
                    .addTest(new MStorMessageTest("testGetAllHeaders",
                            samples[i]));
        }
        return suite;
    }
    */
}
