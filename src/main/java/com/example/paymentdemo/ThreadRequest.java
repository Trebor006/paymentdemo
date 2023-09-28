package com.example.paymentdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThreadRequest {
  @Autowired private BankSimulationRestClient bankSimulationRestClient;
  Random random = new Random();

  @Value("${time.to.sleep.in.seconds}")
  private int delaySeconds;

  @Value("${max.threads}")
  private int maxThreads;

  private List<PaymentSimulatorThread> threads;

  public void executeSimulation() {
    threads = new ArrayList<>();
    UsuariosHelper.usuarios = bankSimulationRestClient.obtenerClientes();
    for (int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread paymentSimulatorThread = new PaymentSimulatorThread();
      paymentSimulatorThread.setRandom(random);
      paymentSimulatorThread.setId(i + 1);
      paymentSimulatorThread.setDelaySeconds(delaySeconds);
      paymentSimulatorThread.setBankSimulationRestClient(bankSimulationRestClient);
      paymentSimulatorThread.start();

      addHilo(paymentSimulatorThread);
    }
  }

  public void addHilo(PaymentSimulatorThread paymentSimulatorThread) {
    threads.add(paymentSimulatorThread);
  }

  public void removeHilo(PaymentSimulatorThread paymentSimulatorThread) {
    threads.remove(paymentSimulatorThread);
  }
}
