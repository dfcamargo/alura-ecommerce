package br.com.dfc.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String idOrder;
    private final BigDecimal amount;
    private final String email;

    public Order(String idOrder, BigDecimal amount, String email) {
        this.idOrder = idOrder;
        this.amount = amount;
        this.email = email;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Order{" +
                "idOrder='" + idOrder + '\'' +
                ", amount=" + amount + '\'' +
                ", email=" + email +
                '}';
    }
}
