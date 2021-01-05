#!/bin/sh
set -e

docker rm -f hien-mysql || true

docker run --name hien-mysql --network bank -p 3306:3306 -v /root/data/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=hien -d mysql:8.0.22