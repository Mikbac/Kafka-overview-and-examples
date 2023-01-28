# Security-SSL-Zookeeper

## SSL local set up (with docker)

Two scripts:

1. Only for local environment -> [certs.sh](..%2Fdocker%2Fcerts%2Fcerts.sh)
2. [kafka-generate-ssl.sh](https://raw.githubusercontent.com/confluentinc/confluent-platform-security-tools/master/kafka-generate-ssl.sh)

### Broker SSL Settings

For Kraft (docker):

* add volumes:

```
- ./certs/kafka.keystore.jks:/opt/bitnami/kafka/config/certs/kafka.keystore.jks:ro
- ./certs/kafka.truststore.jks:/opt/bitnami/kafka/config/certs/kafka.truststore.jks:ro
```

* change `KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP` to `SSL` for specific listener
* set password `KAFKA_CERTIFICATE_PASSWORD`