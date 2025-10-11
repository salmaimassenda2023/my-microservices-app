package com.example.discovryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscovryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscovryServerApplication.class, args);
    }

}
