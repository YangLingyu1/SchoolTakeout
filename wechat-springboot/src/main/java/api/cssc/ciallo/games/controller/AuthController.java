package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.RiderApplication;
import api.cssc.ciallo.games.entity.Rider;
import api.cssc.ciallo.games.entity.User;
import api.cssc.ciallo.games.service.RiderApplicationService;
import api.cssc.ciallo.games.service.RiderService;
import api.cssc.ciallo.games.service.UserService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RiderApplicationService riderApplicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RiderService riderService;

    // 微信登录
    @PostMapping("/login")
    public Response<?> wechatLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            return Response.badRequest("微信code不能为空");
        }

        try {
            // 调用微信API获取openid
            String appId = "wx57604323ac0e26a8";
            String appSecret = "b6858f072486b74ec5094b17117af165";
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
            
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JSONObject result = JSONObject.parseObject(response.getBody());
            String openid = result.getString("openid");
            
            if (openid == null) {
                return Response.badRequest("获取openid失败：" + result.getString("errmsg"));
            }
            
            // 查找或创建用户
            User user = userService.getUserByOpenid(openid).orElse(null);
            if (user == null) {
                // 创建新用户
                user = new User();
                user.setOpenid(openid);
                user.setNickname("微信用户");
                user.setIsRider(false);
                user.setRiderStatus("not_applied");
                user = userService.createUser(user);
            }

            // 生成JWT token
            String token = "jwt_token_" + user.getId();

            // 构建用户信息
            java.util.Map<String, Object> userInfoMap = new java.util.HashMap<>();
            userInfoMap.put("id", user.getId());
            userInfoMap.put("openid", user.getOpenid());
            userInfoMap.put("nickname", user.getNickname());
            userInfoMap.put("avatar", user.getAvatar());
            userInfoMap.put("is_rider", user.getIsRider());
            userInfoMap.put("rider_status", user.getRiderStatus());

            java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("token", token);
            responseMap.put("userInfo", userInfoMap);
            responseMap.put("isRider", user.getIsRider());
            responseMap.put("riderStatus", user.getRiderStatus());

            return Response.success(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.internalError("登录失败：" + e.getMessage());
        }
    }

    // 获取用户信息
    @GetMapping("/user-info")
    public Response<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return Response.unauthorized("未授权");
        }

        try {
            // 提取token部分（去掉 "Bearer " 前缀）
            String token = null;
            if (authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7).trim(); // 去掉 "Bearer " (7个字符)
            } else {
                // 如果没有 "Bearer " 前缀，直接使用原值（兼容旧逻辑）
                token = authorizationHeader;
            }

            if (token == null || token.isEmpty()) {
                return Response.unauthorized("无效的token");
            }

            // 从token中解析用户ID
            // 实际项目中应该使用JWT解析，这里简化处理
            Integer userId = null;
            if (token.startsWith("jwt_token_")) {
                String userIdStr = token.substring("jwt_token_".length());
                try {
                    userId = Integer.parseInt(userIdStr);
                } catch (NumberFormatException e) {
                    return Response.unauthorized("无效的token");
                }
            }

            if (userId == null) {
                return Response.unauthorized("无效的token");
            }

            // 获取用户信息
            User user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                return Response.notFound("用户不存在");
            }

            // 构建用户信息
            java.util.Map<String, Object> userInfoMap = new java.util.HashMap<>();
            userInfoMap.put("id", user.getId());
            userInfoMap.put("openid", user.getOpenid());
            userInfoMap.put("nickname", user.getNickname());
            userInfoMap.put("avatar", user.getAvatar());
            userInfoMap.put("is_rider", user.getIsRider());
            userInfoMap.put("rider_status", user.getRiderStatus());

            java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("userInfo", userInfoMap);
            responseMap.put("isRider", user.getIsRider());
            responseMap.put("riderStatus", user.getRiderStatus());

            return Response.success(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.internalError("获取用户信息失败：" + e.getMessage());
        }
    }

    // 骑手登录
    @PostMapping("/rider/login")
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

    // 骑手申请
    @PostMapping("/rider-apply")
    public Response<?> riderApply(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> request) {
        try {
            // 从Authorization头部解析用户ID，避免前端传递userId参数
            Integer userId = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer jwt_token_")) {
                String tokenPart = authorizationHeader.substring(7); // 去掉 "Bearer " 前缀
                if (tokenPart.startsWith("jwt_token_")) {
                    String userIdStr = tokenPart.substring("jwt_token_".length());
                    try {
                        userId = Integer.parseInt(userIdStr);
                    } catch (NumberFormatException e) {
                        System.out.println("从token解析用户ID失败: " + tokenPart);
                        return Response.badRequest("无效的认证信息");
                    }
                }
            }
            
            if (userId == null) {
                System.out.println("无法从认证信息中解析用户ID: " + authorizationHeader);
                return Response.unauthorized("未授权访问");
            }
            
            // 打印完整的请求数据用于调试
            System.out.println("=== 骑手申请请求数据 ===");
            System.out.println("完整请求: " + request);
            System.out.println("所有字段: " + request.keySet());
            System.out.println("从认证头解析出的userId: " + userId);
            
            // 从请求中获取其他信息（不再从请求中获取userId）
            String name = (String) request.get("name");
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            String idCard = (String) request.get("idCard");
            String idCardFront = (String) request.get("idCardFront");
            String idCardBack = (String) request.get("idCardBack");
            
            // 验证必填字段
            System.out.println("开始验证必填字段...");
            System.out.println("userId: " + userId + " (从认证头获取)");
            System.out.println("name: '" + name + "'");
            System.out.println("phone: '" + phone + "'");
            System.out.println("password: '" + (password != null ? "已提供" : "未提供") + "'");
            System.out.println("idCard: '" + idCard + "'");
            System.out.println("idCardFront: " + (idCardFront != null ? "已设置" : "null"));
            System.out.println("idCardBack: " + (idCardBack != null ? "已设置" : "null"));

            if (name == null) {
                System.out.println("错误：name 为 null");
                return Response.badRequest("缺少姓名字段");
            }
            if (phone == null) {
                System.out.println("错误：phone 为 null");
                return Response.badRequest("缺少手机号字段");
            }
            if (password == null) {
                System.out.println("错误：password 为 null");
                return Response.badRequest("缺少密码字段");
            }
            if (idCard == null) {
                System.out.println("错误：idCard 为 null");
                return Response.badRequest("缺少身份证号字段");
            }
            if (idCardFront == null) {
                System.out.println("错误：idCardFront 为 null");
                return Response.badRequest("缺少身份证正面字段");
            }
            if (idCardBack == null) {
                System.out.println("错误：idCardBack 为 null");
                return Response.badRequest("缺少身份证反面字段");
            }

            // 检查用户是否存在
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("用户不存在，ID: " + userId);
                return Response.badRequest("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 检查骑手表中是否已存在相同信息的骑手
            Optional<api.cssc.ciallo.games.entity.Rider> existingRider = riderService.getRiderByPhoneAndName(phone, name);
            if (existingRider.isPresent()) {
                api.cssc.ciallo.games.entity.Rider rider = existingRider.get();
                System.out.println("骑手已存在: " + rider.getId() + ", 手机号: " + rider.getPhone() + ", 姓名: " + rider.getRealName());
                return Response.badRequest("该手机号和姓名已注册为骑手");
            }
            
            // 检查是否有待审核的申请
            java.util.List<RiderApplication> pendingApplications = riderApplicationService.getApplicationsByUserId(userId);
            for (RiderApplication app : pendingApplications) {
                if ("pending".equals(app.getStatus())) {
                    return Response.badRequest("你已有待审核的申请，请耐心等待审核结果");
                }
            }

            // 创建骑手申请表单
            RiderApplication application = new RiderApplication();
            application.setUserId(userId);
            application.setRealName(name);
            application.setPhone(phone);
            application.setIdCard(idCard);
            application.setIdPhoto(idCardFront);
            application.setIdPhotoBack(idCardBack);
            application.setStatus("pending");
            application.setPassword(password);

            System.out.println("准备保存骑手申请: " + application);
            
            // 保存到数据库
            RiderApplication savedApplication = riderApplicationService.createApplication(application);
            
            System.out.println("骑手申请已保存，ID: " + savedApplication.getId());

            // 返回成功响应
            java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("message", "骑手申请已提交，我们将在1-3个工作日内完成审核");
            responseMap.put("status", "pending");
            responseMap.put("applicationId", savedApplication.getId());

            return Response.success(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.internalError("申请提交失败：" + e.getMessage());
        }
    }
}
