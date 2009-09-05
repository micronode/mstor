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
package net.fortuna.mstor.connector.mbox;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.yaml.FolderExt;
import net.fortuna.mstor.data.yaml.MessageExt;

import org.ho.yaml.Yaml;
import org.ho.yaml.YamlDecoder;

/**
 * @author Ben
 */
public class YamlMetaFolder extends AbstractMetaFolder<YamlMetaMessage> {

    public static final String FILE_EXTENSION = ".yml";

    private FolderExt folderExt;

//    private YamlConfig yamlConfig;
    
    /**
     * @param delegate
     */
    public YamlMetaFolder(FolderDelegate<MessageDelegate> delegate) {
        super(delegate);
//        yamlConfig = new YamlConfig();
        YamlDecoder decoder = null;
        try {
//            folderExt = (FolderExt) yamlConfig.loadType(getFile(), FolderExt.class);
            decoder = new YamlDecoder(new FileInputStream(getFile()));
            folderExt = (FolderExt) decoder.readObjectOfType(FolderExt.class);
        }
        catch (IOException ioe) {
            folderExt = new FolderExt();
        }
        finally {
            if (decoder != null) {
                decoder.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    protected YamlMetaMessage[] removeMessages(Message[] deleted) {
        List<YamlMetaMessage> metas = new ArrayList<YamlMetaMessage>();

        for (Iterator<MessageExt> i = folderExt.getMessages().iterator(); i
                .hasNext();) {

            MessageExt messageExt = i.next();
            int messageNumber = messageExt.getMessageNumber();

            for (int n = 0; n < deleted.length; n++) {
                if (deleted[n].getMessageNumber() == messageNumber) {
                    i.remove();
                    metas.add(new YamlMetaMessage(messageExt, this));
                    updateMessageNumbers(messageNumber, -1);
                    break;
                }
            }
        }
        return (YamlMetaMessage[]) metas.toArray(new YamlMetaMessage[metas.size()]);
    }


    /**
     * Updates all message numbers according to the specified arguments. Used when message metadata
     * is removed from the list.
     * 
     * @param startIndex anything greater than (or equal to) the start index is affected
     * @param delta amount to adjust relevant message numbers by
     */
    private void updateMessageNumbers(final int startIndex, final int delta) {
        for (Iterator<MessageExt> i = folderExt.getMessages().iterator(); i
                .hasNext();) {
            MessageExt messageExt = i.next();
            int messageNumber = messageExt.getMessageNumber();
            if (messageNumber >= startIndex) {
                messageExt.setMessageNumber(messageNumber + delta);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void save() throws DelegateException {
//        FileOutputStream fout = null;
        try {
            Yaml.dump(folderExt, getFile(), true);
//            fout = new FileOutputStream(getFile());
//            yamlConfig.dump(folderExt, fout);
        }
        catch (IOException ioe) {
            throw new DelegateException("Error saving metadata", ioe);
        }
//        finally {
//            if (fout != null) {
//                try {
//                    fout.close();
//                }
//                catch (IOException ioe) {}
//            }
//        }
    }

    /**
     * {@inheritDoc}
     */
    protected YamlMetaMessage createMessage(int messageNumber) throws DelegateException {
        MessageExt messageExt = new MessageExt(messageNumber);
        messageExt.setFlags(new Flags());
        YamlMetaMessage delegate = new YamlMetaMessage(messageExt, this);
        // only add the metadata if message is associated with folder..
        if (messageNumber > 0) {
            folderExt.getMessages().add(messageNumber - 1, messageExt);
        }
        return delegate;
    }

    /**
     * {@inheritDoc}
     */
    protected void setLastUid(long uid) throws UnsupportedOperationException,
            DelegateException {
        folderExt.setLastUid(uid);
    }

    /**
     * {@inheritDoc}
     */
    public YamlMetaFolder getFolder(String name) throws MessagingException {
        return new YamlMetaFolder(getDelegate().getFolder(name));
    }

    /**
     * {@inheritDoc}
     */
    public long getLastUid() throws UnsupportedOperationException {
        return folderExt.getLastUid();
    }

    /**
     * {@inheritDoc}
     */
    public YamlMetaMessage getMessage(int messageNumber)
            throws DelegateException {

        for (Iterator<MessageExt> i = folderExt.getMessages().iterator(); i.hasNext();) {
            MessageExt messageExt = i.next();
            if (messageExt.getMessageNumber() == messageNumber) {
                return new YamlMetaMessage(messageExt, this);
            }
        }
        YamlMetaMessage newMessage = createMessage(messageNumber);
        // allocate a new UID for the message..
        allocateUid(newMessage);

        return newMessage;
    }

    /**
     * {@inheritDoc}
     */
    public YamlMetaFolder getParent() {
        return new YamlMetaFolder(getDelegate().getParent());
    }
    
    /**
     * {@inheritDoc}
     */
    public long getUidValidity() throws UnsupportedOperationException,
            MessagingException {
        if (folderExt.getUidValidity() <= 0) {
            folderExt.setUidValidity(newUidValidity());
            try {
                save();
            }
            catch (DelegateException de) {
                throw new MessagingException("Error in delegate", de);
            }
        }
        return folderExt.getUidValidity();
    }

    /**
     * {@inheritDoc}
     */
    public YamlMetaFolder[] list(String pattern) {
        FolderDelegate<MessageDelegate>[] folders = getDelegate().list(pattern);
        List<YamlMetaFolder> folderList = new ArrayList<YamlMetaFolder>();
        for (int i = 0; i < folders.length; i++) {
            folderList.add(new YamlMetaFolder(folders[i]));
        }
        return folderList.toArray(new YamlMetaFolder[folderList.size()]);
    }

}
