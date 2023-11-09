# WireMock gRPC Demos

## Java

This example requires at least Java 11 to be installed.

To generate all the necessary code and descriptors then run the tests, run the following command:

```bash
cd java
./gradlew check
```

## Standalone

This example requires `protoc` and at least Java 11 to be installed. Optionally, [grpcurl](https://github.com/fullstorydev/grpcurl)
can be used for testing.

To generate the descriptor file from the .proto file and start the standalone server with gRPC enabled:

```bash
cd standalone
./run.sh
```

If you have `grpcurl` installed you can test the mock as follows:

```bash
grpcurl -d '{"name": "Tom" }' -plaintext \
  -proto ExampleServices.proto \
  localhost:8000 com.example.grpc.GreetingService/greeting
```

## Docker

This example requires `protoc` and at least Java 11 to be installed. Optionally, [grpcurl](https://github.com/fullstorydev/grpcurl)
can be used for testing.

To generate the descriptor file from the .proto file and start WireMock in Docker with gRPC enabled:

```bash
cd docker
./run.sh
```

If you have `grpcurl` installed you can test the mock as follows:

```bash
grpcurl -d '{"name": "Tom" }' -plaintext \
  -proto ExampleServices.proto \
  localhost:8080 com.example.grpc.GreetingService/greeting
```
