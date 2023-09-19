package com.example.paymentdemo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentdemoApplication.class, args);
    }

    @Autowired
    private ThreadRequest threadRequest;

    @PostConstruct
    public void execute(){
        threadRequest.executeSimulation();
    }
}
