package org.mstor.model;

public class MessageReference {

    public enum ReferenceType {
        InReplyTo;
    }

    private final Message message;

    private final ReferenceType referenceType;

    public MessageReference(Message message, ReferenceType referenceType) {
        this.message = message;
        this.referenceType = referenceType;
    }

    public Message getMessage() {
        return message;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
}
