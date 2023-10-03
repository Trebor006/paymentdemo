package com.example.paymentdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadRequest extends Thread {

  @Setter private BankSimulationRestClient bankSimulationRestClient;
  @Setter private Random random;
  @Setter private int delaySeconds;
  @Setter private int maxThreads;
  private List<PaymentSimulatorThread> threads;

  @Override
  public void run() {
    initTreadsSimulation();
    while (true) {
      try {
        UsuariosHelper.usuarios = bankSimulationRestClient.obtenerClientes();
        if (UsuariosHelper.usuarios.size() > 0) {
          executeSimulation();
        }
      } catch (Exception exception) {
      } finally {
        try {
          Thread.sleep(delaySeconds * 1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private void executeSimulation() {
    for(int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread hilo = getHilo(i);
      hilo.start();
    }
//    validateThreads();
  }

  public void initTreadsSimulation() {
    threads = new ArrayList<>();
    for (int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread paymentSimulatorThread = new PaymentSimulatorThread();
      paymentSimulatorThread.setRandom(random);
      paymentSimulatorThread.setId(i + 1);
      paymentSimulatorThread.setDelaySeconds(delaySeconds);
      paymentSimulatorThread.setBankSimulationRestClient(bankSimulationRestClient);
      addHilo(paymentSimulatorThread);
    }
  }

  private void validateThreads() {
    for (int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread hilo = getHilo(i);
      if (hilo.isInterrupted()) {
        removeHilo(hilo);
      }
    }
  }

  public void addHilo(PaymentSimulatorThread paymentSimulatorThread) {
    threads.add(paymentSimulatorThread);
  }

  public void removeHilo(PaymentSimulatorThread paymentSimulatorThread) {
    threads.remove(paymentSimulatorThread);
  }

  public PaymentSimulatorThread getHilo(int index) {
    return threads.get(index);
  }
}
