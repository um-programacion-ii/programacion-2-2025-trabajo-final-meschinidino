package com.eventos.proxy.infrastructure.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.eventos.proxy.security.ServiceJwtProvider;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.assertj.core.api.Assertions.assertThat;

class BackendRestNotifierTest {

    private static final String BASE_URL = "http://backend";

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private BackendRestNotifier notifier;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        ServiceJwtProvider jwtProvider = new ServiceJwtProvider(
                "change-me",
                "eventos-proxy",
                "eventos-backend",
                300
        );
        notifier = new BackendRestNotifier(restTemplate, BASE_URL, "dummy-token", jwtProvider);
    }

    @Test
    void notificarEvento_enviaPostAlBackend() {
        String url = BASE_URL + "/api/sync/webhook";

        server.expect(requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> {
                    String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    assertThat(auth).isNotNull();
                    assertThat(auth).startsWith("Bearer ");
                })
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.header("X-Webhook-Token", "dummy-token"))
                .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        notifier.notificarEvento("mensaje");

        server.verify();
    }
}
