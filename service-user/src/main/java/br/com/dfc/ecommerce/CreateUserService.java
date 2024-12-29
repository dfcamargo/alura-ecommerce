package br.com.dfc.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {

    private final Connection conn;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.conn = DriverManager.getConnection(url);
        this.conn.createStatement().execute("CREATE TABLE IF NOT EXISTS users (" +
            " id VARCHAR(50) PRIMARY KEY, " +
            " email VARCHAR(200) " +
            ")"
        );
    }

    public static void main(String[] args) throws SQLException {
        CreateUserService createUserService = new CreateUserService();
        try(KafkaService<Order> service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws SQLException {
        System.out.println("--------------------------------");
        System.out.println("Processing new order, checking for user...");
        System.out.println(record.key());
        System.out.println(record.value());

        Order order = record.value();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }

    }

    private void insertNewUser(String email) throws SQLException {
        PreparedStatement ps = this.conn.prepareStatement("INSERT INTO users VALUES (?,?)");
        ps.setString(1, UUID.randomUUID().toString());
        ps.setString(2, email);
        ps.execute();
        System.out.println("New user created!");
    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement ps = this.conn.prepareStatement("SELECT id FROM users WHERE email = ? LIMIT 1");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        return !rs.next();
    }
}
