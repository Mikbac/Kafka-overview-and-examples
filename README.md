# Kafka demo

## [Theory](./docs/Theory.md)
## [Installation](./docs/Installation.md)

-------------------------------------------------------------------

## Create topic

```shell
./kafka-topics.sh --create \
  --topic quickstart-events \
  --bootstrap-server localhost:9092
```

or

```shell
./kafka-topics.sh --create \
  --topic quickstart-events \
  --replication-factor 1 \
  --partitions 3 \
  --bootstrap-server localhost:9092
```

## Produce Log/Message

```shell
./kafka-console-producer.sh \
    --broker-list localhost:9092 \
    --topic quickstart-events
```

## Consume Log/Message

```shell
./kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic quickstart-events \
  --from-beginning
```