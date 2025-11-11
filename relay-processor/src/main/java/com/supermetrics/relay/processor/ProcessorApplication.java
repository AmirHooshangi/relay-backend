package com.supermetrics.relay.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.supermetrics.relay.processor")
@EnableJpaRepositories(
    basePackages = "com.supermetrics.relay.processor.repository",
    considerNestedRepositories = true
)
@EntityScan(basePackages = "com.supermetrics.relay.common.entity")
public class ProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }
}

