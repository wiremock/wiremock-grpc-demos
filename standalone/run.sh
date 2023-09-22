#!/usr/bin/env bash

if ! command -v protoc &> /dev/null
then
    echo "protoc must be installed to run this script"
    exit 1
fi

protoc --descriptor_set_out wiremock-data/grpc/services.dsc ExampleServices.proto
java -cp wiremock-standalone-3.2.0.jar:wiremock-grpc-extension-standalone-0.1.0.jar wiremock.Run --port 8000 --root-dir wiremock-data