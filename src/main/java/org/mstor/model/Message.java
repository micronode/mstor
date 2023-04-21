package org.mstor.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Message implements PropertyContainer, FluentMessage {

    private PropertyList propertyList;

    private final byte[] content;

    public Message(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    InputStream getContentAsStream() {
        return new ByteArrayInputStream(content);
    }

//    Iterable<MessageReference> getReferences();


    @Override
    public Message getFluentTarget() {
        return this;
    }

    @Override
    public PropertyList getPropertyList() {
        return propertyList;
    }

    @Override
    public void setPropertyList(PropertyList properties) {
        this.propertyList = properties;
    }
}
