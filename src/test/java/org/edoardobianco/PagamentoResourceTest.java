package org.edoardobianco;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class PagamentoResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/pagamento?type=PAYPAL&order=order-123")
          .then()
             .statusCode(200)
             .body("success", notNullValue())
             .body("message", notNullValue());
    }

    @Test
    void testHelloCreditCardEndpoint() {
        given()
                .when().get("/pagamento?type=CREDIT_CARD&order=order-124")
                .then()
                .statusCode(200)
                .body("success", notNullValue())
                .body("message", notNullValue());
    }

}