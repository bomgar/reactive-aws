# Idea

Create an aws sdk that is non-blocking and does **not** use the official amazon sdk under the hood.

# Build

[![Build Status](https://travis-ci.org/bomgar/reactive-aws.svg?branch=master)](https://travis-ci.org/bomgar/reactive-aws)

# Setup
Set credentials as environment variables. Currently in use:

| Used Env |
| --- |
| AWS\_ID |
| SNS\_AWS\_ACCESS\_KEY |
| SNS\_AWS\_SECRET\_KEY |
| SQS\_AWS\_ACCESS\_KEY |
| SQS\_AWS\_SECRET\_KEY |

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
* Add permission