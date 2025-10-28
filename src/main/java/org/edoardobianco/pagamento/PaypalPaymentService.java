package org.edoardobianco.pagamento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

@ApplicationScoped
@Payment(PaymentType.PAYPAL)
@Default
public class PaypalPaymentService implements PaymentService {
    @Override
    public String processPayment(String paymentDetails) {
        return "Paypal payment processed for: " + paymentDetails;
    }
}
