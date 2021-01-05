#!/bin/sh
set -e

./gradlew :db:update -Penv=prod -x test

./gradlew :api:clean -Penv=prod -x test
./gradlew :api:build -Penv=prod -x test

docker build -t eshopping:lastest .

docker rm -f hien-eshopping || true

docker run --name hien-eshopping --network bank -p 8081:8080 -d eshopping:lastest 

docker network connect telecom hien-eshopping || true