package com.example.paymentdemo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class PaymentdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentdemoApplication.class, args);
    }

    @Autowired private BankSimulationRestClient bankSimulationRestClient;


    @Value("${time.to.sleep.in.seconds}")
    private int delaySeconds;

    @Value("${max.threads}")
    private int maxThreads;

    @PostConstruct
    public void execute(){
        Random random = new Random();
        ThreadRequest threadRequest = new ThreadRequest();
        threadRequest.setBankSimulationRestClient(bankSimulationRestClient);
        threadRequest.setRandom(random);
        threadRequest.setDelaySeconds(delaySeconds);
        threadRequest.setMaxThreads(maxThreads);

        threadRequest.start();
    }
}
