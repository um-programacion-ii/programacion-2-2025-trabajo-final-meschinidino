package com.eventos.proxy.infrastructure.catedra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CatedraRestClientTest {

    private static final String BASE_URL = "http://catedra";
    private static final String TOKEN = "token";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private CatedraRestClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        client = new CatedraRestClient(restTemplate, BASE_URL, TOKEN);
    }

    @Test
    void obtenerEventosResumidos_enviaHeadersYRetornaLista() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/eventos-resumidos";
        String body = objectMapper.writeValueAsString(List.of(Map.of("id", 1)));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = client.obtenerEventosResumidos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("id")).isEqualTo(1);
        server.verify();
    }

    @Test
    void obtenerEventosCompletos_enviaHeadersYRetornaLista() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/eventos";
        String body = objectMapper.writeValueAsString(List.of(Map.of("id", 2)));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = client.obtenerEventosCompletos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("id")).isEqualTo(2);
        server.verify();
    }

    @Test
    void obtenerEvento_enviaHeadersYRetornaMapa() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/evento/5";
        String body = objectMapper.writeValueAsString(Map.of("id", 5));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        Map<String, Object> result = client.obtenerEvento(5L);

        assertThat(result.get("id")).isEqualTo(5);
        server.verify();
    }

    @Test
    void bloquearAsientos_enviaHeadersYBodyYRetornaMapa() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/bloquear-asientos";
        Map<String, Object> request = Map.of("eventoId", 7);
        String requestBody = objectMapper.writeValueAsString(request);
        String responseBody = objectMapper.writeValueAsString(Map.of("ok", true));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(requestBody))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Map<String, Object> result = client.bloquearAsientos(request);

        assertThat(result.get("ok")).isEqualTo(true);
        server.verify();
    }

    @Test
    void realizarVenta_enviaHeadersYBodyYRetornaMapa() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/realizar-venta";
        Map<String, Object> request = Map.of("eventoId", 9);
        String requestBody = objectMapper.writeValueAsString(request);
        String responseBody = objectMapper.writeValueAsString(Map.of("ventaId", 123));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(requestBody))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        Map<String, Object> result = client.realizarVenta(request);

        assertThat(result.get("ventaId")).isEqualTo(123);
        server.verify();
    }

    @Test
    void listarVentas_enviaHeadersYRetornaLista() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/listar-ventas";
        String body = objectMapper.writeValueAsString(List.of(Map.of("ventaId", 1)));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = client.listarVentas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("ventaId")).isEqualTo(1);
        server.verify();
    }

    @Test
    void obtenerVenta_enviaHeadersYRetornaMapa() throws Exception {
        String url = BASE_URL + "/api/endpoints/v1/listar-venta/4";
        String body = objectMapper.writeValueAsString(Map.of("ventaId", 4));

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        Map<String, Object> result = client.obtenerVenta(4L);

        assertThat(result.get("ventaId")).isEqualTo(4);
        server.verify();
    }
}
