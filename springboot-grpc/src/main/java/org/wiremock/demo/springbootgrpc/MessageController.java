package org.wiremock.demo.springbootgrpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wiremock.grpc.EchoServiceGrpc;
import org.wiremock.grpc.EchoServiceOuterClass;
import org.wiremock.grpc.EchoServiceOuterClass.EchoResponse;

@RestController
public class MessageController {
    
    @Autowired
    EchoServiceGrpc.EchoServiceBlockingStub echo;

    @GetMapping("/test-echo")
    public String getEchoedMessage(@RequestParam String message) {
        final EchoResponse response = echo.echo(EchoServiceOuterClass.EchoRequest.newBuilder().setMessage(message).build());
        return response.getMessage();
    }
}