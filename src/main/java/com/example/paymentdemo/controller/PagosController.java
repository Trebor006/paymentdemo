package com.example.paymentdemo.controller;

import com.example.paymentdemo.dto.response.DetallePagoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("payments")
public class PagosController {
  @PostMapping("callback")
  public ResponseEntity registrarPago(
      @RequestHeader("uuid") String uuid, @RequestBody DetallePagoResponse detallePagoResponse) {
    log.info("Registrando CALLBACK!!!: ");
    log.info("UUID: " + uuid);
    log.info("Procesando Pago :" + detallePagoResponse);

    return ResponseEntity.ok().build();
  }
}
