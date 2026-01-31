package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Order;
import api.cssc.ciallo.games.entity.OrderItem;
import api.cssc.ciallo.games.service.OrderService;
import api.cssc.ciallo.games.service.OrderItemService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public Response<?> getOrders(@RequestParam(required = false) Integer status) {
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrders();
        } else {
            orders = orderService.getOrders();
        }
        return Response.success(orders);
    }

    @GetMapping("/{id}")
    public Response<?> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Response.notFound("订单不存在");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(id);
        return Response.success(Map.of(
                "order", order,
                "items", orderItems
        ));
    }

    @PutMapping("/{id}/status")
    public Response<?> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null) {
            return Response.badRequest("状态不能为空");
        }
        orderService.updateOrderStatus(id, status);
        return Response.success();
    }

    @PutMapping("/{id}/assign-rider")
    public Response<?> assignRider(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        Integer riderId = request.get("riderId");
        if (riderId == null) {
            return Response.badRequest("骑手ID不能为空");
        }
        orderService.assignRider(id, riderId);
        return Response.success();
    }

    @GetMapping("/statistics")
    public Response<?> getOrderStatistics() {
        Map<String, Object> statistics = orderService.getOrderStatistics();
        return Response.success(statistics);
    }

    @GetMapping("/statistics/date-range")
    public Response<?> getOrderStatisticsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> statistics = orderService.getOrderStatisticsByDateRange(startDate, endDate);
        return Response.success(statistics);
    }
}