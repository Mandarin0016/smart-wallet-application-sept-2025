package app.gift;

import app.event.SuccessfulChargeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GiftService {

    @Async
    @Order(1)
    @EventListener
    public void sendGift(SuccessfulChargeEvent event) {

        String threadName = Thread.currentThread().getName();
        System.out.println("Thread in GiftService.java: " + threadName);
//        System.out.printf("Sending 1 € for charge compensation for user with email [%s].", event.getEmail());
    }
}
