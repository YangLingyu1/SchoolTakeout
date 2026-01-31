package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Setting;
import api.cssc.ciallo.games.repository.SettingRepository;
import api.cssc.ciallo.games.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingRepository settingRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/business-hours")
    public Response<?> getBusinessHours() {
        java.util.Optional<Setting> businessHoursSetting = settingRepository.findByKey("business_hours");
        java.util.Optional<Setting> nightHoursSetting = settingRepository.findByKey("night_hours");

        String openTime = "10:00";
        String closeTime = "23:00";
        String nightStart = "22:00";
        String nightEnd = "06:00";

        if (businessHoursSetting.isPresent()) {
            try {
                String value = businessHoursSetting.get().getValue();
                if (value.startsWith("{")) {
                    Map<String, String> json = objectMapper.readValue(value, Map.class);
                    if (json.containsKey("startTime")) {
                        openTime = json.get("startTime");
                    }
                    if (json.containsKey("endTime")) {
                        closeTime = json.get("endTime");
                    }
                }
            } catch (Exception e) {
                // 解析失败，使用默认值
            }
        }

        if (nightHoursSetting.isPresent()) {
            try {
                String value = nightHoursSetting.get().getValue();
                if (value.startsWith("{")) {
                    Map<String, String> json = objectMapper.readValue(value, Map.class);
                    if (json.containsKey("startTime")) {
                        nightStart = json.get("startTime");
                    }
                    if (json.containsKey("endTime")) {
                        nightEnd = json.get("endTime");
                    }
                }
            } catch (Exception e) {
                // 解析失败，使用默认值
            }
        }

        return Response.success(Map.of(
                "openTime", openTime,
                "closeTime", closeTime,
                "nightStart", nightStart,
                "nightEnd", nightEnd
        ));
    }

    @GetMapping("/night-delivery-buildings")
    public Response<?> getNightDeliveryBuildings() {
        // 返回默认的夜间配送楼栋（1-3号楼）
        String buildings = "1,2,3";
        return Response.success(Map.of(
                "buildings", buildings
        ));
    }
}