package org.mstor.provider.mail.search;

import jakarta.mail.Message;
import jakarta.mail.search.SearchTerm;

import java.util.function.Predicate;

/**
 * A search term backed with a functional predicate.
 */
public class PredicateSearchTerm extends SearchTerm {

    private final Predicate<Message> messagePredicate;

    public PredicateSearchTerm(Predicate<Message> messagePredicate) {
        this.messagePredicate = messagePredicate;
    }

    @Override
    public boolean match(Message msg) {
        return messagePredicate.test(msg);
    }
}
