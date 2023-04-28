package org.mstor.model;

import org.mstor.filter.FilterExpression;

import java.util.List;

public interface MessageCollection extends LabelSupport {

    String getName();

    MessageCollection getParent();

    List<MessageCollection> getCollections();

    int getMessageCount();

    Iterable<Message> listMessages(int offset, int length);

    Iterable<Message> query(FilterExpression filterExpression);

    Message getMessage(String messageId);
}
