/**
 * This file is part of Base Modules.
 *
 * Copyright (c) 2010, Ben Fortuna [fortuna@micronode.com]
 *
 * Base Modules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Base Modules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Base Modules.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fortuna.mstor.search

import javax.mail.Message.RecipientType;
import javax.mail.search.ComparisonTerm;

import groovy.util.FactoryBuilderSupport;

/**
 * @author Ben
 *
 */
class SearchTermBuilder extends FactoryBuilderSupport {

    SearchTermBuilder() {
        registerFactory 'from', new FromTermFactory()
        registerFactory 'fromContains', new FromStringTermFactory()
        registerFactory 'to', new RecipientTermFactory(type: RecipientType.TO)
        registerFactory 'toContains', new RecipientStringTermFactory(type: RecipientType.TO)
        registerFactory 'cc', new RecipientTermFactory(type: RecipientType.CC)
        registerFactory 'ccContains', new RecipientStringTermFactory(type: RecipientType.CC)
        registerFactory 'bcc', new RecipientTermFactory(type: RecipientType.BCC)
        registerFactory 'bccContains', new RecipientStringTermFactory(type: RecipientType.BCC)
        registerFactory 'and', new AndTermFactory()
        registerFactory 'receivedBefore', new ReceivedDateTermFactory(comparison: ComparisonTerm.LT)
        registerFactory 'receivedAfter', new ReceivedDateTermFactory(comparison: ComparisonTerm.GT)
        registerFactory 'receivedOn', new ReceivedDateTermFactory(comparison: ComparisonTerm.EQ)
        registerFactory 'receivedOnOrBefore', new ReceivedDateTermFactory(comparison: ComparisonTerm.LE)
        registerFactory 'receivedOnOrAfter', new ReceivedDateTermFactory(comparison: ComparisonTerm.GE)
        registerFactory 'notReceivedOn', new ReceivedDateTermFactory(comparison: ComparisonTerm.NE)
        registerFactory 'sentBefore', new SentDateTermFactory(comparison: ComparisonTerm.LT)
        registerFactory 'sentAfter', new SentDateTermFactory(comparison: ComparisonTerm.GT)
        registerFactory 'sentOn', new SentDateTermFactory(comparison: ComparisonTerm.EQ)
        registerFactory 'sentOnOrBefore', new SentDateTermFactory(comparison: ComparisonTerm.LE)
        registerFactory 'sentOnOrAfter', new SentDateTermFactory(comparison: ComparisonTerm.GE)
        registerFactory 'notSentOn', new SentDateTermFactory(comparison: ComparisonTerm.NE)
        registerFactory 'messageNumber', new MessageNumberTermFactory()
        registerFactory 'sizeLessThan', new SizeTermFactory(comparison: ComparisonTerm.LT)
        registerFactory 'sizeGreaterThan', new SizeTermFactory(comparison: ComparisonTerm.GT)
        registerFactory 'sizeEquals', new SizeTermFactory(comparison: ComparisonTerm.EQ)
        registerFactory 'sizeLessThanOrEqual', new SizeTermFactory(comparison: ComparisonTerm.LE)
        registerFactory 'sizeGreateThanOrEqual', new SizeTermFactory(comparison: ComparisonTerm.GE)
        registerFactory 'sizeNotEqual', new SizeTermFactory(comparison: ComparisonTerm.NE)
        registerFactory 'hasAllFlags', new FlagTermFactory(set: true)
        registerFactory 'hasNoFlags', new FlagTermFactory(set: false)
        registerFactory 'inReplyTo', new InReplyToTermFactory()
        registerFactory 'not', new NotTermFactory()
        registerFactory 'or', new OrTermFactory()
        registerFactory 'references', new ReferencesTermFactory()
        registerFactory 'bodyContains', new BodyTermFactory()
        registerFactory 'headerContains', new HeaderTermFactory()
        registerFactory 'messageIdContains', new MessageIDTermFactory()
        registerFactory 'subjectContains', new SubjectTermFactory()
    }
}
