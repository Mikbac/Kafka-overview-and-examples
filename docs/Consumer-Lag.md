## Consumer Lag

Kafka lag (Consumer Lag) refers to the delay between message production and consumption in a Kafka system. It indicates how far behind
the consumer is in processing messages compared to the producer. Essentially, it measures the difference between the "
log end offset" (latest message produced) and the "consumer offset" (last message consumed). High lag can lead to
delayed processing, outdated data, and performance issues.

```shell
bin/kafka-consumer-groups.sh \
  --bootstrap-server <broker> \
  --describe \
  --group <consumer-group-name>
# e.g.
bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --group my-consumer-group
```
