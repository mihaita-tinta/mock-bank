package ro.splitmate.mockbank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class AutoConfirmScheduler {
    private static final Logger log = LoggerFactory.getLogger(AutoConfirmScheduler.class);
    private final PaymentsService paymentsService;

    @Value("${mock-bank.autoUpdateStatus: Done}")
    private PaymentStatus autoUpdateStatus;
    public AutoConfirmScheduler(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void schedule() {
        List<PaymentsController.Payment> list = paymentsService.findAll()
                .filter(p -> p.status() != PaymentStatus.Done)
                .toList();
        log.info("schedule - found {} payments to auto-confirm", list.size());
        var toStatusDone = new PaymentsController.UpdateStatusRequest(autoUpdateStatus);
        list.forEach(p -> paymentsService.update(p.id(), toStatusDone));
    }
}
