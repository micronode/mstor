package org.mstor.connector.jcr;

import org.mstor.model.MessageCollection;
import org.mstor.model.MessageStore;

import java.util.List;

public class JcrMessageStore implements MessageStore {

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public List<MessageCollection> getCollections() {
        return null;
    }

    @Override
    public MessageCollection getCollection(String collectionId) {
        return null;
    }
}
