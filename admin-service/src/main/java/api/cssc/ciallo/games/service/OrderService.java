package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order getOrderById(Integer id);
    List<Order> getOrders();
    List<Order> getOrdersByUserId(Integer userId);
    List<Order> getOrdersByRiderId(Integer riderId);
    List<Order> getPendingOrders();
    Order createOrder(Order order);
    Order updateOrder(Order order);
    void updateOrderStatus(Integer orderId, Integer status);
    void assignRider(Integer orderId, Integer riderId);
    void deleteOrder(Integer id);
    Map<String, Object> getOrderStatistics();
    Map<String, Object> getOrderStatisticsByDateRange(String startDate, String endDate);
}