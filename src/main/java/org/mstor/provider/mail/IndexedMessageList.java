package org.mstor.provider.mail;

import jakarta.mail.Header;
import jakarta.mail.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Support for indexing messages by message header.
 */
public class IndexedMessageList {

    private final Map<Header, List<Integer>> indexMap;

    public IndexedMessageList(List<Message> messages, Header...headers) {
        this.indexMap = new ConcurrentHashMap<>();
        messages.forEach(message -> add(message, headers));
    }

    private void add(Message message, Header... headers) {
        Arrays.stream(headers).forEach(header -> {
            if (indexMap.containsKey(header)) {
                indexMap.get(header).add(message.getMessageNumber());
            } else {
                indexMap.put(header, Collections.singletonList(message.getMessageNumber()));
            }
        });
    }

    List<Integer> getMessages(Header header) {
        return indexMap.get(header);
    }
}
