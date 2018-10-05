#!/usr/bin/env bash

set -e

VERSION=$1

docker build -t clusteringsolutionsshowdown/phone-app:${VERSION} .

docker push clusteringsolutionsshowdown/phone-app:${VERSION}

kubectl set image deployment/phone-app phone-app=clusteringsolutionsshowdown/phone-app:${VERSION}