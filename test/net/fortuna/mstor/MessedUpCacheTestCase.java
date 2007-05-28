package net.fortuna.mstor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

public class MessedUpCacheTestCase extends TestCase {
    
    private static Random random;
    
    private Store store;

    public void setUp() throws MessagingException, MalformedURLException {

        File testDir = new File(System.getProperty("java.io.tmpdir"),
                "mstor_test" + File.separator + getName());
        
        new File(testDir, "INBOX").delete();
        new File(testDir, "INBOX.emf").delete();
        
        Properties mailSessionProps  = new Properties();
        Session mailSession = Session.getDefaultInstance(mailSessionProps);
        String destination="mstor://"+(testDir.toURL());
        store=mailSession.getStore(new URLName(destination));
        store.connect();
    }
    
    public static MimeMessage generateMessage() throws MessagingException {
        MimeMessage mm = new MimeMessage((Session) null);
        int r = getRandom().nextInt() % 100000;
        int r2 = getRandom().nextInt() % 100000;
        mm.setSubject("good news" + r);
        mm.setFrom(new InternetAddress("user" + r + "@localhost"));
        mm.setRecipients(Message.RecipientType.TO,
                new InternetAddress[] { new InternetAddress("user" + r2
                        + "@localhost") });
        String text = "Hello User" + r2
                + "!\n\nhave a nice holiday.\r\n\r\ngreetings,\nUser" + r
                + "\n";
        mm.setText(text);
        return mm;
    }
    
    protected static synchronized Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;

    }
    
    public void testMessedUpCache() throws MessagingException {
        Folder inbox=store.getFolder("INBOX");
        inbox.create(Folder.HOLDS_MESSAGES);
        inbox.open(Folder.READ_WRITE);
        inbox.appendMessages(new MimeMessage[] {generateMessage(),generateMessage(),generateMessage()});
        inbox.getMessage(1).setFlag(Flags.Flag.DELETED, true);
        assertEquals(3, inbox.getMessageCount());
        inbox.close(true);
        inbox.open(Folder.READ_WRITE);
        // this is correct
        assertEquals(2, inbox.getMessageCount());
        inbox.close(true);
        inbox.open(Folder.READ_WRITE);
        // there should still be 2, nothing has changed
        assertEquals(2, inbox.getMessageCount());
        inbox.close(true);
    }

}
