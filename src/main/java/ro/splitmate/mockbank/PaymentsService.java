package ro.splitmate.mockbank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class PaymentsService {
    private static final Logger log = LoggerFactory.getLogger(PaymentsService.class);
    private final Map<UUID, PaymentsController.Payment> payments = new ConcurrentHashMap<>();

    public PaymentsController.Payment save(Requests.PaymentCreate paymentCreate) {
        UUID internalReference = UUID.randomUUID();
        PaymentsController.Payment payment = new PaymentsController.Payment(internalReference, paymentCreate, PaymentStatus.New);
        payments.put(internalReference, payment);
        return payment;
    }
    public PaymentsController.Payment update(UUID paymentId, PaymentsController.UpdateStatusRequest update) {
        PaymentStatus newStatus = update.status();
        PaymentsController.Payment payment = findById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }
        PaymentsController.Payment updated = new PaymentsController.Payment(paymentId, payment.request(), newStatus);
        payments.put(paymentId, updated);
        log.info("update - payment: {} moved to status: {}", paymentId, update.status());
        return updated;
    }

    public PaymentsController.Payment findById(UUID paymentId) {
        return payments.get(paymentId);
    }
    public Stream<PaymentsController.Payment> findAll() {
        return payments.values()
                .stream();
    }
}
