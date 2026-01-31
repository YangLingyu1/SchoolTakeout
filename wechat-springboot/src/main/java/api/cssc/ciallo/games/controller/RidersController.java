package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Order;
import api.cssc.ciallo.games.entity.Earnings;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.service.OrderService;
import api.cssc.ciallo.games.service.EarningsService;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/riders")
public class RidersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EarningsService earningsService;

    @Autowired
    private RiderService riderService;

    // 获取骑手信息
    @GetMapping("/info")
    public Response<?> getRiderInfo(@RequestParam Integer userId) {
        Optional<Rider> riderOpt = riderService.getRiderByUserId(userId);
        if (riderOpt.isEmpty()) {
            return Response.notFound("骑手信息不存在");
        }
        Rider rider = riderOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", rider.getId());
        result.put("userId", rider.getUserId());
        result.put("realName", rider.getRealName());
        result.put("idCard", rider.getIdCard());
        result.put("phone", rider.getPhone());
        result.put("status", rider.getStatus());
        result.put("level", rider.getLevel());
        result.put("balance", rider.getBalance());
        result.put("totalOrders", rider.getTotalOrders());
        result.put("totalEarnings", rider.getTotalEarnings());
        result.put("paymentCode", rider.getPaymentCode());
        
        return Response.success(result);
    }

    // 获取骑手余额
    @GetMapping("/balance")
    public Response<?> getRiderBalance(@RequestParam Integer userId) {
        Optional<Rider> riderOpt = riderService.getRiderByUserId(userId);
        if (riderOpt.isEmpty()) {
            return Response.notFound("骑手信息不存在");
        }
        Rider rider = riderOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("balance", rider.getBalance());
        
        return Response.success(result);
    }

    // 更新骑手状态
    @PutMapping("/status")
    public Response<?> updateRiderStatus(@RequestParam Integer userId, @RequestBody Map<String, String> request) {
        Optional<Rider> riderOpt = riderService.getRiderByUserId(userId);
        if (riderOpt.isEmpty()) {
            return Response.notFound("骑手信息不存在");
        }
        
        Rider rider = riderOpt.get();
        String status = request.get("status");
        if (status == null || (!status.equals("active") && !status.equals("inactive"))) {
            return Response.badRequest("状态必须是active或inactive");
        }
        
        rider.setStatus(status);
        riderService.updateRider(rider);
        
        return Response.success("状态更新成功");
    }

    // 更新收款码
    @PutMapping("/payment-code")
    public Response<?> updatePaymentCode(@RequestParam Integer userId, @RequestBody Map<String, String> request) {
        String paymentCode = request.get("paymentCode");
        if (paymentCode == null || paymentCode.isEmpty()) {
            return Response.badRequest("收款码不能为空");
        }
        
        Rider rider = riderService.updatePaymentCode(userId, paymentCode);
        if (rider == null) {
            return Response.notFound("骑手信息不存在");
        }
        
        return Response.success("收款码更新成功");
    }

    // 获取骑手统计信息（含排名）
    @GetMapping("/statistics")
    public Response<?> getRiderStatistics(@RequestParam Integer userId) {
        Optional<Rider> riderOpt = riderService.getRiderByUserId(userId);
        if (riderOpt.isEmpty()) {
            return Response.notFound("骑手信息不存在");
        }
        Rider rider = riderOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", rider.getTotalOrders());
        result.put("totalEarnings", rider.getTotalEarnings());
        result.put("balance", rider.getBalance());
        result.put("level", rider.getLevel());
        
        result.put("rankByTotalOrders", riderService.getRiderRankByTotalOrders(userId));
        result.put("rankByTotalEarnings", riderService.getRiderRankByTotalEarnings(userId));
        result.put("rankByTodayOrders", riderService.getRiderRankByTodayOrders(userId));
        result.put("rankByTodayEarnings", riderService.getRiderRankByTodayEarnings(userId));
        
        return Response.success(result);
    }

    // 获取待接单列表
    @GetMapping("/pending-orders")
    public Response<?> getPendingOrders() {
        List<Order> orders = orderService.getOrdersByStatus(1); // 1: 待接单
        
        List<Map<String, Object>> result = orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("status", order.getStatus());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("deliveryFee", order.getDeliveryFee());
            orderMap.put("createdAt", order.getCreatedAt());
            
            if (order.getAddress() != null) {
                String address = order.getAddress().getBuilding() + "栋" + 
                               order.getAddress().getFloor() + "层 " + 
                               order.getAddress().getDetail();
                orderMap.put("address", address);
                orderMap.put("userName", order.getAddress().getName());
                orderMap.put("phone", order.getAddress().getPhone());
            }
            
            return orderMap;
        }).toList();
        
        return Response.success(result);
    }

    // 获取骑手配送中订单列表（含收益）
    @GetMapping("/delivery-orders")
    public Response<?> getDeliveryOrders(@RequestParam Integer riderId) {
        System.out.println("=== 获取配送中订单，riderId: " + riderId + " ===");
        List<Order> orders = orderService.getOrdersByRiderId(riderId);
        System.out.println("获取到 " + orders.size() + " 个订单");
        
        // 过滤出配送中的订单（status=2）
        List<Order> deliveringOrders = orders.stream()
            .filter(order -> order.getStatus() == 2)
            .toList();
        System.out.println("配送中订单数: " + deliveringOrders.size());
        
        List<Map<String, Object>> result = deliveringOrders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("status", order.getStatus());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("deliveryFee", order.getDeliveryFee());
            orderMap.put("createdAt", order.getCreatedAt());
            
            if (order.getAddress() != null) {
                orderMap.put("address", order.getAddress().getDetail());
                orderMap.put("floor", order.getAddress().getFloor());
                orderMap.put("phone", order.getAddress().getPhone());
                orderMap.put("name", order.getAddress().getName());
            }
            
            if (order.getUser() != null) {
                orderMap.put("userName", order.getUser().getNickname());
            }
            
            System.out.println("订单 " + order.getId() + " - 状态: " + order.getStatus());
            if (order.getStatus() == 2) {
                System.out.println("订单 " + order.getId() + " 进入配送中逻辑");
                try {
                    int itemCount = orderService.getOrderItemCount(order.getId());
                    int floor = order.getAddress() != null ? order.getAddress().getFloor() : 0;
                    BigDecimal earnings = new BigDecimal("0.1").multiply(new BigDecimal(floor + itemCount));
                    System.out.println("订单 " + order.getId() + " - 商品数: " + itemCount + ", 楼层: " + floor + ", 收益: " + earnings);
                    orderMap.put("earnings", earnings);
                    orderMap.put("itemCount", itemCount);
                    orderMap.put("floor", floor);
                } catch (Exception e) {
                    System.out.println("订单 " + order.getId() + " 计算收益时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return orderMap;
        }).toList();
        
        System.out.println("=== 返回 " + result.size() + " 个订单 ===");
        return Response.success(result);
    }

    // 获取骑手订单列表
    @GetMapping("/orders")
    public Response<?> getRiderOrders(@RequestParam Integer riderId) {
        List<Order> orders = orderService.getOrdersByRiderId(riderId);
        return Response.success(orders);
    }

    // 获取骑手收益列表
    @GetMapping("/earnings")
    public Response<?> getRiderEarnings(@RequestParam Integer riderId) {
        List<Earnings> earnings = earningsService.getEarningsByRiderId(riderId);
        return Response.success(earnings);
    }

    // 骑手接单
    @PutMapping("/accept-order")
    public Response<?> acceptOrder(@RequestBody java.util.Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        Integer riderId = request.get("riderId");
        if (orderId == null || riderId == null) {
            return Response.badRequest("参数错误");
        }
        
        Optional<Rider> riderOpt = riderService.getRiderByUserId(riderId);
        if (riderOpt.isEmpty()) {
            return Response.notFound("骑手信息不存在");
        }
        Rider rider = riderOpt.get();
        
        if (!"active".equals(rider.getStatus())) {
            return Response.badRequest("骑手状态不是接单中");
        }
        
        int maxOrders = getMaxOrdersByLevel(rider.getLevel());
        int currentOrders = orderService.getDeliveringOrdersCount(riderId);
        
        if (currentOrders >= maxOrders) {
            return Response.badRequest("已达到最大配送单数限制");
        }
        
        orderService.assignRider(orderId, riderId);
        return Response.success("接单成功");
    }

    // 骑手完成订单
    @PutMapping("/complete-order")
    public Response<?> completeOrder(@RequestBody java.util.Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        Integer riderId = request.get("riderId");
        if (orderId == null || riderId == null) {
            return Response.badRequest("参数错误");
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return Response.notFound("订单不存在");
        }
        
        if (!order.getRiderId().equals(riderId)) {
            return Response.badRequest("不是该骑手的订单");
        }
        
        orderService.updateOrderStatus(orderId, 3); // 3: 已完成
        
        riderService.updateRiderLevel(riderId);
        
        return Response.success("订单完成");
    }

    private int getMaxOrdersByLevel(String level) {
        switch (level) {
            case "gold":
                return 5;
            case "silver":
                return 3;
            default:
                return 1;
        }
    }
}
