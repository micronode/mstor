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
package net.fortuna.mstor.connector.mbox

import groovy.util.logging.Slf4j

import javax.mail.Folder;
import javax.mail.Store
import javax.mail.URLName

import spock.lang.Shared
import spock.lang.Specification

@Slf4j
class MboxMessageSpec extends Specification {
	
	@Shared javax.mail.Session mailSession

	def setupSpec() {
		mailSession = javax.mail.Session.getInstance(new Properties())
	}
	
	def 'assert read messages from mbox file'() {
		setup: 'construct a local store'
		def filename = new File(path).absolutePath
		Store localStore = mailSession.getStore(new URLName("mstor:$filename"))
		
		localStore.connect()
		
		log.info "Connected to store: $localStore.defaultFolder.fullName"
		
		Folder inbox = localStore.defaultFolder //.getFolder('Inbox')
		inbox.open(Folder.READ_ONLY)

		expect:
		inbox.messageCount == messageCount
		
		def message = inbox.messages[0]
		log.info "First message from: $message.from, subject: $message.subject"
		
		where:
		path 												| messageCount
		'etc/samples/mailboxes/contenttype-semis.mbox' 		| 1
//		'etc/samples/mailboxes/imagined.mbox' 				| 223
		'etc/samples/mailboxes/parseexception.mbox' 		| 1
		'etc/samples/mailboxes/received-0xc.mbox' 			| 1
		'etc/samples/mailboxes/samples.mbx' 				| 2
		'etc/samples/mailboxes/subject-0x1f.mbox' 			| 1
	}
}
