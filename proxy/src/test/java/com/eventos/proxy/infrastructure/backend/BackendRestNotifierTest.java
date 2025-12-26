package com.eventos.proxy.infrastructure.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BackendRestNotifierTest {

    private static final String BASE_URL = "http://backend";

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private BackendRestNotifier notifier;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        notifier = new BackendRestNotifier(restTemplate, BASE_URL);
    }

    @Test
    void notificarEvento_enviaPostAlBackend() {
        String url = BASE_URL + "/api/eventos/sincronizar";

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        notifier.notificarEvento("mensaje");

        server.verify();
    }
}
