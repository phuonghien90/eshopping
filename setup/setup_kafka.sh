#!/bin/sh
set -e

docker rm -f hien-zookeeper || true

docker run --name hien-zookeeper --network bank -p 2181:2181 -d wurstmeister/zookeeper

docker rm -f hien-kafka || true

docker run --name hien-kafka --network bank -p 9094:9094 \
    -e KAFKA_ZOOKEEPER_CONNECT=hien-zookeeper \
    -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT \
    -e KAFKA_ADVERTISED_LISTENERS=INSIDE://:9092,OUTSIDE://hien-kafka:9094 \
    -e KAFKA_LISTENERS=INSIDE://:9092,OUTSIDE://:9094 \
    -e KAFKA_INTER_BROKER_LISTENER_NAME=INSIDE \
    -d wurstmeister/kafka
