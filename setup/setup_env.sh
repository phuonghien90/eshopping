#!/bin/sh
set -e

echo 'checking curl.....'
if ! [ -x "$(command -v curl)" ]; then
    apt install curl
fi

echo 'checking java.....'
if ! [ -x "$(command -v java)" ]; then
  sudo apt-get install openjdk-8-jdk
fi

echo 'checking docker.....'
if ! [ -x "$(command -v docker)" ]; then
    apt-get update
    apt-get install -y docker.io
fi

docker network create --driver=bridge --subnet=172.20.0.0/16 --ip-range=172.20.5.0/24 --gateway=172.20.5.254  bank || true

docker network create --driver=bridge --subnet=172.21.0.0/16 --ip-range=172.21.5.0/24 --gateway=172.21.5.254  telecom || true