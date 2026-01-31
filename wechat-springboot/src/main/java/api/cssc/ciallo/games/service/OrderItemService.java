package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem getOrderItemById(Integer id);
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
    OrderItem updateOrderItem(OrderItem orderItem);
    void deleteOrderItem(Integer id);
    void createOrderItems(List<OrderItem> orderItems);
}
