package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Order;
import api.cssc.ciallo.games.entity.OrderItem;
import api.cssc.ciallo.games.entity.Product;
import api.cssc.ciallo.games.entity.Address;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.service.OrderService;
import api.cssc.ciallo.games.service.OrderItemService;
import api.cssc.ciallo.games.service.AddressService;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private AddressService addressService;

    // 获取用户订单列表
    @GetMapping
    public Response<?> getOrders(@RequestParam(required = false) Integer userId, @RequestParam(required = false) String status) {
        // 使用前端传递的用户ID，如果没有提供则返回错误
        if (userId == null) {
            return Response.badRequest("用户ID不能为空");
        }
        
        System.out.println("查询订单，用户ID: " + userId);
        
        // 获取该用户的所有订单
        List<Order> orders = orderService.getOrdersByUserId(userId);
        System.out.println("查询到的订单数量: " + orders.size());
        
        if (status != null && !status.isEmpty()) {
            // 这里可以根据实际状态值进行过滤
            // orders = orders.stream().filter(order -> order.getStatus().equals(status)).toList();
        }
        
        // 为每个订单添加items字段，确保前端能够正确渲染
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (Order order : orders) {
            System.out.println("订单信息: " + order.getId() + " - " + order.getOrderNo() + " - " + order.getUserId());
            java.util.Map<String, Object> orderMap = new java.util.HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("userId", order.getUserId());
            orderMap.put("riderId", order.getRiderId());
            orderMap.put("addressId", order.getAddressId());
            orderMap.put("status", order.getStatus());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("deliveryFee", order.getDeliveryFee());
            orderMap.put("createdAt", order.getCreatedAt());
            orderMap.put("updatedAt", order.getUpdatedAt());
            // 获取订单的订单项
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
            List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
            for (OrderItem item : orderItems) {
                java.util.Map<String, Object> itemMap = new java.util.HashMap<>();
                itemMap.put("productId", item.getProductId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                
                // 获取真实的商品信息
                Product product = item.getProduct();
                if (product != null) {
                    itemMap.put("name", product.getName());
                    itemMap.put("image", product.getImageUrl() != null ? product.getImageUrl() : "https://via.placeholder.com/80x80?text=商品");
                } else {
                    // 如果商品信息不存在，使用默认值
                    itemMap.put("name", "商品" + item.getProductId());
                    itemMap.put("image", "https://via.placeholder.com/80x80?text=商品");
                }
                
                items.add(itemMap);
            }
            orderMap.put("items", items); // 添加真实的订单项，确保前端能够正确显示商品数量和名称
            
            // 获取并添加地址信息
            Address address = addressService.getAddressById(order.getAddressId());
            if (address != null) {
                orderMap.put("addressName", address.getName());
                orderMap.put("addressPhone", address.getPhone());
                orderMap.put("addressDetail", address.getDetail());
                orderMap.put("addressBuilding", address.getBuilding());
                orderMap.put("addressFloor", address.getFloor());
            }
            
            result.add(orderMap);
        }
        
        // 返回实际的订单数据
        return Response.success(result);
    }

    // 获取单个订单详情
    @GetMapping("/{id}")
    public Response<?> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Response.notFound("订单不存在");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(id);
        
        // 获取地址信息
        Address address = addressService.getAddressById(order.getAddressId());
        
        // 获取骑手信息
        Map<String, Object> riderInfo = null;
        if (order.getRiderId() != null) {
            System.out.println("订单 " + id + " 的 rider_id: " + order.getRiderId());
            Optional<Rider> riderOpt = riderService.getRiderByUserId(order.getRiderId());
            System.out.println("查询骑手结果: " + riderOpt.isPresent());
            if (riderOpt.isPresent()) {
                Rider rider = riderOpt.get();
                System.out.println("骑手信息: id=" + rider.getId() + ", userId=" + rider.getUserId() + ", realName=" + rider.getRealName() + ", phone=" + rider.getPhone());
                riderInfo = new java.util.HashMap<>();
                riderInfo.put("id", rider.getId());
                riderInfo.put("userId", rider.getUserId());
                riderInfo.put("realName", rider.getRealName());
                riderInfo.put("phone", rider.getPhone());
                riderInfo.put("status", rider.getStatus());
                riderInfo.put("level", rider.getLevel());
            } else {
                System.out.println("未找到骑手记录，user_id=" + order.getRiderId());
            }
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("order", order);
        result.put("items", orderItems);
        result.put("address", address);
        if (riderInfo != null) {
            result.put("rider", riderInfo);
        }
        
        return Response.success(result);
    }

    // 创建订单
    @PostMapping
    public Response<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            // 这里简化处理，实际需要更复杂的订单创建逻辑
            Order order = new Order();
            
            // 处理userId - 使用前端传递的真实用户ID
            Integer userId = (Integer) request.get("userId");
            System.out.println("前端提交的userId: " + userId);
            
            if (userId == null) {
                return Response.badRequest("用户ID不能为空");
            }
            
            order.setUserId(userId);
            System.out.println("最终使用的userId: " + userId);
            
            // 处理addressId - 使用前端传递的真实地址ID
            Integer addressId = (Integer) request.get("addressId");
            System.out.println("前端提交的addressId: " + addressId);
            
            if (addressId == null) {
                return Response.badRequest("地址ID不能为空");
            }
            
            // 验证地址是否存在且属于当前用户
            Address address = addressService.getAddressById(addressId);
            if (address == null) {
                return Response.badRequest("地址不存在");
            }
            if (!address.getUserId().equals(userId)) {
                return Response.badRequest("地址不属于当前用户");
            }
            
            order.setAddressId(addressId);
            System.out.println("最终使用的addressId: " + addressId);
            
            // 处理BigDecimal类型转换
            Object totalAmountObj = request.get("totalAmount");
            if (totalAmountObj != null) {
                if (totalAmountObj instanceof Double) {
                    order.setTotalAmount(java.math.BigDecimal.valueOf((Double) totalAmountObj));
                } else if (totalAmountObj instanceof Integer) {
                    order.setTotalAmount(java.math.BigDecimal.valueOf((Integer) totalAmountObj));
                } else if (totalAmountObj instanceof String) {
                    order.setTotalAmount(new java.math.BigDecimal((String) totalAmountObj));
                }
            }
            
            Object deliveryFeeObj = request.get("deliveryFee");
            if (deliveryFeeObj != null) {
                if (deliveryFeeObj instanceof Double) {
                    order.setDeliveryFee(java.math.BigDecimal.valueOf((Double) deliveryFeeObj));
                } else if (deliveryFeeObj instanceof Integer) {
                    order.setDeliveryFee(java.math.BigDecimal.valueOf((Integer) deliveryFeeObj));
                } else if (deliveryFeeObj instanceof String) {
                    order.setDeliveryFee(new java.math.BigDecimal((String) deliveryFeeObj));
                }
            }
            
            order.setOrderNo("ORDER_" + System.currentTimeMillis());
            order.setStatus(0); // 0: pending
            order.setCreatedAt(java.time.LocalDateTime.now());
            order.setUpdatedAt(java.time.LocalDateTime.now());

            Order createdOrder = orderService.createOrder(order);

            // 创建订单项
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(createdOrder.getId());
                    
                    // 处理productId - 从id字段获取
                    Object productIdObj = item.get("productId");
                    if (productIdObj == null) {
                        productIdObj = item.get("id"); // 兼容前端使用id字段
                    }
                    if (productIdObj != null) {
                        orderItem.setProductId((Integer) productIdObj);
                    }
                    
                    orderItem.setQuantity((Integer) item.get("quantity"));
                    
                    // 处理price类型转换
                    Object priceObj = item.get("price");
                    if (priceObj != null) {
                        if (priceObj instanceof Double) {
                            orderItem.setPrice(java.math.BigDecimal.valueOf((Double) priceObj));
                        } else if (priceObj instanceof Integer) {
                            orderItem.setPrice(java.math.BigDecimal.valueOf((Integer) priceObj));
                        } else if (priceObj instanceof String) {
                            orderItem.setPrice(new java.math.BigDecimal((String) priceObj));
                        }
                    }
                    
                    orderItemService.createOrderItem(orderItem);
                }
            }

            return Response.success(createdOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.internalError("订单创建失败: " + e.getMessage());
        }
    }

    // 更新订单状态
    @PutMapping("/status")
    public Response<?> updateOrderStatus(@RequestBody Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        Integer status = request.get("status");
        if (orderId == null || status == null) {
            return Response.badRequest("参数错误");
        }
        orderService.updateOrderStatus(orderId, status);
        return Response.success(null);
    }

    // 骑手接单
    @PutMapping("/assign-rider")
    public Response<?> assignRider(@RequestBody Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        Integer riderId = request.get("riderId");
        if (orderId == null || riderId == null) {
            return Response.badRequest("参数错误");
        }
        orderService.assignRider(orderId, riderId);
        return Response.success(null);
    }

    // 支付订单
    @PutMapping("/{id}/pay")
    public Response<?> payOrder(@PathVariable Integer id) {
        if (id == null) {
            return Response.badRequest("参数错误");
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Response.notFound("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            return Response.badRequest("订单状态不正确，无法支付");
        }
        
        orderService.updateOrderStatus(id, 1); // 1: 待接单
        return Response.success("支付成功");
    }

    // 取消订单
    @PutMapping("/{id}/cancel")
    public Response<?> cancelOrder(@PathVariable Integer id) {
        if (id == null) {
            return Response.badRequest("参数错误");
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Response.notFound("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            return Response.badRequest("只有待支付的订单才能取消");
        }
        
        orderService.updateOrderStatus(id, 4); // 4: 已取消
        return Response.success("订单已取消");
    }

    // 删除订单
    @DeleteMapping("/{id}")
    public Response<?> deleteOrder(@PathVariable Integer id) {
        if (id == null) {
            return Response.badRequest("参数错误");
        }
        orderService.deleteOrder(id);
        return Response.success(null);
    }
}
