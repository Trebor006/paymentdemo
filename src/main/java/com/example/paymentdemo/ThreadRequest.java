package com.example.paymentdemo;

import com.example.paymentdemo.dto.DetalleDeuda;
import com.example.paymentdemo.dto.DetalleDeudaDto;
import com.example.paymentdemo.dto.PagoDto;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThreadRequest {
  @Autowired private BankSimulationRestClient bankSimulationRestClient;
  Long MIN = 7000001L;
  Long MAX = 7999999L;
  Random random = new Random();

  public void executeSimulation() {
//    while (true) {
//
//      Thread thread =
//          new Thread(
//              new Runnable() {
//                @Override
//                public void run() {
//                  ejecutarPago();
//                }
//              });
//
//      thread.start();
//    }

    ExecutorService executorService = Executors.newFixedThreadPool(10); // Limita a 10 hilos concurrentes

    while (true) {
      executorService.execute(new Runnable() {
        @Override
        public void run() {
          ejecutarPago();
        }
      });
    }
  }

  private void ejecutarPago() {
    String identificacion = getIdentificacionRandom();
    DetalleDeudaDto detalleDeudaDto =
        bankSimulationRestClient.obtenerDeudasPorCliente(identificacion);
    if (tieneDeuda(detalleDeudaDto)) {
      simularPago(detalleDeudaDto);
    }
  }

  private void simularPago(DetalleDeudaDto detalleDeudaDto) {
    Optional<DetalleDeuda> first =
        detalleDeudaDto.getDetalle().stream()
            .filter(deuda -> deuda.getMontoDeuda() > deuda.getMontoPagos())
            .findFirst();
    DetalleDeuda detalleDeuda = first.get();
    Long deudaId = detalleDeuda.getDeudaId();
    double montoDeuda = detalleDeuda.getMontoDeuda() - detalleDeuda.getMontoPagos();
    Double montoPago = generateRandomNumber(montoDeuda);

    bankSimulationRestClient.realizarPago(
        PagoDto.builder().deudaId(deudaId).monto(montoPago).build());
  }

  private boolean tieneDeuda(DetalleDeudaDto detalleDeudaDto) {
    return !detalleDeudaDto.getDetalle().isEmpty()
        && detalleDeudaDto.getDetalle().stream()
            .anyMatch(deuda -> deuda.getMontoDeuda() > deuda.getMontoPagos());
  }

  private String getIdentificacionRandom() {
    return generateRandomNumber(MIN, MAX) + "";
  }

  private Double generateRandomNumber(double maxX) {
    int minValue = 100; // Valor mínimo
    Double randomNumber = random.nextDouble(maxX - minValue + 1) + minValue;
    return randomNumber;
  }

  private static long generateRandomNumber(long min, long max) {
    Random random = new Random();
    return (random.nextLong() % (max - min + 1)) + min;
  }
}
