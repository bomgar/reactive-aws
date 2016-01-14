# Idea

Create an aws sdk that is non-blocking and does **not** use the official amazon sdk under the hood.

# Build

[![Build Status](https://travis-ci.org/bomgar/reactive-aws.svg?branch=master)](https://travis-ci.org/bomgar/reactive-aws)

# Currently working
## Authentication 
code is still an ugly scala adaptation of the amazon samples

## SQS
* List queues
* Queue url by name
* Create queue
* Delete queue
* Send messages
* Receive messages
* Acknowledge messages
* Purge queues
* gGet queue attributes

## SNS
* Create topics
* List topics (without paging, yet!)
* Delete topic
* Publish
* Subscribe topic
* List subscriptions by topic
* Get/Set topic attributes