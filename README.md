# Kafka demo

## [Installation](./docs/Installation.md)

## [Theory](./docs/Theory.md)

## [Producer](./docs/Producer.md)

## [Consumer](./docs/Consumer.md)

## [Cluster](./docs/Cluster.md)

-------------------------------------------------------------------

## Create topic

```shell
./kafka-topics.sh --create \
  --topic quickstart-events \
  --bootstrap-server localhost:9192
```

or

```shell
./kafka-topics.sh --create \
  --topic quickstart-events \
  --replication-factor 1 \
  --partitions 3 \
  --bootstrap-server localhost:9192
```

-------------------------------------------------------------------

## Produce Log/Message

```shell
./kafka-console-producer.sh \
    --broker-list localhost:9192 \
    --topic quickstart-events
```

With key:

```shell
./kafka-console-producer.bat \
  --broker-list localhost:9192 \
  --topic quickstart-events \
  --property "key.separator=-" \
  --property "parse.key=true"
```

Input e.g. `<KEY>-<VALUE>`

-------------------------------------------------------------------

## Consume Log/Message

```shell
./kafka-console-consumer.sh \
  --bootstrap-server localhost:9192 \
  --topic quickstart-events \
  --from-beginning
```

With key:

```shell
./kafka-console-consumer.sh \
  --bootstrap-server localhost:9192 \
  --topic quickstart-events \
  --from-beginning \
  --property "key.separator=-" \
  --property "print.key=true"
```

With Consumer Group:

```shell
./kafka-console-consumer.sh \
  --bootstrap-server localhost:9192 \
  --topic quickstart-events \
  --group <group-name>
```

-------------------------------------------------------------------

## List the topics in a cluster

```shell
./kafka-topics.sh --bootstrap-server localhost:9192 --list
```

-------------------------------------------------------------------

## List the consumer groups

```shell
./kafka-consumer-groups.sh --bootstrap-server localhost:9192 --list
```