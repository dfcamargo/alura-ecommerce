package br.com.dfc.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            try(KafkaDispatcher<Mail> mailDispatcher = new KafkaDispatcher<>()) {
                String userEmail = String.format("%s@email.com", UUID.randomUUID());

                for (int i = 0; i < 10; i++) {
                    String idOrder = UUID.randomUUID().toString();
                    BigDecimal amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    Order order = new Order(idOrder, amount, userEmail);

                    String body = String.format("Thanks for you order! We are processing now! Your order: %s", idOrder);
                    Mail email = new Mail("New Order", body);

                    // save order
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, order);
                    // sending mail..
                    mailDispatcher.send("ECOMMERCE_SEND_MAIL", userEmail, email);
                }
            }
        }
    }


}
