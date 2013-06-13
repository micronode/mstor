=======================
 mstor - Release Notes
=======================

 Mstor is a JavaMail provider for persistent email storage. mstor builds on the mbox format and
 provides the following benefits:

 * implemented using Java "New I/O" (java.nio.*) to (theoretically) provide better
 performance
 
 * uses the mbox format for storing messages
 
 * incorporates a "metafile" for each mbox folder to store additional metadata for
 messages (and alleviate the need to modify the mbox file)
 
 * provides basic caching configuration to allow control over the performance vs.
 memory usage equation
 
 * better performance for large mailboxes

 Detailed descriptions of changes included in each release may be found
 in the CHANGELOG.


==============
 How to build
==============
 
 Using Maven:

 If you have downloaded the source distribution, you should be able to package a JAR
 file simply by running maven in the root directory. e.g:
 
   C:\Libs\mstor-0.9.12-src\>mvn package


 Using Ant:
 
 If you have downloaded the source distribution, you should be able to package a JAR
 file simply by running ant in the root directory. e.g:
 
   C:\Libs\mstor-0.9.12-src\>ant
 
 If for some reason you would like to override the default build classpath, I would
 suggest creating a "build.properties" file (see the provided sample) in the root directory
 and add overridden properties to this. You can also override properties via Java system
 properties (e.g. -Dproject.classpath="..."). You shouldn't need to modify the "build.xml" at all,
 so if you do find a need let me know and I'll try to rectify this.
 
===================
 System Properties
===================

 A number of system properties may be specified in order to configure the operation
 of mstor to suit your purpose. These are as follows:
 
  - mstor.mbox.metadataStrategy={none|xml|yaml}
  
  enables mstor-specific metadata to provide full JavaMail
  functionality and performance enhancement. Default value is equivalent to xml.
  
  - mstor.mbox.encoding (specifies the file encoding used to read the mbox file.
  Default value: "ISO-8859-1").
  
  - mstor.mbox.bufferStrategy={default|mapped|direct}
  
  specifies whether to use java.nio maps to read
  messages from the mbox file. This may improve performance for reading, however
  due to the implementation of java.nio maps it is not recommended to enable
  maps when you plan to modify the underlying mbox file. Default value is equivalent
  to non-direct buffers.
  
  - mstor.mbox.cacheBuffers={default|enabled|disabled}
  
  message data from an mbox file is read into a buffer
  prior to returning the underlying data stream. This property specifies whether
  to cache these buffers in memory. This option may improve performance in reading
  messages, but will increase memory usage. Default value is equivalent to disabled.
  
  - mstor.cache.disabled={false|true}
  
  a value of 'true' turns of the caching functionality. In most cases this setting
  will lead to drastic performance degradation, especially if your application 
  reads the same message multiple times. There are cases though, when you only need
  to read a single message only once. If this is the case, this setting may 
  drastically reduce the memory consumption. Apart from that setting this property
  to 'true' will allow you to exclude ehcache from the list of jars you need to
  ship with your application.
 
==========
 Metadata
==========

 By default mstor provides the ability to extend the standard JavaMail features
 through the use of metadata. This metadata is stored in an XML-based document
 for each folder in the store. Current metadata includes the following:
 
  - received date
  - flags
  - headers (NOTE: although headers are already saved to the underlying mbox
  file, duplicating in metadata allows them to be read without needing to parse
  the entire message content - thus increasing performance..hopefully!)
 
 If you decide not to use mstor's metadata feature, you can disable it by
 specifying the following session property:
 
 	mstor.metadata=disabled
 	
 e.g.
 
 	Properties p = new Properties();
 	p.setProperty("mstor.metadata", "disabled");
 	Session session = Session.getDefaultInstance(p);

====================
 Mbox File Encoding
====================

 The mbox format is essentially a concatenation of RFC822 (or RFC2822) messages
 with an additional "From_" line inserted at the start of each message. Instances
 of "From_" within the message body are also escaped with a preceding ">"
 character.
 
 Although mstor doesn't encode/decode message content (it is assumed appended
 messages are valid RFC822 messages), we still need to use an encoding to
 interpret and create the "From_" line.
 
 Because mbox is just RFC822 messages, file encoding should always be "US-ASCII",
 however JavaMail seems to use "ISO-8859-1" encoding and as such mstor will also
 use this encoding as the default.
 
 It is possible however, to override the mbox file encoding used by specifying the
 following system property:
 
     -Dmstor.mbox.encoding=<some_encoding>
 

==================
 OutOfMemoryError
==================

 You may encounter errors when trying to load a large mailbox into memory all
 at once. Here are some pointers to help avoid this problem:
 
 - As mstor messages subclass javax.mail.internet.MimeMessage a "lazy" loading
 scheme is employed. This means that message data is only parsed on request
 (e.g. message.getSubject()) as opposed to upon instantiation.
 	
 Memory problems may still be encountered however, in circumstances where you
 require data from all messages in a folder (e.g. displaying message subject
 line in a table). If using a swing table this may be alleviated somewhat by
 using an appropriate table model implementation to only retrieve message
 data for visible messages.
 
 Mstor helps in this respect as it should only cache a limited number of
 messages in memory (the default is one hundred (100)) allowing other messages
 to be garbage collected (*).
 
 * Side Note: mstor doesn't always conform to the following comment on
 javax.mail.Folder.getMessage() due to caching:
 
 	"Unlike Folder objects, repeated calls to getMessage with the same
 	 message number will return the same Message object, as long as no
 	 messages in this folder have been expunged."

 - Try to avoid using javax.mail.Folder.getMessages() on large folders as you
 will be operating on all available messages - effectively overriding the
 cache mechanism. The method itself is not that memory intensive, but parsing
 message data for all messages in the array may be.
 
 In some cases you might even consider turning the cache off altogether.
 See the description of the 'mstor.cache.disabled' system property for
 more details.