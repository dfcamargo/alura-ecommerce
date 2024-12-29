package br.com.dfc.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<Mail> mailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        mailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String userEmail = req.getParameter("email");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));

            String idOrder = UUID.randomUUID().toString();
            Order order = new Order(idOrder, amount, userEmail);

            String body = String.format("Thanks for you order! We are processing now! Your order: %s", idOrder);
            Mail email = new Mail("New Order", body);

            // save order
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, order);
            // sending mail..
            mailDispatcher.send("ECOMMERCE_SEND_MAIL", userEmail, email);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().printf("New order sent successfully! Order Id: [%s]", order.getIdOrder());
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
