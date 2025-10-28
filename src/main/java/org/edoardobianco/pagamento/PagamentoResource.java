package org.edoardobianco.pagamento;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;

@Path("/pagamento")
public class PagamentoResource {

    @Inject
    PaymentProcessor paymentProcessor;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentResult hello(@QueryParam("type") String type, @QueryParam("order") String order) {
        // Chamada ao processador de pagamento com parâmetros fornecidos na query
        return paymentProcessor.handleMessage(type, order);
    }
}
