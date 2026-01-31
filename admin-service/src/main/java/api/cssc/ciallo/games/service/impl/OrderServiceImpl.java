package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Order;
import api.cssc.ciallo.games.repository.OrderRepository;
import api.cssc.ciallo.games.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByRiderId(Integer riderId) {
        return orderRepository.findByRiderId(riderId);
    }

    @Override
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(0);
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }

    @Override
    public void assignRider(Integer orderId, Integer riderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setRiderId(riderId);
            order.setStatus(2); // 状态改为配送中
            orderRepository.save(order);
        }
    }

    @Override
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        List<Order> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> o.getStatus() == 1).count();
        long deliveringOrders = allOrders.stream().filter(o -> o.getStatus() == 2).count();
        long completedOrders = allOrders.stream().filter(o -> o.getStatus() == 3).count();
        long cancelledOrders = allOrders.stream().filter(o -> o.getStatus() == 4).count();
        
        BigDecimal totalAmount = allOrders.stream()
            .filter(o -> o.getStatus() == 3)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("pendingOrders", pendingOrders);
        result.put("deliveringOrders", deliveringOrders);
        result.put("completedOrders", completedOrders);
        result.put("cancelledOrders", cancelledOrders);
        result.put("totalAmount", totalAmount);
        
        return result;
    }

    @Override
    public Map<String, Object> getOrderStatisticsByDateRange(String startDate, String endDate) {
        List<Order> allOrders = orderRepository.findAll();
        
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        
        List<Order> filteredOrders = allOrders.stream()
            .filter(o -> o.getCreatedAt() != null)
            .filter(o -> !o.getCreatedAt().isBefore(startDateTime))
            .filter(o -> !o.getCreatedAt().isAfter(endDateTime))
            .toList();
        
        long totalOrders = filteredOrders.size();
        long completedOrders = filteredOrders.stream().filter(o -> o.getStatus() == 3).count();
        
        BigDecimal totalAmount = filteredOrders.stream()
            .filter(o -> o.getStatus() == 3)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("completedOrders", completedOrders);
        result.put("totalAmount", totalAmount);
        
        return result;
    }
}