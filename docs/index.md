# mStor - Universal message archival and storage

mStor is a Java library that supports persistence of various forms of digital communications, such as Email 
and Instant Messaging.

## Overview

Messaging specifications typically define encoding and metadata, but are relatively agnostic to the content.
So too is mStor, which focuses on supporting persistence of metadata and a suitably encoded message body.

## JavaMail

The origins of mStor are as a JavaMail provider for the unofficial mbox specification. JavaMail is still
supported, however the intention is to also support storage of other communications such as Instant
Messaging via a universal API. This would theoretically allow messages to be stored and retrieved as either
Email or Instant Messaging interchangeably.

## JMAP

JMAP is a relatively new API from Fastmail, designed to support Web-based Email clients.

## Tinkerpop

Tinkerpop is an open API for interacting with graph databases. This is potentially useful for capturing
relationships between messages, senders and recipients, and can support powerful analytics and reporting
on large data sets.

## DynamoDB

DynamoDB is a highly scalable NoSQL database from AWS. It can efficiently store large amounts of metadata
and efficiently query on a well-designed schema.
