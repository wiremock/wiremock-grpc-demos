package org.wiremock.demo.springbootgrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wiremock.demo.springbootgrpc.client.GreetingsClient;

import javax.annotation.PostConstruct;
import java.net.URI;

@RestController
public class GreetingController {

    private GreetingsClient greetingsClient;

    @Value("${greeting-service.url}")
    URI greetingServiceBaseUrl;

    @PostConstruct
    private void init() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(
                    greetingServiceBaseUrl.getHost(),
                    greetingServiceBaseUrl.getPort()
                )
                .usePlaintext()
                .build();

        greetingsClient = new GreetingsClient(channel);
    }

    @GetMapping("/greeting")
    public String getGreeting(@RequestParam String name) {
        return greetingsClient.greet(name);
    }
}