package org.wiremock.demo.springbootgrpc;

import com.example.grpc.GreetingServiceGrpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;

@SpringBootApplication
public class SpringbootGrpcApplication {

	@Bean
	GreetingServiceGrpc.GreetingServiceBlockingStub greetings(GrpcChannelFactory channels) {
		return GreetingServiceGrpc.newBlockingStub(channels.createChannel("local").build());
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootGrpcApplication.class, args);
	}

}
