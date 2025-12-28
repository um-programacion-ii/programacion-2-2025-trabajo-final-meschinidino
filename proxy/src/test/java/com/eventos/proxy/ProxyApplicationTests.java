package com.eventos.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "CATEDRA_HOST=example.com",
        "CATEDRA_URL=http://example.com",
        "CATEDRA_TOKEN=test-token",
        "CATEDRA_REDIS_PORT=6379",
        "SYNC_WEBHOOK_TOKEN=test-webhook",
        "SERVICE_JWT_SECRET=test-secret",
        "spring.kafka.consumer.group-id=test-group",
        "spring.kafka.listener.auto-startup=false"
})
class ProxyApplicationTests {

    @Test
    void contextLoads() {
    }

}
