package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wiremock.grpc.GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.wiremock.grpc.dsl.WireMockGrpc.json;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

@Configuration
public class WiremockConfiguration {

    @Bean
    public WireMockServer grpcWireMockServer() {
        var server = startGrpcWiremockServer();

        return server;
    }

    @Bean
    public WireMockGrpcService marginReportHistoryServiceGrpc(@Qualifier("grpcWireMockServer") WireMockServer grpcWireMockServer) {
        var mockService = new WireMockGrpcService(new WireMock(grpcWireMockServer.port()), "com.example.grpc.GreetingService");

        System.out.println("Port: " + grpcWireMockServer.port());

        mockService.stubFor(
                method("greeting")
                        .withRequestMessage(equalToJson("{ \"name\":  \"Tom\" }"))
                        .willReturn(json("{ \"greeting\": \"Hi Tom from JSON\" }"))
        );

        return mockService;
    }

    public WireMockServer startGrpcWiremockServer() {
        var server = new WireMockServer(wireMockConfig()
                .dynamicPort()
                .withRootDirectory("src/main/resources/wiremock")
                .extensions(new GrpcExtensionFactory())
        );

        server.start();

        return server;
    }
}
