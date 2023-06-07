# Cluster

* https://developer.confluent.io/learn-kafka/architecture/control-plane/

## Zookeeper

1. Create a new server config with changed:

```properties
broker.id=<unique-broker-d>
listeners=PLAINTEXT://localhost:<unique-port>
log.dirs=/tmp/<unique-kafka-folder>
# Optional
auto.create.topics.enable=false
```

e.g.

```properties
broker.id=1
listeners=PLAINTEXT://localhost:9093
log.dirs=/tmp/kafka-logs-1
# Optional
auto.create.topics.enable=false
```

2. Start a new broker.

3. Create topic:

**Replication should be equal or less than the number of brokers that you have in the Kafka cluster**

```shell
./kafka-topics.sh --create \
  --topic test-topic \
  --zookeeper localhost:2181 \
  --replication-factor 3 \
  --partitions 4
```

4. Send a message:

```shell
./kafka-console-producer.sh --broker-list localhost:9092 \
  --topic test-topic
```

Summary:

* Partition leaders are assigned during topic Creation
* Clients will only invoke leader of the partition to produce and consume data
* Load is evenly distributed between the brokers

## Kraft - properties

Environment variables for Docker:

```properties
KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@bitnami-kafka-1:9093,2@bitnami-kafka-2:9093
KAFKA_CFG_NODE_ID=1
KAFKA_KRAFT_CLUSTER_ID=rwJnBJlxJUaYNBpgdpahcx
# Optional
KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=false
```

Create topic:

**Replication should be equal or less than the number of brokers that you have in the Kafka cluster**

```shell
./kafka-topics.sh --create \
  --topic test-topic \
  --replication-factor 3 \
  --partitions 4 \
  --bootstrap-server localhost:9092
```

Send a message

```shell
./kafka-console-producer.sh --broker-list localhost:9092 \
  --topic test-topic
```
