package com.example.paymentdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDeuda {

  @JsonProperty("deudaId")
  private Long deudaId;

  @JsonProperty("montoDeuda")
  private Double montoDeuda;

  @JsonProperty("montoPagos")
  private Double montoPagos;
}
