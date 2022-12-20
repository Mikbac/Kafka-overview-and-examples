# Theory

* https://kafka.apache.org/documentation/
* https://kafka.apache.org/intro
* https://cwiki.apache.org/confluence/display/KAFKA/KIP-500%3A+Replace+ZooKeeper+with+a+Self-Managed+Metadata+Quorum

## Books and certs

* Book -> https://www.confluent.io/resources/kafka-the-definitive-guide/
* Cert -> https://cloud.contentraven.com/confluent/self-userpackage

## Dictionary

* Topic is an Entity in Kafka with a name
* Partition is where the message lives inside the topic
* Broker is an Apache Kafka instance (k8s POD or VM)
* Cluster is several brokers connected together

## Architecture

![Kafka architecture from projectpro](./img/Kafka_Architecture_projectpro.webp "Kafka Architecture from https://www.projectpro.io/article/apache-kafka-architecture-/442")

![Kafka architecture from bulldogjob](./img/Kafka_Architecture_bulldogjob.png "Kafka Architecture from https://bulldogjob.pl/readme/apache-kafka-opis-dzialania-i-zastosowania")

* **Topic** - Events are organized and durably stored in topics. Very simplified, a topic is similar to a folder in a
  filesystem, and the events are the files in that folder.
* **Partition** - Topics are partitioned, meaning a topic is spread over a number of "buckets" located on different
  Kafka
  brokers. This distributed placement of your data is very important for scalability because it allows client
  applications to both read and write the data from/to many brokers at the same time. When a new event is published to a
  topic, it is actually appended to one of the topic's partitions.

## API's

* Admin API - The Admin API supports managing and inspecting topics, brokers, acls, and other Kafka objects.
* Producer API - The Producer API allows applications to send streams of data to topics in the Kafka cluster.
* Consumer API - The Consumer API allows applications to read streams of data from topics in the Kafka cluster.
* Kafka Streams API - The Streams API allows transforming streams of data from input topics to output topics.
* Kafka Connect API - The Connect API allows implementing connectors that continually pull from some source data system
  into Kafka or push from Kafka into some sink data system.

## Partition

To receive messages in the same order, send messages to the same partition.

```
Using a hashing key partition, we can deliver messages with the same key in order, by sending it to the same partition.
Data within a partition will be stored in the order in which it is written. Therefore, data read from a partition will
be read in order for that partition with producer key.
```

## Consumer Offset

Consumer have three options to read:

* from-beginning
* latest
* specific offset

Consumer offsets behaves like a bookmark for the consumer to start
reading the messages from the point it left off.

## Consumer Groups

Consumer Groups are used for scalable message consumption.
Each different application will have a unique consumer group.

![Kafka Consumer Groups from dilipsundarraj1](./img/Kafka_Consumers_Group_dilipsundarraj1.png "Kafka Consumer Groups")

## Retention Policy

Determines how long the message is retained.
Configured using the property `log.retention.hours` in `server.properties` file.
Default retention period is 168 hours (7 days).