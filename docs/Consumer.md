# Consumer

Consumer can consume records from multiple topics.

## Consuming

**MessageListenerContainer**

* KafkaMessageListenerContainer
  * Implementation of MessageListenerContainer
  * Polls the records
  * Commits the Offsets
  * Single Threaded
* ConcurrentMessageListenerContainer

**@KafkaLisener Annotation**

* Uses ConcurrentMessageListenerContainer behind the scenes