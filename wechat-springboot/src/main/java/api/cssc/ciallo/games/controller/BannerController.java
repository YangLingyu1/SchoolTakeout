package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.util.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    // 获取banner列表
    @GetMapping
    public Response<?> getBanners() {
        // Mock banner data
        List<Map<String, Object>> banners = new ArrayList<>();
        banners.add(Map.of(
                "id", 1,
                "image", "https://via.placeholder.com/750x300/FF6B35/FFFFFF?text=Banner+1",
                "link", ""
        ));
        banners.add(Map.of(
                "id", 2,
                "image", "https://via.placeholder.com/750x300/4ECDC4/FFFFFF?text=Banner+2",
                "link", ""
        ));
        banners.add(Map.of(
                "id", 3,
                "image", "https://via.placeholder.com/750x300/FFE66D/FFFFFF?text=Banner+3",
                "link", ""
        ));
        return Response.success(banners);
    }
}
