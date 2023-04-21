package org.mstor.connector.jcr;

import org.mstor.filter.FilterExpression;
import org.mstor.model.Message;
import org.mstor.model.MessageCollection;

import java.util.List;
import java.util.Set;

public class JcrMessageCollection implements MessageCollection {

    @Override
    public void addLabel(String label) {

    }

    @Override
    public void removeLabel(String label) {

    }

    @Override
    public Set<String> getLabels() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public MessageCollection getParent() {
        return null;
    }

    @Override
    public List<MessageCollection> getCollections() {
        return null;
    }

    @Override
    public int getMessageCount() {
        return 0;
    }

    @Override
    public Iterable<Message> listMessages(int offset, int length) {
        return null;
    }

    @Override
    public Iterable<Message> query(FilterExpression filterExpression) {
        return null;
    }

    @Override
    public Message getMessage(String messageId) {
        return null;
    }
}
