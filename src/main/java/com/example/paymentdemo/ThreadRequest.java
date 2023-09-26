package com.example.paymentdemo;

import com.example.paymentdemo.dto.DetalleDeuda;
import com.example.paymentdemo.dto.DetalleDeudaDto;
import com.example.paymentdemo.dto.PagoDto;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.example.paymentdemo.dto.UsuarioDto;
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

  public void executeSimulation() {
    List<UsuarioDto> usuarios = bankSimulationRestClient.obtenerClientes();

    while (true) {
      UsuarioDto usuarioDto = obtenerUsuarioRandom(usuarios);
      try {
        log.info("Procesando Pago" + usuarioDto.getIdentificacion());
        ejecutarPago(usuarioDto.getIdentificacion());

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

  private void ejecutarPago(String identificacion) {
    DetalleDeudaDto detalleDeudaDto =
        bankSimulationRestClient.obtenerDeudasPorCliente(identificacion);
    if (tieneDeuda(detalleDeudaDto)) {
      simularPago(detalleDeudaDto);
    }
  }

  private void simularPago(DetalleDeudaDto detalleDeudaDto) {
    Optional<DetalleDeuda> first =
        detalleDeudaDto.getDetalle().stream()
            .filter(deuda -> deuda.getSaldo() > 0 )
            .findFirst();
    DetalleDeuda detalleDeuda = first.get();
    Long deudaId = detalleDeuda.getDeudaId();
    Double montoPago = generateRandomNumber(detalleDeuda.getSaldo());

    bankSimulationRestClient.realizarPago(
        PagoDto.builder().deudaId(deudaId).monto(montoPago).build());
  }

  private boolean tieneDeuda(DetalleDeudaDto detalleDeudaDto) {
    return !detalleDeudaDto.getDetalle().isEmpty()
        && detalleDeudaDto.getDetalle().stream()
            .anyMatch(deuda -> deuda.getSaldo() > 0);
  }

  private Double generateRandomNumber(double maxX) {
    if (maxX < 500){
      return maxX;
    }

    int minValue = 100; // Valor mínimo
    Double randomNumber = random.nextDouble(maxX - minValue + 1) + minValue;
    return randomNumber;
  }

  private static long generateRandomNumber(long min, long max) {
    Random random = new Random();
    return (random.nextLong() % (max - min + 1)) + min;
  }
}
