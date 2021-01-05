#!/bin/sh
set -e

./gradlew :api:clean -Penv=prod -x test
./gradlew :api:build -Penv=prod -x test

docker build -t telecom:lastest .

docker rm -f hien-telecom || true

docker run --name hien-telecom --network telecom -p 8083:8080 -d telecom:lastest 