package org.mstor.filter;

import jakarta.mail.search.AndTerm;
import jakarta.mail.search.NotTerm;
import jakarta.mail.search.OrTerm;
import jakarta.mail.search.SearchTerm;
import org.mstor.filter.expression.BinaryExpression;
import org.mstor.filter.expression.UnaryExpression;

public interface PredicateFactory {

    default SearchTerm predicate(FilterExpression expression) {
        if (expression instanceof UnaryExpression) {
            return predicate((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return predicate((BinaryExpression) expression);
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    SearchTerm predicate(UnaryExpression expression);

    SearchTerm predicate(BinaryExpression expression);

    default SearchTerm and(SearchTerm...predicates) {
        return new AndTerm(predicates);
    }

    default SearchTerm or(SearchTerm...predicates) {
        return new OrTerm(predicates);
    }

    default SearchTerm not(SearchTerm predicate) {
        return new NotTerm(predicate);
    }
}
