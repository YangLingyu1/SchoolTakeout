package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.RiderApplication;
import api.cssc.ciallo.games.entity.User;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.service.RiderApplicationService;
import api.cssc.ciallo.games.service.UserService;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/riders")
public class AdminRidersController {

    @Autowired
    private RiderApplicationService riderApplicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RiderService riderService;

    // 获取骑手申请表单列表
    @GetMapping("/applications")
    public Response<?> getRiderApplications(@RequestParam(required = false) String status) {
        List<RiderApplication> applications;
        if (status != null) {
            applications = riderApplicationService.getApplicationsByStatus(status);
        } else {
            applications = riderApplicationService.getApplications();
        }

        // 转换为前端期望的格式
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (RiderApplication app : applications) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", app.getId());
            map.put("userId", app.getUserId());
            map.put("userName", app.getRealName());
            map.put("userPhone", app.getPhone());
            map.put("idCard", app.getIdCard());
            map.put("vehicleType", ""); // 暂时为空
            map.put("vehicleNumber", ""); // 暂时为空
            map.put("status", app.getStatus());
            map.put("createdAt", app.getCreatedAt());
            map.put("updatedAt", app.getCreatedAt()); // 暂时使用创建时间
            result.add(map);
        }

        return Response.success(result);
    }

    // 获取单个骑手申请表单详情
    @GetMapping("/applications/{id}")
    public Response<?> getRiderApplicationById(@PathVariable Integer id) {
        RiderApplication application = riderApplicationService.getApplicationById(id);
        if (application == null) {
            return Response.notFound("申请表单不存在");
        }
        
        // 获取当前时间作为默认值
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime createdAt = application.getCreatedAt() != null ? application.getCreatedAt() : now;
        
        // 转换为前端期望的格式
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", application.getId());
        map.put("userId", application.getUserId());
        map.put("userName", application.getRealName());
        map.put("userPhone", application.getPhone());
        map.put("idCard", application.getIdCard());
        map.put("vehicleType", ""); // 暂时为空
        map.put("vehicleNumber", ""); // 暂时为空
        map.put("status", application.getStatus());
        map.put("createdAt", createdAt);
        map.put("updatedAt", createdAt); // 暂时使用创建时间
        
        return Response.success(map);
    }

    // 审核骑手申请
    @PutMapping("/applications/{id}/review")
    public Response<?> reviewRiderApplication(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            return Response.badRequest("状态必须是approved或rejected");
        }

        RiderApplication application = riderApplicationService.getApplicationById(id);
        if (application == null) {
            return Response.notFound("申请表单不存在");
        }

        // 更新申请表单状态
        application.setStatus(status);
        riderApplicationService.updateApplication(application);

        // 如果审核通过，更新用户状态为骑手并创建骑手记录
        if (status.equals("approved")) {
            User user = userService.getUserById(application.getUserId());
            if (user != null) {
                user.setIsRider(true);
                user.setRiderStatus("approved");
                userService.updateUser(user);
                
                // 创建骑手记录
                Rider rider = new Rider();
                rider.setUserId(application.getUserId());
                rider.setRealName(application.getRealName());
                rider.setIdCard(application.getIdCard());
                rider.setPhone(application.getPhone());
                rider.setPassword(application.getPassword());
                rider.setStatus("active");
                rider.setLevel("normal");
                rider.setBalance(java.math.BigDecimal.ZERO);
                rider.setTotalOrders(0);
                rider.setTotalEarnings(java.math.BigDecimal.ZERO);
                riderService.createRider(rider);
            }
        } else {
            // 如果审核拒绝，更新用户状态为拒绝
            User user = userService.getUserById(application.getUserId());
            if (user != null) {
                user.setRiderStatus("rejected");
                userService.updateUser(user);
            }
        }

        return Response.success();
    }

    // 获取骑手列表
    @GetMapping
    public Response<?> getRiders() {
        List<User> riders = userService.getRiders();
        
        // 获取当前时间作为默认值
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        // 转换为前端期望的格式
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (User rider : riders) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", rider.getId());
            map.put("username", rider.getNickname()); // 使用微信昵称作为username
            
            // 从骑手申请表中获取真实姓名和手机号
            java.util.Optional<RiderApplication> latestApplication = riderApplicationService.getLatestApplicationByUserId(rider.getId());
            if (latestApplication.isPresent()) {
                RiderApplication application = latestApplication.get();
                map.put("name", application.getRealName()); // 使用真实姓名作为name
                map.put("phone", application.getPhone() != null ? application.getPhone() : ""); // 使用申请表中的手机号
            } else {
                map.put("name", "");
                map.put("phone", "");
            }
            
            map.put("isRider", rider.getIsRider());
            map.put("riderStatus", rider.getRiderStatus());
            map.put("createdAt", rider.getCreatedAt() != null ? rider.getCreatedAt() : now);
            map.put("updatedAt", rider.getUpdatedAt() != null ? rider.getUpdatedAt() : now);
            result.add(map);
        }
        
        return Response.success(result);
    }

    // 获取骑手详情
    @GetMapping("/{id}")
    public Response<?> getRiderById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null || !user.getIsRider()) {
            return Response.notFound("骑手不存在");
        }
        
        // 获取当前时间作为默认值
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        // 转换为前端期望的格式
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getNickname()); // 使用微信昵称作为username
        
        // 从骑手申请表中获取真实姓名和手机号
        java.util.Optional<RiderApplication> latestApplication = riderApplicationService.getLatestApplicationByUserId(user.getId());
        if (latestApplication.isPresent()) {
            RiderApplication application = latestApplication.get();
            map.put("name", application.getRealName()); // 使用真实姓名作为name
            map.put("phone", application.getPhone() != null ? application.getPhone() : ""); // 使用申请表中的手机号
        } else {
            map.put("name", "");
            map.put("phone", "");
        }
        
        map.put("isRider", user.getIsRider());
        map.put("riderStatus", user.getRiderStatus());
        map.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : now);
        map.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt() : now);
        
        return Response.success(map);
    }
}