#!/bin/sh
set -e

docker rm -f hien-eureka || true

docker run --name hien-eureka --network bank -p 8761:8761  \
    -d springcloud/eureka 