# Kafka demo

## [Documentation and notes](./docs/README.md)

## Installation

### Docker - without Zookeeper

https://hub.docker.com/r/bitnami/kafka

Run (without UI):
```shell
docker compose -f ./docker/kraft.yaml up
```

Run (with UI):
```shell
docker compose -f ./docker/kouncil-kraft.yaml up
```

Kouncil UI: http://localhost:9080

http://localhost:9080

### Local - with Zookeeper

1. Go to https://kafka.apache.org/downloads
2. Binary download e.g. `kafka_2.13-3.3.1.tgz`
3. Unpack
4. Go to
   * (For Windows) Go to `kafka_2.13-3.3.1/bin/windows`
   * (For Linux) Go to `kafka_2.13-3.3.1/bin`


## UI - Kouncil

https://hub.docker.com/r/consdata/kouncil

-------------------------------------------------------------------

## Create topic

```shell
./kafka-topics.sh --create \
  --topic quickstart-events \
  --bootstrap-server localhost:9092
```
