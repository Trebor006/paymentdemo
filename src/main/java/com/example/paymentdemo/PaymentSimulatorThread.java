package com.example.paymentdemo;

import com.example.paymentdemo.components.response.ApiResponse;
import com.example.paymentdemo.dto.*;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentSimulatorThread extends Thread {

  @Setter private BankSimulationRestClient bankSimulationRestClient;
  @Setter private Random random;
  @Setter private int delaySeconds;
  @Setter private int id;
  @Setter private int maxIntentos;
  @Getter private boolean started = false;
  @Getter private boolean broken;

  private List<Transaction> listTransactions;

  @Override
  public void run() {
    listTransactions = new ArrayList<>();
    broken = false;
    started = true;
    int maxBrokenTransactions = 10;
    int countBrokenTransactions = 0;

    while (true && !broken) {
      UsuarioDto usuarioDto = obtenerUsuarioRandom(UsuariosHelper.usuarios);
      try {
        log.info("Hilo nro: " + id + " Procesando Pago" + usuarioDto.getIdentificacion());
        Transaction transactions = ejecutarPago(usuarioDto.getIdentificacion());
        if (transactions != null) {
          listTransactions.add(transactions);

          log.info(
              "Hilo nro: "
                  + id
                  + " Procesando Pago "
                  + usuarioDto.getIdentificacion()
                  + ", "
                  + transactions.getPago().getMonto());
          countBrokenTransactions = 0;
        } else {
          countBrokenTransactions++;
          if (countBrokenTransactions == maxBrokenTransactions) {
            broken = true;
          }
        }

        log.info("Transacciones realizadas por Hilo nro: " + id + " :: " + listTransactions.size());
        Thread.sleep(delaySeconds * 1000);
      } catch (InterruptedException e) {
        broken = true;
        log.info("Marcando como BROKEN!!!");
        log.info(e.getMessage());
      }
    }
  }

  private UsuarioDto obtenerUsuarioRandom(List<UsuarioDto> usuarios) {
    int pos = random.nextInt(usuarios.size() + 1);

    return usuarios.get(pos);
  }

  private Transaction ejecutarPago(String identificacion) {
    DetalleDeudaDto detalleDeudaDto =
        this.bankSimulationRestClient.obtenerDeudasPorCliente(identificacion);
    boolean retry = false;
    int countRetry = 1;
    Transaction transaction = null;
    if (tieneDeuda(detalleDeudaDto)) {

      try {
        DetalleDeuda detalleDeuda = determinarDeudaPagar(detalleDeudaDto);
        do {
          log.info("Intento de pago nro " + countRetry + " para :" + detalleDeuda.getDeudaId());

          transaction = simularPago(detalleDeuda, UUID.randomUUID().toString());
          transaction.setIdentificacion(identificacion);
          if (!transaction.isSuccess()) {
            if (countRetry <= maxIntentos) {
              retry = true;
              countRetry++;
            }
          }
        } while (retry);

      } catch (Exception exception) {
        return null;
      }
    }
    if (transaction == null || !transaction.isSuccess()) {
      log.info("Deberiamos detallar el caso...");
    }
    return transaction;
  }

  private Transaction reintentarSimularPago(Transaction transaction, String uuid) {
    ApiResponse apiResponse =
        this.bankSimulationRestClient.realizarPago(transaction.getPago(), uuid);

    return Transaction.builder()
        .pago(transaction.getPago())
        .detalleDeuda(transaction.getDetalleDeuda())
        .uuid(uuid)
        .success(apiResponse.isSuccess())
        .build();
  }

  private DetalleDeuda determinarDeudaPagar(DetalleDeudaDto detalleDeudaDto) {
    Optional<DetalleDeuda> first =
        detalleDeudaDto.getDetalle().stream().filter(deuda -> deuda.getSaldo() > 0).findFirst();
    return first.get();
  }

  private Transaction simularPago(DetalleDeuda detalleDeuda, String uuid) {
    Long deudaId = detalleDeuda.getDeudaId();
    Double montoPago = generateRandomNumber(detalleDeuda.getSaldo());

    PagoDto pago = PagoDto.builder().deudaId(deudaId).monto(montoPago).build();
    ApiResponse apiResponse = this.bankSimulationRestClient.realizarPago(pago, uuid);

    return Transaction.builder()
        .pago(pago)
        .detalleDeuda(detalleDeuda)
        .uuid(uuid)
        .success(apiResponse.isSuccess())
        .build();
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
