package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Admin;
import api.cssc.ciallo.games.service.AdminService;
import api.cssc.ciallo.games.util.Response;
import api.cssc.ciallo.games.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // 管理员登录
    @PostMapping("/login")
    public Response<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return Response.badRequest("用户名和密码不能为空");
        }

        Admin admin = adminService.authenticate(username, password);
        if (admin == null) {
            return Response.unauthorized("用户名或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(admin.getUsername());

        // 构建响应数据
        Map<String, Object> responseData = Map.of(
                "token", token,
                "admin", Map.of(
                        "id", admin.getId(),
                        "username", admin.getUsername(),
                        "name", admin.getName(),
                        "role", admin.getRole()
                )
        );

        return Response.success(responseData);
    }

    // 获取管理员信息
    @GetMapping("/info")
    public Response<?> getAdminInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            System.out.println("收到获取管理员信息请求，token: " + token);
            
            if (token == null || token.isEmpty()) {
                System.out.println("token为空");
                return Response.unauthorized("未授权");
            }

            // 从token中提取用户名
            String cleanToken = token.replace("Bearer ", "");
            System.out.println("清理后的token: " + cleanToken);
            
            String username = jwtUtil.extractSubject(cleanToken);
            System.out.println("从token中提取的用户名: " + username);
            
            Admin admin = adminService.getAdminByUsername(username).orElse(null);

            if (admin == null) {
                System.out.println("管理员不存在: " + username);
                return Response.notFound("管理员不存在");
            }

            Map<String, Object> adminInfo = Map.of(
                    "id", admin.getId(),
                    "username", admin.getUsername(),
                    "name", admin.getName(),
                    "role", admin.getRole()
            );

            System.out.println("返回管理员信息: " + adminInfo);
            return Response.success(adminInfo);
        } catch (Exception e) {
            System.out.println("获取管理员信息失败: " + e.getMessage());
            e.printStackTrace();
            return Response.internalError("获取管理员信息失败: " + e.getMessage());
        }
    }

    // 管理员登出
    @PostMapping("/logout")
    public Response<?> logout() {
        // 由于使用JWT，登出操作主要在前端清除token
        return Response.success();
    }
}