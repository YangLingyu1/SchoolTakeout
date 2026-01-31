package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Order;
import api.cssc.ciallo.games.entity.OrderItem;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.repository.OrderRepository;
import api.cssc.ciallo.games.repository.OrderItemRepository;
import api.cssc.ciallo.games.service.OrderService;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.service.EarningsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RiderService riderService;

    @Autowired
    private EarningsService earningsService;

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo).orElse(null);
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
    public List<Order> getOrdersByStatus(Integer status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllActive();
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setDeleted(true);
            orderRepository.save(order);
        }
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
            
            if (status == 3 && order.getRiderId() != null) {
                int itemCount = getOrderItemCount(orderId);
                int floor = order.getAddress() != null ? order.getAddress().getFloor() : 0;
                earningsService.addEarningsForOrder(order.getRiderId(), orderId, itemCount, floor);
            }
        }
    }

    @Override
    public void assignRider(Integer orderId, Integer riderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setRiderId(riderId);
            order.setStatus(2); // 2: 待配送
            orderRepository.save(order);
            
            // 更新骑手订单数
            Optional<Rider> riderOpt = riderService.getRiderByUserId(riderId);
            if (riderOpt.isPresent()) {
                Rider rider = riderOpt.get();
                rider.setTotalOrders(rider.getTotalOrders() + 1);
                riderService.updateRider(rider);
            }
        }
    }

    @Override
    public int getOrderItemCount(Integer orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.size();
    }

    @Override
    public int getDeliveringOrdersCount(Integer riderId) {
        List<Order> orders = orderRepository.findByRiderId(riderId);
        return (int) orders.stream().filter(order -> order.getStatus() == 2).count();
    }
}
