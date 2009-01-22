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

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#getFileExtension()
     */
    protected String getFileExtension() {
        return FILE_EXTENSION;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#removeMessages(javax.mail.Message[])
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
    
    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.mbox.AbstractMetaFolder#save()
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

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.AbstractFolderDelegate#createMessage(int)
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
    public YamlMetaFolder getFolder(String name) throws MessagingException {
        return new YamlMetaFolder(getDelegate().getFolder(name));
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

    /*
     * (non-Javadoc)
     * 
     * @see net.fortuna.mstor.connector.FolderDelegate#getParent()
     */
    public YamlMetaFolder getParent() {
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
    public YamlMetaFolder[] list(String pattern) {
        FolderDelegate<MessageDelegate>[] folders = getDelegate().list(pattern);
        List<YamlMetaFolder> folderList = new ArrayList<YamlMetaFolder>();
        for (int i = 0; i < folders.length; i++) {
            folderList.add(new YamlMetaFolder(folders[i]));
        }
        return folderList.toArray(new YamlMetaFolder[folderList.size()]);
    }

}
