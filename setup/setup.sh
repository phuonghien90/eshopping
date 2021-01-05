#!/bin/sh
set -e

./setup_env.sh
./setup_eureka.sh
./setup_kafka.sh
./setup_mysql.sh