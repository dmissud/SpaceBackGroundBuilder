package org.dbs.spgb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SwaggerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSwaggerUiAccess() {
        webTestClient.get().uri("/swagger-ui.html").exchange().expectStatus().isFound();
    }

    @Test
    void testApiDocsAccess() {
        webTestClient.get().uri("/v3/api-docs").exchange().expectStatus().isOk();
    }

    @Test
    void testRootRedirection() {
        webTestClient.get().uri("/").exchange().expectStatus().isFound().expectHeader().valueMatches("Location", ".*/swagger-ui.html");
    }

    @Test
    void testErrorEndpoint() {
        webTestClient.get().uri("/error").exchange().expectStatus().is5xxServerError().expectBody().jsonPath("$.status").exists().jsonPath("$.message").exists();
    }
}
