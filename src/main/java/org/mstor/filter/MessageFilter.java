/*
 *  Copyright (c) 2021, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.mstor.filter;

import jakarta.mail.search.SearchTerm;
import org.mstor.filter.expression.BinaryExpression;
import org.mstor.filter.expression.LiteralExpression;
import org.mstor.filter.expression.TargetExpression;
import org.mstor.filter.expression.UnaryExpression;

public class MessageFilter implements PredicateFactory {

    protected <V> V literal(FilterExpression expression) {
        if (expression instanceof BinaryExpression && ((BinaryExpression) expression).right instanceof LiteralExpression) {
            return ((LiteralExpression<V>) ((BinaryExpression) expression).right).getValue();
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    protected FilterTarget target(FilterExpression expression) {
        if (expression instanceof UnaryExpression
                && ((UnaryExpression) expression).operand instanceof TargetExpression) {
            return ((TargetExpression) ((UnaryExpression) expression).operand).getValue();
        } else if (expression instanceof BinaryExpression
                && ((BinaryExpression) expression).left instanceof TargetExpression) {
            return ((TargetExpression) ((BinaryExpression) expression).left).getValue();
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    public SearchTerm predicate(UnaryExpression expression) {
        switch (expression.operator) {
            case not:
                return not(predicate(expression.operand));
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    public SearchTerm predicate(BinaryExpression expression) {
        switch (expression.operator) {
            case and:
                return and(predicate(expression.left), predicate(expression.right));
            case or:
                return or(predicate(expression.left), predicate(expression.right));
        }
        throw new IllegalArgumentException("Not a valid filter");
    }
}
