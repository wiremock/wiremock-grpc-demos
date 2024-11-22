package org.wiremock.demo.springbootgrpc;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.WireMockConfigurationCustomizer;

public class GrpcConfigurationCustomizer implements WireMockConfigurationCustomizer {

    @Override
    public void customize(WireMockConfiguration configuration, ConfigureWireMock options) {
        configuration.extensions(new Jetty12GrpcExtensionFactory());
    }
}
