package org.edoardobianco.pagamento;

import jakarta.enterprise.util.AnnotationLiteral;

import java.lang.annotation.Annotation;

public class PaymentFilter extends AnnotationLiteral<Payment> implements Payment {

    private final PaymentType type;

    private PaymentFilter(PaymentType type) {
        this.type = type;
    }

    @Override
    public PaymentType value() {
        return type;
    }

    public static PaymentFilter of(PaymentType type) {
        return new PaymentFilter(type);
    }
}
