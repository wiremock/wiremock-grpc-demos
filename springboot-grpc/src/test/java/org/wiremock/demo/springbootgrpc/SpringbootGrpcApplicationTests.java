package org.wiremock.demo.springbootgrpc;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
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
                configurationCustomizers = GrpcConfigurationCustomizer.class
        )
})
class SpringbootGrpcApplicationTests {

    @InjectWireMock("greeting-service")
    WireMockServer mockGrpcGreetingService;
    WireMockGrpcService greetingService;

    @LocalServerPort
    int serverPort;
    RestClient client;

    @BeforeEach
    void init() {
        greetingService = new WireMockGrpcService(
                new WireMock(mockGrpcGreetingService),
                "com.example.grpc.GreetingService"
        );
        client = RestClient.create();
    }

    @Test
    void returns_greeting_from_grpc_service() {
        greetingService.stubFor(
                method("greeting")
                        .willReturn(message(HelloResponse.newBuilder().setGreeting("Hi Tom"))));

        String result = client.get()
                .uri("http://localhost:" + serverPort + "/greeting?name=whatever")
                .exchange((clientRequest, clientResponse) -> clientResponse.bodyTo(String.class));

        assertThat(result, is("Hi Tom"));
    }

}
