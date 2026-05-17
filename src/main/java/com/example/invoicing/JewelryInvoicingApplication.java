package com.example.invoicing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JewelryInvoicingApplication {

    public static void main(String[] args) {
        SpringApplication.run(JewelryInvoicingApplication.class, args);
    }
}
