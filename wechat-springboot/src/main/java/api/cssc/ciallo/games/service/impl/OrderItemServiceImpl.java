package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.OrderItem;
import api.cssc.ciallo.games.repository.OrderItemRepository;
import api.cssc.ciallo.games.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem getOrderItemById(Integer id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public OrderItem updateOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public void deleteOrderItem(Integer id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    public void createOrderItems(List<OrderItem> orderItems) {
        orderItemRepository.saveAll(orderItems);
    }
}
