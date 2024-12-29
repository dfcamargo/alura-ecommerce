package br.com.dfc.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;


public class MailService {

    public static void main(String[] args) {
        MailService mailService = new MailService();
        try(KafkaService<Mail> service = new KafkaService<>(MailService.class.getSimpleName(),
                "ECOMMERCE_SEND_MAIL",
                mailService::parse,
                Mail.class,
                Map.of())) {
            service.run();
        };
    }

    private void parse(ConsumerRecord<String, Mail> record) {
        System.out.println("--------------------------------");
        System.out.println("Sending email...");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignoring...
            e.printStackTrace();
        }
        System.out.println("Email sent!");
    }
}
