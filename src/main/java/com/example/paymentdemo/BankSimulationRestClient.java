package com.example.paymentdemo;

import com.example.paymentdemo.components.response.ApiResponse;
import com.example.paymentdemo.dto.DetalleDeudaDto;
import com.example.paymentdemo.dto.PagoDto;
import com.example.paymentdemo.dto.UsuarioDto;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class BankSimulationRestClient {

  @Value("${bank-simulator.server}")
  private String server;

  @Value("${server.host}")
  private String serverHost;

  @Value("${server.port}")
  private String serverPort;

  @Value("${bank-simulator.port}")
  private String port;

  @Value("${bank-simulator.timeout}")
  private Integer timeout;

  private String BASE_URL; // Reemplaza con la URL base de tu API

  @Autowired RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    BASE_URL = server + ":" + port + "/api";
  }

  // Método para realizar una solicitud GET a un endpoint
  public DetalleDeudaDto obtenerDeudasPorCliente(String identificacion) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("uuid", UUID.randomUUID().toString());
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<DetalleDeudaDto> response =
          restTemplate.exchange(
              BASE_URL + "/deudas/byUser/" + identificacion,
              HttpMethod.GET,
              entity,
              DetalleDeudaDto.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        System.err.println(
            "Error en la solicitud GET. Código de estado: " + response.getStatusCode());
        return null;
      }

    } catch (ResourceAccessException e) {
      System.err.println(
          "No se pudo establecer conexión con el servidor backend: " + e.getMessage());
      return null;
    }
  }

//  // Método para realizar una solicitud POST a un endpoint
//  public void performPostRequest() {
//    ClientBuilder clientBuilder = ClientBuilder.newBuilder();
//    clientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
//    clientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
//    Client client = clientBuilder.build();
//    WebTarget target =
//        client.target(BASE_URL).path("/endpoint"); // Reemplaza con la URL de tu endpoint POST
//
//    // Datos a enviar en la solicitud POST (pueden ser objetos JSON, form parameters, etc.)
//    String jsonData = "{\"key\": \"value\"}";
//
//    Response response =
//        target
//            .request(MediaType.APPLICATION_JSON)
//            .header("uuid", UUID.randomUUID().toString())
//            .post(Entity.entity(jsonData, MediaType.APPLICATION_JSON));
//
//    if (response.getStatus() == 201) {
//      String jsonResponse = response.readEntity(String.class);
//      System.out.println("Respuesta POST: " + jsonResponse);
//    } else {
//      System.err.println("Error en la solicitud POST. Código de estado: " + response.getStatus());
//    }
//
//    response.close();
//    client.close();
//  }

  public ApiResponse realizarPago(PagoDto pago, String uuid) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("uuid", uuid);
    headers.set("callbackUri", serverHost + ":" + serverPort + "/payments/callback");

    HttpEntity<String> entity = new HttpEntity<>(new Gson().toJson(pago), headers);

    try {
      ResponseEntity<ApiResponse> response = restTemplate.exchange(
              BASE_URL + "/pagos",
              HttpMethod.POST,
              entity,
              ApiResponse.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ApiResponse apiResponse = response.getBody();
        System.out.println("Respuesta POST: " + new Gson().toJson(apiResponse));
        return apiResponse;
      } else {
        System.err.println("Error en la solicitud POST. Código de estado: " + response.getStatusCode());
        return null;  // O puedes devolver un ApiResponse con un mensaje de error si prefieres
      }

    } catch (ResourceAccessException e) {
      System.err.println("No se pudo establecer conexión con el servidor backend: " + e.getMessage());
      return null;  // O puedes devolver un ApiResponse con un mensaje de error si prefieres
    }
  }

  public List<UsuarioDto> obtenerClientes() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<UsuarioDto[]> response = restTemplate.exchange(
              BASE_URL + "/usuarios",
              HttpMethod.GET,
              entity,
              UsuarioDto[].class);

      if (response.getStatusCode().is2xxSuccessful()) {
        UsuarioDto[] usuariosArray = response.getBody();
        List<UsuarioDto> usuariosList = Arrays.asList(usuariosArray);
        System.out.println("Respuesta GET: " + usuariosList);
        return usuariosList;
      } else {
        System.err.println("Error en la solicitud GET. Código de estado: " + response.getStatusCode());
        return new ArrayList<>();
      }

    } catch (ResourceAccessException e) {
      System.err.println("No se pudo establecer conexión con el servidor backend: " + e.getMessage());
      return new ArrayList<>();
    }
  }
}
