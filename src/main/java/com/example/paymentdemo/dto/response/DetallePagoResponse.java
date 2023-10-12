package com.example.paymentdemo.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePagoResponse {
  private Long pagoId;
  private DetallePagoDeudaResponse detalleDeuda;
  private BigDecimal monto;
  private String uuid;
}
