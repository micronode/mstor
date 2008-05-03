package net.fortuna.mstor.connector.mbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.ho.yaml.Yaml;

import net.fortuna.mstor.connector.DelegateException;
import net.fortuna.mstor.connector.FolderDelegate;
import net.fortuna.mstor.connector.MessageDelegate;
import net.fortuna.mstor.data.yaml.FolderExt;
import net.fortuna.mstor.data.yaml.MessageExt;

public class YamlMetaFolder extends AbstractMetaFolder {

	private FolderExt folderExt;
	
	/**
	 * @param delegate
	 */
	public YamlMetaFolder(FolderDelegate delegate) {
		super(delegate);
	}
	
	protected String getFileExtension() {
		return ".yml";
	}

	protected MessageDelegate[] removeMessages(Message[] deleted) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void save() throws DelegateException {
		Yaml.dump(folderExt, getFile());
	}

	protected MessageDelegate createMessage(int messageNumber)
			throws DelegateException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setLastUid(long uid) throws UnsupportedOperationException,
			DelegateException {
		folderExt.setLastUid(uid);
	}

	public FolderDelegate getFolder(String name) {
		return new YamlMetaFolder(getDelegate());
	}

	public long getLastUid() throws UnsupportedOperationException {
		return folderExt.getLastUid();
	}

	public MessageDelegate getMessage(int messageNumber)
			throws DelegateException {
		for (Iterator i = folderExt.getMessages().iterator(); i.hasNext();) {
			MessageExt messageExt = (MessageExt) i.next();
			if (messageExt.getMessageNumber() == messageNumber) {
				return messageExt;
			}
		}
		return null;
	}

	public FolderDelegate getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getUidValidity() throws UnsupportedOperationException,
			MessagingException {
		return folderExt.getUidValidity();
	}

	public FolderDelegate[] list(String pattern) {
		FolderDelegate[] folders = getDelegate().list(pattern);
		List folderList = new ArrayList();
		for (int i = 0; i < folders.length; i++) {
			folderList.add(new YamlMetaFolder(folders[i]));
		}
		return (FolderDelegate[]) folderList.toArray(new FolderDelegate[folderList.size()]);
	}

}
