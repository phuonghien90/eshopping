#!/bin/sh
set -e

./gradlew :db:update -Penv=prod -x test

./gradlew :api:clean -Penv=prod -x test
./gradlew :api:build -Penv=prod -x test

docker build -t billing:lastest .

docker rm -f hien-billing || true

docker run --name hien-billing --network bank -p 8082:8080 -d billing:lastest 