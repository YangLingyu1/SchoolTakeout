package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.util.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // 获取用户购买历史商品
    @GetMapping("/purchased-products")
    public Response<?> getPurchasedProducts() {
        // 模拟返回购买历史数据
        return Response.success(Collections.emptyList());
    }
}
