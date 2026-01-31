package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem getOrderItemById(Integer id);
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem updateOrderItem(OrderItem orderItem);
    void deleteOrderItem(Integer id);
}