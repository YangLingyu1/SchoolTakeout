package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rider")
public class RiderAuthController {

    @Autowired
    private RiderService riderService;

    // 骑手登录
    @PostMapping("/login")
    public Response<?> riderLogin(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String name = request.get("name");
            String password = request.get("password");

            System.out.println("=== 骑手登录请求 ===");
            System.out.println("phone: " + phone);
            System.out.println("name: " + name);
            System.out.println("password: " + (password != null ? "已提供" : "未提供"));

            if (phone == null || phone.isEmpty()) {
                return Response.badRequest("手机号不能为空");
            }
            if (name == null || name.isEmpty()) {
                return Response.badRequest("姓名不能为空");
            }
            if (password == null || password.isEmpty()) {
                return Response.badRequest("密码不能为空");
            }

            // 检查骑手是否存在
            Optional<Rider> riderOpt = riderService.getRiderByPhoneAndName(phone, name);

            if (riderOpt.isEmpty()) {
                return Response.notFound("骑手信息不存在");
            }

            Rider rider = riderOpt.get();

            // 验证密码
            if (!rider.getPassword().equals(password)) {
                return Response.badRequest("密码错误");
            }

            // 生成骑手登录token
            String token = "rider_token_" + rider.getId();

            // 构建响应
            java.util.Map<String, Object> riderInfo = new java.util.HashMap<>();
            riderInfo.put("id", rider.getId());
            riderInfo.put("userId", rider.getUserId());
            riderInfo.put("realName", rider.getRealName());
            riderInfo.put("phone", rider.getPhone());
            riderInfo.put("status", rider.getStatus());
            riderInfo.put("level", rider.getLevel());

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", token);
            response.put("riderInfo", riderInfo);

            return Response.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.internalError("登录失败：" + e.getMessage());
        }
    }
}
