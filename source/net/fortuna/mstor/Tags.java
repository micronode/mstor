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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.Flags;

/**
 * @author Ben Fortuna
 */
public class Tags implements Set<String>, Serializable {

    private static final long serialVersionUID = -4185780688194955112L;

    protected static final String TAG_PREFIX = "tag_";
    
    private Flags flags;
    
    /**
     * Default constructor.
     */
    public Tags() {
        this(new Flags());
    }
    
    /**
     * @param flags
     */
    public Tags(Flags flags) {
        this.flags = flags;
    }
    
    /* (non-Javadoc)
     * @see java.util.Set#size()
     */
    public int size() {
        int tagCount = 0;
        String[] userFlags = flags.getUserFlags();
        for (int i = 0; i < userFlags.length; i++) {
            if (userFlags[i].startsWith(TAG_PREFIX)) {
                tagCount++;
            }
        }
        return tagCount;
    }

    /* (non-Javadoc)
     * @see java.util.Set#isEmpty()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /* (non-Javadoc)
     * @see java.util.Set#contains(java.lang.Object)
     */
    public boolean contains(Object arg0) {
        if (arg0 instanceof String) {
            String tag = TAG_PREFIX + arg0;
            String[] userFlags = flags.getUserFlags();
            for (int i = 0; i < userFlags.length; i++) {
                if (userFlags[i].equals(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return
     */
    private Set<String> getTagSet() {
        Set<String> tags = new HashSet<String>(); 
        String[] userFlags = flags.getUserFlags();
        for (int i = 0; i < userFlags.length; i++) {
            if (userFlags[i].startsWith(TAG_PREFIX)) {
                tags.add(userFlags[i].split(TAG_PREFIX)[1]);
            }
        }
        return tags;
    }
    
    /* (non-Javadoc)
     * @see java.util.Set#iterator()
     */
    public Iterator<String> iterator() {
        return getTagSet().iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Set#toArray()
     */
    public Object[] toArray() {
        return getTagSet().toArray();
    }

    /* (non-Javadoc)
     * @see java.util.Set#toArray(T[])
     */
    public <T> T[] toArray(T[] arg0) {
        return getTagSet().toArray(arg0);
    }

    /* (non-Javadoc)
     * @see java.util.Set#add(E)
     */
    public boolean add(String arg0) {
        String tag = TAG_PREFIX + arg0;
        if (!flags.contains(tag)) {
            flags.add(tag);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Set#remove(java.lang.Object)
     */
    public boolean remove(Object arg0) {
        if (arg0 instanceof String) {
            flags.remove(TAG_PREFIX + (String) arg0);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> arg0) {
        for (Iterator i = arg0.iterator(); i.hasNext();) {
            if (!contains(i.next())) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends String> arg0) {
        for (Iterator<? extends String> i = arg0.iterator(); i.hasNext();) {
            if (!add(i.next())) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> arg0) {
        for (Iterator<String> i = iterator(); i.hasNext();) {
            String tag = i.next();
            if (!arg0.contains(tag)) {
                if (!remove(tag)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> arg0) {
        for (Iterator i = arg0.iterator(); i.hasNext();) {
            if (!remove(i.next())) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Set#clear()
     */
    public void clear() {
        for (Iterator i = iterator(); i.hasNext();) {
            remove(i.next());
        }
    }
}
