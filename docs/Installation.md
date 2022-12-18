# Installation

## Docker

### Docker - without Zookeeper

https://hub.docker.com/r/bitnami/kafka

**Run (without UI):**

```shell
docker compose -f ./docker/kraft.yaml up
```

| Service      | Endpoint              |
|--------------|-----------------------|
| Kafka broker | http://localhost:9092 |

**Run (with UI):**

```shell
docker compose -f ./docker/kouncil-kraft.yaml up
```

| Service      | Endpoint              |
|--------------|-----------------------|
| Kafka broker | http://localhost:9092 |
| Kouncil UI   | http://localhost:9080 |

**Run (with UI via proxy):**

```shell
docker compose -f ./docker/kouncil-kraft-traefik.yaml up
```

| Service           | Endpoint                           |
|-------------------|------------------------------------|
| Kafka broker      | http://localhost:9092              |
| Traefik Dashboard | http://localhost:9081/dashboard/#/ |
| Kouncil UI        | http://localhost:9082/kouncil-ui/  |

-------------------------------------------------------------------

## Local - without Zookeeper (Kafka 3.0)

1. Go to https://kafka.apache.org/downloads
2. Binary download e.g. `kafka_2.13-3.3.1.tgz`
3. Unpack
4. Go to
    * (For Windows) Go to `kafka_2.13-3.3.1/bin/windows`
    * (For Linux) Go to `kafka_2.13-3.3.1/bin`
5. Edit `server.properties` (for server-1):
    ```
    broker.id=<unique-broker-d>
    listeners=PLAINTEXT://localhost:<unique-port>
    log.dirs=/tmp/<unique-kafka-folder>
    auto.create.topics.enable=false
   ```
   e.g.
    ```
    broker.id=1
    listeners=PLAINTEXT://localhost:9093
    log.dirs=/tmp/kafka-logs-1
    auto.create.topics.enable=false
    ```
6. Run Kafka Broker:
    ```shell
    ./kafka-server-start.sh ../config/server-1.properties
    ./kafka-server-start.sh ../config/server-2.properties
    ./kafka-server-start.sh ../config/server-3.properties
    ```

-------------------------------------------------------------------

## Local - with Zookeeper

*For Kafka 3.0 Zookeeper is deprecated*

1. Go to https://kafka.apache.org/downloads
2. Binary download e.g. `kafka_2.13-3.3.1.tgz`
3. Unpack
4. Go to
    * (For Windows) Go to `kafka_2.13-3.3.1/bin/windows`
    * (For Linux) Go to `kafka_2.13-3.3.1/bin`
5. Run Zookeeper `./zookeeper-server-start.sh ../config/zookeeper.properties`
6. Add properties to `server.properties`:
   ```properties
   listeners=PLAINTEXT://localhost:9092
   # When you try to send a message to a topic that doesn't exist, then automatically create a topic
   auto.create.topics.enable=false
   ```
7. Run Kafka Broker `./kafka-server-start.sh ../config/server.properties`

-------------------------------------------------------------------

## UI - Kouncil

https://hub.docker.com/r/consdata/kouncil