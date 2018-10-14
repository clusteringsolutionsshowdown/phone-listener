#!/usr/bin/env bash

set -e

APP=phone-app

function getVersion() {
    kubectl get deployment $APP -o jsonpath='{.spec.template.spec.containers[0].image}' | egrep -o ':.+' | cut -c 2-
}

if [ -z $1 ]; then
PREV_VERSION=$(getVersion)

VERSION=$(($PREV_VERSION+1))
else
VERSION=$1
fi

echo "Building $APP version $VERSION"

docker build -t clusteringsolutionsshowdown/$APP:${VERSION} .

echo "Pushing $APP version $VERSION"

docker push clusteringsolutionsshowdown/$APP:${VERSION}

echo "Deploying $APP version $VERSION"

kubectl set image deployment/$APP $APP=clusteringsolutionsshowdown/$APP:${VERSION}