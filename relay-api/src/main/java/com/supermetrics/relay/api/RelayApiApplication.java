package com.supermetrics.relay.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.supermetrics.relay.api.repository")
@EntityScan(basePackages = "com.supermetrics.relay.common.entity")
public class RelayApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RelayApiApplication.class, args);
    }
}

