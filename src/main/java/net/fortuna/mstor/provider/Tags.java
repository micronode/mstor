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

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Ben Fortuna
 * 
 * <pre>
 * $Id$
 *
 * Created on 6/05/2006
 * </pre>
 * 
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
    public Tags(final Flags flags) {
        this.flags = flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#size()
     */
    public final int size() {
        int tagCount = 0;
        String[] userFlags = flags.getUserFlags();
        for (String userFlag : userFlags) {
            if (userFlag.startsWith(TAG_PREFIX)) {
                tagCount++;
            }
        }
        return tagCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#isEmpty()
     */
    public final boolean isEmpty() {
        return size() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#contains(java.lang.Object)
     */
    public final boolean contains(final Object tag) {
        if (tag instanceof String) {
            String flag = TAG_PREFIX + tag;
            String[] userFlags = flags.getUserFlags();
            for (String userFlag : userFlags) {
                if (userFlag.equals(flag)) {
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
        for (String userFlag : userFlags) {
            if (userFlag.startsWith(TAG_PREFIX)) {
                tags.add(userFlag.split(TAG_PREFIX)[1]);
            }
        }
        return tags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#iterator()
     */
    public final Iterator<String> iterator() {
        return getTagSet().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#toArray()
     */
    public final Object[] toArray() {
        return getTagSet().toArray();
    }
    
    /* (non-Javadoc)
     * @see java.util.Set#toArray(T[])
     */
    public final <T> T[] toArray(T[] a) {
        return getTagSet().toArray(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#add(E)
     */
    public final boolean add(final String tag) {
        String flag = TAG_PREFIX + tag;
        if (!flags.contains(flag)) {
            flags.add(flag);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#remove(java.lang.Object)
     */
    public final boolean remove(final Object tag) {
        if (tag instanceof String) {
            flags.remove(TAG_PREFIX + tag);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    public final boolean containsAll(final Collection<?> arg0) {
        for (Object anArg0 : arg0) {
            if (!contains(anArg0)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#addAll(java.util.Collection)
     */
    public final boolean addAll(final Collection<? extends String> arg0) {
        for (String anArg0 : arg0) {
            if (!add(anArg0)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    public final boolean retainAll(final Collection<?> arg0) {
        for (String tag : this) {
            if (!arg0.contains(tag) && !remove(tag)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    public final boolean removeAll(final Collection<?> arg0) {
        for (Object anArg0 : arg0) {
            if (!remove(anArg0)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Set#clear()
     */
    public final void clear() {
        for (String s : this) {
            remove(s);
        }
    }

    /**
     * Adds the specified provider to a message.
     * 
     * @param tag
     * @param message
     * @throws MessagingException when unable to provider the given message
     * @throws UnsupportedOperationException if the given message does not support tags
     */
    public static void addTag(final String tag, final Message message)
            throws MessagingException {

        if (message instanceof Taggable) {
            ((Taggable) message).addTag(tag);
            return;
        }
        throw new UnsupportedOperationException("Message is not taggable");
    }

    /**
     * Remove the specified provider from a message.
     * 
     * @param tag
     * @param message
     * @throws MessagingException when unable to remove the provider from the given message
     * @throws UnsupportedOperationException if the given message does not support tags
     */
    public static void removeTag(final String tag, final Message message)
            throws MessagingException {

        if (message instanceof Taggable) {
            ((Taggable) message).removeTag(tag);
            return;
        }
        throw new UnsupportedOperationException("Message is not taggable");
    }

    /**
     * Returns the tags associated with the specified message.
     * 
     * @param message
     * @return
     * @throws MessagingException when unable to retrieve the tags for the given message
     * @throws UnsupportedOperationException if the given message does not support tags
     */
    public static Tags getTags(final Message message) throws MessagingException {

        if (message instanceof Taggable) {
            return ((Taggable) message).getTags();
        }
        throw new UnsupportedOperationException("Message is not taggable");
    }
}
