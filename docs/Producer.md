# Producer

## Producer Configurations

https://kafka.apache.org/documentation/#producerconfigs

**ACK**
acks = 0, 1 and all

* acks = 1 -> guarantees message is written to a leader ( Default)
* acks = all -> guarantees message is written to a leader and to all the replicas
* acks=0 -> no guarantee (Not Recommended)

**retries** - retrying in case of any failure producing the message for thr broker

* Integer value = [0 - 2147483647]
* In Spring Kafka, the default value is -> 2147483647

**retry.backoff.ms**

* Integer value represented in milliseconds
* Default value is 100ms