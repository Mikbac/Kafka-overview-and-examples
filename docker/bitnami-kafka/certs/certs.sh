#!/bin/bash

EXTERNAL_ALIAS=localhost
VALIDITY=365
PASSWORD=password1

# ----------------------------------------------------
# ---------------Generating certificates--------------
# ----------------------------------------------------

# Generating the KeyStore
keytool -keystore kafka.keystore.jks \
  -alias $EXTERNAL_ALIAS \
  -validity $VALIDITY \
  -genkey \
  -storepass $PASSWORD \
  -keyalg RSA

# Generating CA (only for local environment)
openssl req -new \
  -x509 \
  -keyout ca-key \
  -out ca-cert \
  -days 365 \
  -subj "/CN=local-security-CA" \
  -reqexts SAN -config <(cat /etc/ssl/openssl.cnf <(printf "\n[SAN]\nsubjectAltName=DNS:bitnami-kafka\n")) \
  -passin pass:$PASSWORD \
  -passout pass:$PASSWORD

# Certificate Signing Request(CSR)
keytool -keystore kafka.keystore.jks \
  -alias $EXTERNAL_ALIAS \
  -certreq \
  -storepass $PASSWORD \
  -file cert-file

# Signing the certificate
openssl x509 -req \
  -CA ca-cert \
  -CAkey ca-key \
  -in cert-file \
  -out cert-signed \
  -days $VALIDITY \
  -CAcreateserial \
  -passin pass:$PASSWORD

# Adding the Signed Cert in to the KeyStore file
keytool -keystore kafka.keystore.jks \
  -alias CARoot \
  -import \
  -file ca-cert \
  -storepass $PASSWORD

keytool -keystore kafka.keystore.jks \
  -alias $EXTERNAL_ALIAS \
  -import \
  -file cert-signed \
  -storepass $PASSWORD

# Generate the TrustStore
keytool -keystore kafka.truststore.jks \
  -alias CARoot \
  -import \
  -file ca-cert \
  -storepass $PASSWORD

keytool -keystore kafka.truststore.jks \
  -alias $EXTERNAL_ALIAS \
  -import \
  -file ca-cert \
  -storepass $PASSWORD

# ----------------------------------------------------
# --------------------Verification--------------------
# ----------------------------------------------------

keytool -list \
  -v \
  -keystore kafka.keystore.jks \
  -storepass $PASSWORD | grep Alias

# ----------------------------------------------------
# ----------------Cleaning and copying----------------
# ----------------------------------------------------

rm ca-cert ca-cert.srl ca-key cert-file cert-signed

cp kafka.keystore.jks ../../Library-producer/src/main/resources/certs/kafka.keystore.jks
cp kafka.truststore.jks ../../Library-producer/src/main/resources/certs/kafka.truststore.jks

cp kafka.keystore.jks ../../Library-consumer/src/main/resources/certs/kafka.keystore.jks
cp kafka.truststore.jks ../../Library-consumer/src/main/resources/certs/kafka.truststore.jks