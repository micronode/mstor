/*
 * $Id$
 *
 * Created on 14/05/2006
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.search;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.search.AddressStringTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;

import junit.framework.TestCase;
import net.fortuna.mstor.StoreLifecycle;
import net.fortuna.mstor.tag.Taggable;
import net.fortuna.mstor.tag.Tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Unit tests for {@link net.fortuna.mstor.search.TagsTerm}.
 * 
 * @author Ben Fortuna
 */
public class TagsTermTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(TagsTermTest.class);

    private String tag = "Test 1";

//    private Folder inbox;

//    private String testFolder;

    private StoreLifecycle lifecycle;
    
    private Store store;
    
    private String username;
    
    private String password;
    
    private String[] folderNames;

    /**
     * Default constructor.
     */
    public TagsTermTest(String method, StoreLifecycle lifecycle,
            String username, String password) {
        
        super(method);
        this.lifecycle = lifecycle;
        this.username = username;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        lifecycle.startup();
        store = lifecycle.getStore();
        store.connect(username, password);
        
        List folderList = new ArrayList();
        Folder[] folders = store.getDefaultFolder().list();
        for (int i = 0; i < folders.length; i++) {
            folderList.add(folders[i].getName());
        }
        folderNames = (String[]) folderList.toArray(new String[folderList.size()]);

//        inbox = store.getDefaultFolder().getFolder(testFolder);
//        inbox.open(Folder.READ_WRITE);
//
//        Taggable message = (Taggable) inbox.getMessage(1);
//        message.addTag(tag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        store.close();
        lifecycle.shutdown();

        super.tearDown();

        // Taggable message = (Taggable) inbox.getMessage(2);
        // message.removeTag(tag);
    }

    /**
     * A unit test that tags a message and uses a search term to identify it.
     */
    public final void testTagMessage() throws MessagingException {
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_WRITE);
            
            Taggable message = (Taggable) folder.getMessage(1);
            message.addTag(tag);
            
            assertTrue(message.getTags().contains(tag));
    
            Tags searchTags = new Tags();
            searchTags.add(tag);
            TagsTerm term = new TagsTerm(searchTags);
    
            Message[] messages = folder.search(term);
            assertEquals(1, messages.length);
            assertEquals(1, messages[0].getMessageNumber());
            
            folder.close(false);
        }
    }

    /**
     * Test castor marshalling.
     */
    /*
     * public final void testPersistTagTerm() throws IOException,
     * MappingException, ValidationException, MarshalException {
     * 
     * Tags tags = new Tags(); tags.add(tag);
     * 
     * Mapping mapping = new Mapping(); // 1. Load the mapping information from
     * the file
     * mapping.loadMapping("source/net/fortuna/mstor/data/xml/mapping.xml");
     *  /* // 2. Unmarshal the data Unmarshaller unmar = new
     * Unmarshaller(mapping); MyOrder order = (MyOrder)unmar.unmarshal(new
     * InputSource(new FileReader("order.xml")));
     *  // 3. Do some processing on the data float total =
     * order.getTotalPrice(); System.out.println("Order total price = " +
     * total); order.setTotal(total);
     * 
     *  // 4. marshal the data with the total price back and print the XML in
     * the console Marshaller marshaller = new Marshaller(new
     * OutputStreamWriter(System.out)); marshaller.setMapping(mapping);
     * marshaller.marshal(tags); }
     */

    /*
     * public void testXmlEncodeTagTerm() { Tags tags = new Tags();
     * tags.add(tag);
     * 
     * TagsTerm term = new TagsTerm(tags);
     * 
     * XMLEncoder encoder = new XMLEncoder(System.out);
     * encoder.writeObject(term); }
     */

    /**
     * @throws MessagingException
     */
    public void testXStreamTagTerm() throws MessagingException {
        Tags tags = new Tags();
        tags.add(tag);

        TagsTerm term = new TagsTerm(tags);
        AddressStringTerm aterm = new FromStringTerm("fortuna@mstor.com");
        OrTerm orterm = new OrTerm(term, aterm);

        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(orterm);

        LOG.info(xml);

        OrTerm decoded = (OrTerm) xstream.fromXML(xml);

        // assertEquals(orterm.getTerms(), decoded.getTerms());
        
        for (int n = 0; n < folderNames.length; n++) {
            Folder folder = store.getFolder(folderNames[n]);
            folder.open(Folder.READ_WRITE);

            Taggable message = (Taggable) folder.getMessage(1);
            message.addTag(tag);
            
            Message[] messages = folder.search(decoded);
            assertEquals(1, messages.length);
            assertEquals(1, messages[0].getMessageNumber());
    
            TagsTerm decodedTags = (TagsTerm) decoded.getTerms()[0];
            assertTrue(decodedTags.getTags().contains(tag));
            
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
            LOG.info("Sample [" + samples[i] + "]");

            suite.addTest(new TagsTermTest("testTagMessage", samples[i]));
            suite.addTest(new TagsTermTest("testXStreamTagTerm", samples[i]));
        }
        return suite;
    }
    */
}
