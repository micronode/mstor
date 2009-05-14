/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.mstor.util;

import javax.mail.Message;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * A {@link CacheAdapter} implementation that uses the Ehcache library.
 */
public class EhCacheAdapter extends CacheAdapter {

    private String cacheName;
    
    public EhCacheAdapter(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * Clears the cache.
     */
    public void clearCache() {
        getMessageCache().removeAll();
    }
    
    @Override
    public Object retrieveObjectFromCache(int index) {
        Element cacheElement = getMessageCache().get(index);
        
        if (cacheElement != null) {
            Message message = (Message) cacheElement.getValue();
            return message;
        } else {
            return null;
        }
    }
    
    @Override
    public void putObjectIntoCache(int index, Object object) {
        getMessageCache().put(new Element(new Integer(index), object));
    }
    
    private Cache getMessageCache() {
        CacheManager manager = CacheManager.create();
        if (manager.getCache(cacheName) == null) {
            manager.addCache(cacheName);
        }
        return manager.getCache(cacheName);
    }
}

