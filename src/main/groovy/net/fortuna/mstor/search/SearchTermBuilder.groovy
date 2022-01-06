/**
 * Copyright (c) 2011, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.mstor.search

import javax.mail.Message.RecipientType
import javax.mail.search.ComparisonTerm

/**
 * @author Ben
 *
 */
class SearchTermBuilder extends FactoryBuilderSupport {

    SearchTermBuilder(boolean init = true) {
        super(init)
    }
    
    def registerTerms() {
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
