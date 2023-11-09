#!/usr/bin/env bash

if ! command -v protoc &> /dev/null
then
    echo "protoc must be installed to run this script"
    exit 1
fi

protoc --descriptor_set_out wiremock-data/grpc/services.dsc ExampleServices.proto
docker run -it --rm \
  -p 8080:8080 \
  --name wiremock \
  -v $PWD/wiremock-data:/home/wiremock \
  -v $PWD/extensions:/var/wiremock/extensions \
  wiremock/wiremock:3.2.0