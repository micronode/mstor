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
package org.mstor.mbox.connector.jcr

import jakarta.mail.Authenticator
import jakarta.mail.Flags.Flag
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.internet.MimeMessage
import org.apache.jackrabbit.core.jndi.RegistryHelper
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session
import javax.jcr.SimpleCredentials
import javax.naming.InitialContext

class JcrMessageSpec extends Specification {
	
	static javax.naming.Context context
	
	@Shared Session session
	
	@Shared jakarta.mail.Session mailSession

	def setupSpec() {
//		def configFile = JcrMessageSpec.getResource('/repository.xml').toURI()
		def configFile = new File("src/test/resources/repository.xml")
		def homeDir = new File("target/repository/${getClass().simpleName}").absolutePath
//		def config = RepositoryConfig.create(configFile, homeDir)
//		
//		def repository = new TransientRepository(config)
		context = new InitialContext()
		RegistryHelper.registerRepository(context, getClass().simpleName, configFile.absolutePath,
			 homeDir, false)
		def repository = context.lookup(getClass().simpleName)

		session = repository.login(new SimpleCredentials('admin', ''.toCharArray()))
		
		def mailSessionProps = new Properties()
		mailSessionProps.setProperty('mstor.repository.name', getClass().simpleName)
		mailSessionProps.setProperty('mstor.repository.path', 'mail')
		mailSessionProps.setProperty('mstor.repository.create', 'true')
		mailSessionProps.setProperty('mail.store.protocol', 'mstor')
		
		mailSession = jakarta.mail.Session.getInstance(mailSessionProps,
			 { new PasswordAuthentication('admin', '') } as Authenticator)
	}
	
	def cleanupSpec() {
		session.logout()
		RegistryHelper.unregisterRepository(context, getClass().simpleName)
	}
	
	def cleanup() {
		session.refresh false
	}

	def 'verify flags are set correctly'() {
		setup:
		def localStore = mailSession.store
		localStore.connect()
		def inbox = localStore.defaultFolder.getFolder('inbox')
		if (!inbox.exists()) {
			inbox.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES)
		}
		inbox.open(Folder.READ_WRITE)
		
		MimeMessage message = [mailSession]
		message.subject = 'Test message'
		message.text = 'This is a test message'
		message.flags = [Flag.DELETED]
		inbox.appendMessages([message] as Message[])
		
		expect:
		Node messageNode = session.rootNode.getNode('mail').getNode('folders')
				.getNode('inbox').getNode('messages').nodes.nextNode()
		messageNode.getProperty('flags').values.collect { it.string } == ['deleted']
	}
}
