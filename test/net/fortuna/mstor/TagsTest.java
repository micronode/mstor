/*
 * $Id$
 *
 * Created on 6/05/2006
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
package net.fortuna.mstor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.Flags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * Unit tests for {@link net.fortuna.mstor.Tags}.
 * @author Ben Fortuna
 */
public class TagsTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(TagsTest.class);
    
    private Flags flags;
    
    private Tags tags;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        flags = new Flags();
        tags = new Tags(flags);
        tags.add("Work");
        tags.add("Family");
        tags.add("Friends");
        tags.add("Humour");
    }
    
    /*
     * Test method for 'net.fortuna.mstor.Tags.size()'
     */
    public void testSize() {
        assertEquals(4, tags.size());
        assertEquals(4, flags.getUserFlags().length);
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.isEmpty()'
     */
    public void testIsEmpty() {
        tags.clear();
        assertTrue(tags.isEmpty());
        assertTrue(flags.getUserFlags().length == 0);
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.contains(Object)'
     */
    public void testContains() {
        assertTrue(tags.contains("Work"));
        assertTrue(Arrays.asList(flags.getUserFlags()).contains(Tags.TAG_PREFIX + "Work"));
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.iterator()'
     */
    public void testIterator() {
        Iterator i = tags.iterator();
        assertTrue(i.hasNext());
        for (;i.hasNext();) {
            LOG.info("Tag: " + i.next());
        }
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.toArray()'
     */
    public void testToArray() {
        Object[] array = tags.toArray();
        assertEquals(array.length, tags.size());
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.toArray(T[]) <T>'
     */
    public void testToArrayTArray() {
        String[] array = (String[]) tags.toArray(new String[tags.size()]);
        assertEquals(array.length, tags.size());
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.add(String)'
     */
    public void testAdd() {
        tags.add("mstor");
        assertTrue(tags.contains("mstor"));
        assertTrue(Arrays.asList(flags.getUserFlags()).contains(Tags.TAG_PREFIX + "mstor"));
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.remove(Object)'
     */
    public void testRemove() {
        tags.remove("Work");
        assertFalse(tags.contains("Work"));
        assertFalse(Arrays.asList(flags.getUserFlags()).contains(Tags.TAG_PREFIX + "Work"));
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.containsAll(Collection<?>)'
     */
    public void testContainsAll() {
        Set set = new HashSet();
        set.add("Friends");
        set.add("Family");
        assertTrue(tags.containsAll(set));
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.addAll(Collection<? extends String>)'
     */
    public void testAddAll() {
        Set set = new HashSet();
        set.add("TestTag 1");
        set.add("TestTag 2");
        set.add("TestTag 3");
        tags.addAll(set);
        assertTrue(tags.containsAll(set));
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.retainAll(Collection<?>)'
     */
    public void testRetainAll() {
        Set set = new HashSet();
        set.add("Family");
        set.add("Friends");
        tags.retainAll(set);
        assertTrue(tags.containsAll(set));
        assertEquals(set.size(), tags.size());
        assertEquals(set.size(), flags.getUserFlags().length);
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.removeAll(Collection<?>)'
     */
    public void testRemoveAll() {
        Set set = new HashSet();
        set.add("Family");
        set.add("Friends");
        tags.removeAll(set);
        for (Iterator i = set.iterator(); i.hasNext();) {
            String tag = (String) i.next();
            assertFalse(tags.contains(tag));
            assertFalse(Arrays.asList(flags.getUserFlags()).contains(Tags.TAG_PREFIX + tag));
        }
    }

    /*
     * Test method for 'net.fortuna.mstor.Tags.clear()'
     */
    public void testClear() {
        tags.clear();
        assertTrue(tags.isEmpty());
        assertTrue(flags.getUserFlags().length == 0);
    }
}
