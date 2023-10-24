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
  @Setter private int maxIntentos;
  private List<PaymentSimulatorThread> threads;

  @Override
  public void run() {
    initTreadsSimulation();
    boolean blocked = false;
    while (true) {
      try {
        if (!blocked && (UsuariosHelper.usuarios == null || UsuariosHelper.usuarios.size() == 0)) {
          blocked = true;
          log.info("Obteniendo deudas!");
          UsuariosHelper.usuarios = bankSimulationRestClient.obtenerClientes();
//          blocked = false;
//        } else {
//          log.info("Esperando a que retornen las deudas!");
//          if (blocked) {
//            blocked = false;
//          }
//        }
//        if (UsuariosHelper.usuarios != null && UsuariosHelper.usuarios.size() > 0) {
          executeSimulation();
        }
      } catch (Exception exception) {
        log.info("Hubo una excepcion");
        log.info(exception.getMessage());
        blocked = false;
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
    for (int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread hilo = getHilo(i);
      if (!hilo.isStarted()) {
        hilo.start();
      }

      if (hilo.isBroken()) {
        hilo.interrupt();

        PaymentSimulatorThread newPaymentSimulatorThread = initHilo(i);
        threads.set(i, newPaymentSimulatorThread);
        newPaymentSimulatorThread.start();
      }
    }
  }

  public void initTreadsSimulation() {
    threads = new ArrayList<>();
    for (int i = 0; i < maxThreads; i++) {
      PaymentSimulatorThread paymentSimulatorThread = initHilo(i);
      addHilo(paymentSimulatorThread);
    }
  }

  private PaymentSimulatorThread initHilo(int i) {
    PaymentSimulatorThread paymentSimulatorThread = new PaymentSimulatorThread();
    paymentSimulatorThread.setRandom(random);
    paymentSimulatorThread.setId(i + 1);
    paymentSimulatorThread.setDelaySeconds(delaySeconds);
    paymentSimulatorThread.setBankSimulationRestClient(bankSimulationRestClient);
    paymentSimulatorThread.setMaxIntentos(maxIntentos);
    return paymentSimulatorThread;
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
