package org.mstor.model;

import java.util.List;

public class MboxMessageStore implements MessageStore {

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
