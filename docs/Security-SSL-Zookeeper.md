# Security-SSL-Zookeeper

## SSL local set up (without docker)

1. Generate `kafka.keystore.jks`.
2. Set up Local Certificate Authority.
3. Create CSR (Certificate Signing Request).
4. Sign the SSL Certificate.
5. Add the Signet SSL Certificate to `kafka.keystore.jks`.
6. Configure the SSL cert in Kafka Broker.
7. Create `kafka.truststore.jks` for the client.

## SSL local set up steps

### Generating the KeyStore

Creates: `kafka.keystore.jks`

```shell
keytool -keystore kafka.keystore.jks \
  -alias localhost \
  -validity 365 \
  -genkey \
  -keyalg RSA
```

first and last name: `localhost` or `amazon.com` etc.

inspect `kafka.keystore.jks`:

```shell
keytool -list -v -keystore kafka.keystore.jks
```

### Generating CA (only for local environment)

Creates: `ca-cert` and `ca-key`

This is normally needed if we are self signing the request.

```shell
openssl req -new \
  -x509 \
  -keyout ca-key \
  -out ca-cert \
  -days 365 \
  -subj "/CN=local-security-CA"
```

### Certificate Signing Request(CSR)

Creates: `cert-file`

```shell
keytool -keystore kafka.keystore.jks -alias localhost -certreq -file cert-file
```

### Signing the certificate

Creates: `cert-signed`

```shell
openssl x509 -req \
  -CA ca-cert \
  -CAkey ca-key \
  -in cert-file \
  -out cert-signed \
  -days 365 \
  -CAcreateserial \
  -passin pass:nimda1
```

inspect:

```shell
keytool -printcert -v -file cert-signed
```

### Adding the Signed Cert in to the KeyStore file

```shell
keytool -keystore kafka.keystore.jks \
  -alias CARoot \
  -import \
  -file ca-cert
```

```shell
keytool -keystore kafka.keystore.jks \
  -alias localhost \
  -import \
  -file cert-signed
```

### Generate the TrustStore

```shell
keytool -keystore kafka.truststore.jks -alias CARoot -import -file ca-cert
```

### Broker SSL Settings

For Zookeeper:

```
listeners=PLAINTEXT://:9092,SSL://:9095
ssl.keystore.location=<location>/kafka.keystore.jks
ssl.keystore.password=password
ssl.key.password=password
ssl.endpoint.identification.algorithm=
```
