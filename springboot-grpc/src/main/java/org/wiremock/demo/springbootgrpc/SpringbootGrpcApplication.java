package org.wiremock.demo.springbootgrpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.wiremock.grpc.EchoServiceGrpc;

@SpringBootApplication
public class SpringbootGrpcApplication {

	@Bean
	EchoServiceGrpc.EchoServiceBlockingStub echo(GrpcChannelFactory channels) {
		return EchoServiceGrpc.newBlockingStub(channels.createChannel("local").build());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootGrpcApplication.class, args);
	}

}
