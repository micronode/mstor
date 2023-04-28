package org.mstor.model;

import java.util.List;

public interface MessageStore {

    void connect();

    void disconnect();

    List<MessageCollection> getCollections();

    MessageCollection getCollection(String collectionId);
}
