package org.edoardobianco.pagamento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentProcessor {
    @Inject
    Instance<PaymentService> paymentServices;

    // Logger for this class using Quarkus/JBoss Logging
    private static final Logger LOG = Logger.getLogger(PaymentProcessor.class);

/**
 * Trata uma mensagem de pagamento determinando o serviço de pagamento
 * apropriado para processar o pagamento com base no tipo de pagamento fornecido.
 *
 * @param paymentTypeString o tipo de pagamento como uma string, que é convertido
 *                          para o enum {@link PaymentType}.
 * @param order             os detalhes do pedido a serem processados pelo serviço de pagamento.
 * @return {@link PaymentResult} contendo sucesso/falha e mensagem explicativa.
 */
    public PaymentResult handleMessage(String paymentTypeString, String order) {
        // Valida parâmetros
        if (paymentTypeString == null || paymentTypeString.isBlank()) {
            LOG.warn("Parâmetro 'type' ausente ou vazio");
            return new PaymentResult(false, "Parâmetro 'type' ausente ou vazio");
        }
        if (order == null || order.isBlank()) {
            LOG.warn("Parâmetro 'order' ausente ou vazio");
            return new PaymentResult(false, "Parâmetro 'order' ausente ou vazio");
        }

        // Converte a string de entrada para o enum PaymentType em maiúsculas
        PaymentType tipoPagamento;
        try {
            tipoPagamento = PaymentType.valueOf(paymentTypeString.toUpperCase());
        } catch (Exception e) {
            LOG.warnf("Tipo de pagamento inválido: %s", paymentTypeString);
            return new PaymentResult(false, "Tipo de pagamento inválido: " + paymentTypeString);
        }
        var selected = paymentServices.select(PaymentFilter.of(tipoPagamento));
        if (selected.isResolvable()) {
            PaymentService svc = selected.get();
            try {
                String processed = svc.processPayment(order);
                LOG.infof("Pagamento Processado: %s", processed);
                return new PaymentResult(true, processed);
            } catch (Exception e) {
                LOG.errorf(e, "Erro ao processar pagamento do tipo %s para order %s", tipoPagamento, order);
                return new PaymentResult(false, "Erro ao processar pagamento: " + e.getMessage());
            }
        } else {
            LOG.warnf("Nenhuma implementação de tipo pagamento encontrada para: %s", tipoPagamento);
            return new PaymentResult(false, "Nenhuma implementação encontrada para: " + tipoPagamento);
        }
    }
}
