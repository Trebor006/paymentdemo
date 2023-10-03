package com.example.paymentdemo;

import com.example.paymentdemo.dto.DetalleDeudaDto;
import com.example.paymentdemo.dto.PagoDto;
import com.example.paymentdemo.dto.UsuarioDto;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BankSimulationRestClient {

  @Value("${bank-simulator.server}")
  private String server;

  @Value("${bank-simulator.port}")
  private String port;

  private String BASE_URL; // Reemplaza con la URL base de tu API

  @PostConstruct
  public void init(){
    BASE_URL = server + ":" + port + "/api";
  }

  // Método para realizar una solicitud GET a un endpoint
  public DetalleDeudaDto obtenerDeudasPorCliente(String identificacion) {
    DetalleDeudaDto responseDto = null;
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(BASE_URL).path("/deudas/byUser/" + identificacion);

    Response response = target.request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 200) {
      String result = response.readEntity(String.class);
      responseDto = new Gson().fromJson(result, DetalleDeudaDto.class);
      System.out.println("Respuesta GET: " + responseDto);
    } else {
      System.err.println("Error en la solicitud GET. Código de estado: " + response.getStatus());
    }

    response.close();
    client.close();
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
    client.close();
  }

  public void realizarPago(PagoDto pago) {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(BASE_URL).path("/pagos"); // Reemplaza con la URL de tu endpoint POST

    Response response =
        target
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(new Gson().toJson(pago), MediaType.APPLICATION_JSON));
    if (response.getStatus() == 200) {
      String jsonResponse = response.readEntity(String.class);
      System.out.println("Respuesta POST: " + jsonResponse);
    } else {
      System.err.println("Error en la solicitud POST. Código de estado: " + response.getStatus());
    }

    response.close();
    client.close();
  }

  public List<UsuarioDto> obtenerClientes() {
    List<UsuarioDto> responseDto = new ArrayList<>();
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(BASE_URL).path("/usuarios");

    Response response = target.request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 200) {
      String result = response.readEntity(String.class);
      var responseDetail = new Gson().fromJson(result, UsuarioDto[].class);
      responseDto = Arrays.stream(responseDetail).toList();
      System.out.println("Respuesta GET: " + responseDto);
    } else {
      System.err.println("Error en la solicitud GET. Código de estado: " + response.getStatus());
    }

    response.close();
    client.close();
    return responseDto;
  }
}
