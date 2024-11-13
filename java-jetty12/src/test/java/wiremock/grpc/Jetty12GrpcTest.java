/*
 * Copyright (C) 2023 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wiremock.grpc;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wiremock.grpc.dsl.WireMockGrpc.*;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;
import wiremock.grpc.client.GreetingsClient;

public class Jetty12GrpcTest {

  WireMockGrpcService mockGreetingService;
  ManagedChannel channel;
  GreetingsClient greetingsClient;

  @RegisterExtension
  public static WireMockExtension wm =
      WireMockExtension.newInstance()
          .options(
              wireMockConfig()
                  .dynamicPort()
                  .withRootDirectory("src/test/resources/wiremock")
                  .extensions(new Jetty12GrpcExtensionFactory()))
          .build();

  @BeforeEach
  void init() {
    mockGreetingService =
        new WireMockGrpcService(new WireMock(wm.getPort()), GreetingServiceGrpc.SERVICE_NAME);

    channel = ManagedChannelBuilder.forAddress("localhost", wm.getPort()).usePlaintext().build();
    greetingsClient = new GreetingsClient(channel);
  }

  @AfterEach
  void tearDown() {
    channel.shutdown();
  }

  @Test
  void dynamic_response_via_JSON() {
    mockGreetingService.stubFor(
        method("greeting")
            .willReturn(
                jsonTemplate(
                    "{\n"
                        + "    \"greeting\": \"Hello {{jsonPath request.body '$.name'}}\"\n"
                        + "}")));

    String greeting = greetingsClient.greet("Tom");

    assertThat(greeting, is("Hello Tom"));
  }

  @Test
  void response_from_message() {
    mockGreetingService.stubFor(
        method("greeting")
            .willReturn(message(HelloResponse.newBuilder().setGreeting("Hi Tom from object"))));

    String greeting = greetingsClient.greet("Whatever");

    assertThat(greeting, is("Hi Tom from object"));
  }

  @Test
  void request_matching_with_JSON() {
    mockGreetingService.stubFor(
        method("greeting")
            .withRequestMessage(equalToJson("{ \"name\":  \"Tom\" }"))
            .willReturn(message(HelloResponse.newBuilder().setGreeting("OK"))));

    assertThat(greetingsClient.greet("Tom"), is("OK"));

    assertThrows(StatusRuntimeException.class, () -> greetingsClient.greet("Wrong"));
  }

  @Test
  void request_matching_with_message() {
    mockGreetingService.stubFor(
        method("greeting")
            .withRequestMessage(equalToMessage(HelloRequest.newBuilder().setName(StringValue.of("Tom"))))
            .willReturn(message(HelloResponse.newBuilder().setGreeting("OK"))));

    assertThat(greetingsClient.greet("Tom"), is("OK"));

    StatusRuntimeException exception =
        assertThrows(StatusRuntimeException.class, () -> greetingsClient.greet("Wrong"));
    assertThat(
        exception.getMessage(), is("UNIMPLEMENTED: No matching stub mapping found for gRPC request"));
  }

  @Test
  void non_OK_status() {
    mockGreetingService.stubFor(
        method("greeting").willReturn(Status.FAILED_PRECONDITION, "Failed some blah prerequisite"));

    StatusRuntimeException exception =
        assertThrows(StatusRuntimeException.class, () -> greetingsClient.greet("Whatever"));
    assertThat(exception.getMessage(), is("FAILED_PRECONDITION: Failed some blah prerequisite"));
  }

  @Test
  void streaming_request_unary_response() {
    mockGreetingService.stubFor(
        method("manyGreetingsOneReply")
            .withRequestMessage(equalToMessage(HelloRequest.newBuilder().setName(StringValue.of("Rob")).build()))
            .willReturn(message(HelloResponse.newBuilder().setGreeting("Hi Rob"))));

    assertThat(greetingsClient.manyGreetingsOneReply("Tom", "Uri", "Rob", "Mark"), is("Hi Rob"));
  }

  @Test
  void unary_request_streaming_response() {
    mockGreetingService.stubFor(
        method("oneGreetingManyReplies")
            .willReturn(message(HelloResponse.newBuilder().setGreeting("Hi Tom"))));

    assertThat(greetingsClient.oneGreetingManyReplies("Tom"), hasItem("Hi Tom"));
  }
}
