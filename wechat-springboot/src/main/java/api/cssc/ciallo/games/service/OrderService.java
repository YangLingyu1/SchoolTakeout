package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(Integer id);
    Order getOrderByOrderNo(String orderNo);
    List<Order> getOrdersByUserId(Integer userId);
    List<Order> getOrdersByRiderId(Integer riderId);
    List<Order> getOrdersByStatus(Integer status);
    List<Order> getAllOrders();
    Order updateOrder(Order order);
    void deleteOrder(Integer id);
    void updateOrderStatus(Integer orderId, Integer status);
    void assignRider(Integer orderId, Integer riderId);
    int getOrderItemCount(Integer orderId);
    int getDeliveringOrdersCount(Integer riderId);
}
