package net.fortuna.mstor.connector.mbox;

import java.io.FileOutputStream;
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

/**
 * @author Ben
 */
public class YamlMetaFolder extends AbstractMetaFolder {

    private FolderExt folderExt;

    /**
     * @param delegate
     */
    public YamlMetaFolder(FolderDelegate delegate) {
        super(delegate);
        try {
            folderExt = (FolderExt) Yaml.loadType(getFile(), FolderExt.class);
        }
        catch (IOException ioe) {
            folderExt = new FolderExt();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#getFileExtension()
     */
    protected String getFileExtension() {
        return ".yml";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#removeMessages(javax.mail.Message[])
     */
    protected MessageDelegate[] removeMessages(Message[] deleted) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#save()
     */
    protected void save() throws DelegateException {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(getFile());
            Yaml.dump(folderExt, fout);
        }
        catch (IOException ioe) {
            throw new DelegateException("Error saving metadata", ioe);
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException ioe) {}
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.AbstractFolderDelegate#createMessage(int)
     */
    protected MessageDelegate createMessage(int messageNumber) throws DelegateException {
        MessageExt messageExt = new MessageExt(messageNumber);
        messageExt.setFlags(new Flags());
        MessageDelegate delegate = new YamlMetaMessage(messageExt, this);
        // only add the metadata if message is associated with folder..
        if (messageNumber > 0) {
            folderExt.getMessages().add(messageNumber - 1, messageExt);
        }
        return delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.AbstractFolderDelegate#setLastUid(long)
     */
    protected void setLastUid(long uid) throws UnsupportedOperationException,
            DelegateException {
        folderExt.setLastUid(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getFolder(java.lang.String)
     */
    public FolderDelegate getFolder(String name) {
        return new YamlMetaFolder(getDelegate());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getLastUid()
     */
    public long getLastUid() throws UnsupportedOperationException {
        return folderExt.getLastUid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getMessage(int)
     */
    public MessageDelegate getMessage(int messageNumber)
            throws DelegateException {

        for (Iterator i = folderExt.getMessages().iterator(); i.hasNext();) {
            MessageExt messageExt = (MessageExt) i.next();
            if (messageExt.getMessageNumber() == messageNumber) {
                return new YamlMetaMessage(messageExt, this);
            }
        }
        MessageDelegate newMessage = createMessage(messageNumber);
        // allocate a new UID for the message..
        allocateUid(newMessage);

        return newMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getParent()
     */
    public FolderDelegate getParent() {
        return new YamlMetaFolder(getDelegate().getParent());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getUidValidity()
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

    /* (non-Javadoc)
     * @see net.fortuna.mstor.connector.FolderDelegate#list(java.lang.String)
     */
    public FolderDelegate[] list(String pattern) {
        FolderDelegate[] folders = getDelegate().list(pattern);
        List folderList = new ArrayList();
        for (int i = 0; i < folders.length; i++) {
            folderList.add(new YamlMetaFolder(folders[i]));
        }
        return (FolderDelegate[]) folderList
                .toArray(new FolderDelegate[folderList.size()]);
    }

}
