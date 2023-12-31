package com.example.paymentdemo;

import com.example.paymentdemo.dto.DetalleDeudaDto;
import com.example.paymentdemo.dto.PagoDto;
import com.google.gson.Gson;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

@Component
public class BankSimulationRestClient {
  private static final String BASE_URL =
      "http://localhost:8080/api"; // Reemplaza con la URL base de tu API

  // Método para realizar una solicitud GET a un endpoint
  public DetalleDeudaDto obtenerDeudasPorCliente(String identificacion) {
    DetalleDeudaDto responseDto = null;
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(BASE_URL).path("/deuda/byUser/" + identificacion);

    Response response = target.request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 200) {
      String result = response.readEntity(String.class);
      responseDto = new Gson().fromJson(result, DetalleDeudaDto.class);
      System.out.println("Respuesta GET: " + responseDto);
    } else {
      System.err.println("Error en la solicitud GET. Código de estado: " + response.getStatus());
    }

    response.close();
    return responseDto;
  }

  // Método para realizar una solicitud POST a un endpoint
  public void performPostRequest() {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(BASE_URL).path("/endpoint"); // Reemplaza con la URL de tu endpoint POST

    // Datos a enviar en la solicitud POST (pueden ser objetos JSON, form parameters, etc.)
    String jsonData = "{\"key\": \"value\"}";

    Response response =
        target
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(jsonData, MediaType.APPLICATION_JSON));

    if (response.getStatus() == 201) {
      String jsonResponse = response.readEntity(String.class);
      System.out.println("Respuesta POST: " + jsonResponse);
    } else {
      System.err.println("Error en la solicitud POST. Código de estado: " + response.getStatus());
    }

    response.close();
  }

  public void realizarPago(PagoDto pago) {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(BASE_URL).path("/pago"); // Reemplaza con la URL de tu endpoint POST

    Response response =
        target
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(new Gson().toJson(pago), MediaType.APPLICATION_JSON));
    if (response.getStatus() == 201) {
      String jsonResponse = response.readEntity(String.class);
      System.out.println("Respuesta POST: " + jsonResponse);
    } else {
      System.err.println("Error en la solicitud POST. Código de estado: " + response.getStatus());
    }

    response.close();
  }
}
