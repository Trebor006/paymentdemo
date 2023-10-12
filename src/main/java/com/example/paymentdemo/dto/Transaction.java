package com.example.paymentdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private boolean success;
    private String uuid;
    private String identificacion;
    private PagoDto pago;
    private DetalleDeuda detalleDeuda;
}
