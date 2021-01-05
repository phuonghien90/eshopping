#!/bin/sh
set -e

cd ../eshopping-module/server
./deploy.sh

cd ../../billing-service/server
./deploy.sh

cd ../../telecom-provider/server
./deploy.sh
