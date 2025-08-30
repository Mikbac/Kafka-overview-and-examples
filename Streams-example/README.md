# Streams

## Steps

1. Run kafka via `docker compose -f ./docker/bitnami-kafka/kraft-kouncil.yaml up`
2. Call `curl -X POST localhost:8080/test`.
2. Open http://localhost:9080/topics and login as viewer
3. Send new log to the topic "sample-input-topic" e.g. `test`
4. Check "sample-output-topic" topic.
