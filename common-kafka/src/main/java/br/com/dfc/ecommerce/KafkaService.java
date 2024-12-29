package br.com.dfc.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final IConsumerFunction<T> parse;

    private KafkaService(String groupId, IConsumerFunction<T> parse, Class<T> type, Map<String,String> properties) {
        this.consumer = new KafkaConsumer<>(this.getProperties(groupId, type, properties));
        this.parse = parse;
    }

    public KafkaService(String groupId, String topic, IConsumerFunction<T> parse, Class<T> type, Map<String,String> properties) {
        this(groupId, parse, type, properties);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, IConsumerFunction<T> parse, Class<T> type, Map<String,String> properties) {
        this(groupId, parse, type, properties);
        this.consumer.subscribe(topic);
    }

    private Properties getProperties(String groupId, Class<T> type, Map<String,String> overrideProperties) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9094");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        // consumes 1 by 1
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.putAll(overrideProperties);
        return properties;
    }

    public void run() {
        while (true) {
            ConsumerRecords<String, T> records = this.consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.printf("Found %d records\n", records.count());
                for (var record : records) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        // so far, just logging the exception
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void close()  {
        this.consumer.close();
    }
}
