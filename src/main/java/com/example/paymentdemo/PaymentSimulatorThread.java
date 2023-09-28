package com.example.paymentdemo;

import com.example.paymentdemo.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentSimulatorThread extends Thread {

  @Setter private BankSimulationRestClient bankSimulationRestClient;
  @Setter private Random random;
  @Setter private int delaySeconds;
  @Setter private int id;

  private List<TransactionsDone> listTransactions;

  @Override
  public void run() {
    listTransactions = new ArrayList<>();

    while (true) {
      UsuarioDto usuarioDto = obtenerUsuarioRandom(UsuariosHelper.usuarios);
      try {
        log.info("Hilo nro: " + id + " Procesando Pago" + usuarioDto.getIdentificacion());
        TransactionsDone transactions = ejecutarPago(usuarioDto.getIdentificacion());
        if (transactions != null) {
          listTransactions.add(transactions);

          log.info("Hilo nro: " + id + " Procesando Pago " + usuarioDto.getIdentificacion()
                  + ", " + transactions.getPago().getMonto());
        }


        log.info("Transacciones realizadas por Hilo nro: " + id + " :: " + listTransactions.size());
        Thread.sleep(delaySeconds * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private UsuarioDto obtenerUsuarioRandom(List<UsuarioDto> usuarios) {
    int pos = random.nextInt(usuarios.size() + 1);

    return usuarios.get(pos);
  }

  private TransactionsDone ejecutarPago(String identificacion) {
    DetalleDeudaDto detalleDeudaDto =
        this.bankSimulationRestClient.obtenerDeudasPorCliente(identificacion);
    if (tieneDeuda(detalleDeudaDto)) {
      TransactionsDone transactionsDone = simularPago(detalleDeudaDto);
      transactionsDone.setIdentificacion(identificacion);

      return transactionsDone;
    }
    return null;
  }

  private TransactionsDone simularPago(DetalleDeudaDto detalleDeudaDto) {
    Optional<DetalleDeuda> first =
        detalleDeudaDto.getDetalle().stream().filter(deuda -> deuda.getSaldo() > 0).findFirst();
    DetalleDeuda detalleDeuda = first.get();
    Long deudaId = detalleDeuda.getDeudaId();
    Double montoPago = generateRandomNumber(detalleDeuda.getSaldo());

    PagoDto pago = PagoDto.builder().deudaId(deudaId).monto(montoPago).build();
    this.bankSimulationRestClient.realizarPago(pago);

    return TransactionsDone.builder().pago(pago).detalleDeuda(detalleDeuda).build();
  }

  private boolean tieneDeuda(DetalleDeudaDto detalleDeudaDto) {
    return !detalleDeudaDto.getDetalle().isEmpty()
        && detalleDeudaDto.getDetalle().stream().anyMatch(deuda -> deuda.getSaldo() > 0);
  }

  private Double generateRandomNumber(double maxX) {
    if (maxX < 500) {
      return maxX;
    }

    int minValue = 100; // Valor mínimo
    Double randomNumber = this.random.nextDouble(maxX - minValue + 1) + minValue;
    return randomNumber;
  }

  private static long generateRandomNumber(long min, long max) {
    Random random = new Random();
    return (random.nextLong() % (max - min + 1)) + min;
  }
}
