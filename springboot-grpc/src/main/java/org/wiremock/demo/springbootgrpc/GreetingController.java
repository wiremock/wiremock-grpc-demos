package org.wiremock.demo.springbootgrpc;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.google.protobuf.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
    
    @Autowired
    GreetingServiceGrpc.GreetingServiceBlockingStub greetings;

    @GetMapping("/greeting")
    public String getGreeting(@RequestParam String name) {
        final HelloResponse response = greetings.greeting(HelloRequest.newBuilder().setName(StringValue.of(name)).build());
        return response.getGreeting();
    }
}