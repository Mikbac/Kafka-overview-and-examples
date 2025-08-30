# Kafka demo

## Docs

* [Installation](./docs/Installation.md)

* [Theory](./docs/Theory.md)

* [Producer](./docs/Producer.md)

* [Consumer](./docs/Consumer.md)

    * [Consumer-Lag.md](docs/Consumer-Lag.md)

* [Cluster](./docs/Cluster.md)

* [Recovery](./docs/Recovery.md)

* [Security](./docs/Security.md)

    * [Security-SSL-Zookeeper](./docs/Security-SSL-Zookeeper.md)

* [Kubernetes](./docs/Kubernetes.md)

## Projects

* [Library-consumer](Library-consumer) and [Library-producer](Library-producer)
  Request: [Library.http](http/Library.http)

* Docker compose files (confluentinc image):
    *  [kraft-akhq.yaml](docker/confluentinc-kafka/kraft-akhq.yaml) - Apache Kafka (**3.9**) and Akhq (**0.26.0**)

* Docker compose files (bitnami image):
    * [kraft.yaml](docker/bitnami-kafka/kraft.yaml) - basic Kafka (3.4.0) example
    * [kraft-kouncil.yaml](docker/bitnami-kafka/kraft-kouncil.yaml) - **(UPDATED)** Kafka (**3.7.0**) and Kouncil (**1.7**) -
      additional configuration descriptions inside the docker compose file
    * [kraft-kouncil-cluster.yaml](docker/bitnami-kafka/kraft-kouncil-cluster.yaml) - three Kafka (3.4.0) instances and Kouncil (1.4)
    * [kraft-kouncil-ssl.yaml](docker/bitnami-kafka/kraft-kouncil-ssl.yaml) - single Kafka (3.4.0) instance with ssl and Kouncil (1.4)
    * [kraft-kouncil-traefik.yaml](docker/bitnami-kafka/kraft-kouncil-traefik.yaml) - single Kafka (3.4.0) instance with Traefik
      proxy (v3.0) and Kouncil (1.4)

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
