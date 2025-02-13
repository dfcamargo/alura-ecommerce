package br.com.dfc.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public interface IConsumerFunction<T> {
    void consume(ConsumerRecord<String, T> record) throws Exception;
}
