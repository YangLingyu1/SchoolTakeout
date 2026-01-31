package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.service.UserService;
import api.cssc.ciallo.games.service.ProductService;
import api.cssc.ciallo.games.service.OrderService;
import api.cssc.ciallo.games.service.RiderApplicationService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RiderApplicationService riderApplicationService;

    // 获取仪表盘数据
    @GetMapping
    public Response<?> getDashboardData() {
        // 统计用户数量
        long userCount = userService.getUsers().size();
        
        // 统计商品数量
        long productCount = productService.getProducts().size();
        
        // 统计订单数量
        long orderCount = orderService.getOrders().size();
        
        // 统计骑手数量
        long riderCount = userService.getRiders().size();
        
        // 统计待审核的骑手申请
        long pendingRiderApplications = riderApplicationService.getPendingApplications().size();
        
        // 统计待处理的订单
        long pendingOrders = orderService.getPendingOrders().size();
        
        // 构建响应数据
        Map<String, Object> dashboardData = Map.of(
                "userCount", userCount,
                "productCount", productCount,
                "orderCount", orderCount,
                "riderCount", riderCount,
                "pendingRiderApplications", pendingRiderApplications,
                "pendingOrders", pendingOrders
        );
        
        return Response.success(dashboardData);
    }
}