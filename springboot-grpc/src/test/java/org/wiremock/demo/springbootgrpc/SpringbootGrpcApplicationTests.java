package org.wiremock.demo.springbootgrpc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import org.wiremock.grpc.EchoServiceGrpc;
import org.wiremock.grpc.EchoServiceOuterClass.EchoResponse;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.wiremock.grpc.dsl.WireMockGrpc.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SpringbootGrpcApplication.class
)
@EnableWireMock({
    @ConfigureWireMock(
        name = "echo-service",
        portProperties = "echo-service.port",
        extensionFactories = { Jetty12GrpcExtensionFactory.class }
    )
})
class SpringbootGrpcApplicationTests {

    @InjectWireMock("echo-service")
    WireMockServer echoWireMockInstance;
    WireMockGrpcService mockEchoService;

    @LocalServerPort
    int serverPort;
    RestClient client;

    @BeforeEach
    void init() {
        mockEchoService = new WireMockGrpcService(
                new WireMock(echoWireMockInstance),
                EchoServiceGrpc.SERVICE_NAME
        );
        client = RestClient.create();
    }

    @Test
    void returns_canned_message_from_grpc_service() {
        mockEchoService.stubFor(
                method("echo")
                        .willReturn(message(
                                EchoResponse.newBuilder()
                                .setMessage("Hi Tom")
                        )));

        String url = "http://localhost:" + serverPort + "/test-echo?message=blah";
        String result = client.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        assertThat(result, is("Hi Tom"));
    }

    @Test
    void returns_dynamic_message_from_grpc_service() {
        mockEchoService.stubFor(
            method("echo").willReturn(
                jsonTemplate(
                    "{\"message\":\"{{jsonPath request.body '$.message'}}\"}"
                )));

        String url = "http://localhost:" + serverPort + "/test-echo?" +
                     "message=my-messsage";

        String result = client.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        assertThat(result, is("my-messsage"));
    }

}
