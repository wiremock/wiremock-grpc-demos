package org.wiremock.demo.springbootgrpc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import org.wiremock.grpc.EchoServiceOuterClass.EchoResponse;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringbootGrpcApplication.class
)
@EnableWireMock({
        @ConfigureWireMock(
                name = "greeting-service",
                baseUrlProperties = "greeting-service.url",
                portProperties = "greeting-service.port",
                extensionFactories = {Jetty12GrpcExtensionFactory.class}
        )
})
class SpringbootGrpcApplicationTests {

    @InjectWireMock("greeting-service")
    WireMockServer echoWireMockInstance;
    WireMockGrpcService mockEchoService;

    @LocalServerPort
    int serverPort;
    RestClient client;

    @BeforeEach
    void init() {
        mockEchoService = new WireMockGrpcService(
                new WireMock(echoWireMockInstance),
                "org.wiremock.grpc.EchoService"
        );
        client = RestClient.create();
    }

    @Test
    void returns_greeting_from_grpc_service() {
        mockEchoService.stubFor(
                method("echo")
                        .willReturn(message(EchoResponse.newBuilder().setMessage("Hi Tom"))));

        String result = client.get()
                .uri("http://localhost:" + serverPort + "/test-echo?message=whatever")
                .retrieve()
                .body(String.class);

        assertThat(result, is("Hi Tom"));
    }

}
