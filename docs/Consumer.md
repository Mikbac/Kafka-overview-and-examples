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

## Consumer Groups

Multiple instances of the same application with the same group id.

## Rebalance

Changing the partition ownership from one consumer to another

## Committing Offsets

https://docs.spring.io/spring-kafka/reference/html/#committing-offsets

* **RECORD**: Commit the offset when the listener returns after processing the record.
* **BATCH**: Commit the offset when all the records returned by the poll() have been processed.
* **TIME**: Commit the offset when all the records returned by the poll() have been processed, as long as the ackTime
  since the last commit has been exceeded.
* **COUNT**: Commit the offset when all the records returned by the poll() have been processed, as long as ackCount
  records have been received since the last commit.
* **COUNT_TIME**: Similar to TIME and COUNT, but the commit is performed if either condition is true.
* **MANUAL**: The message listener is responsible to acknowledge() the Acknowledgment. After that, the same semantics as
  BATCH are applied.
* **MANUAL_IMMEDIATE**: Commit the offset immediately when the Acknowledgment.acknowledge() method is called by the
  listener.

Manual offset config:

```
factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
```

## Concurrency

Concurrency config:

```
factory.setConcurrency(3);
```

(Recommended if you are not running your application in the cloud environment)