package api.cssc.ciallo.games.controller;

import api.cssc.ciallo.games.entity.Setting;
import api.cssc.ciallo.games.service.SettingService;
import api.cssc.ciallo.games.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    public Response<?> getSettings() {
        List<Setting> settings = settingService.getSettings();
        return Response.success(settings);
    }

    @GetMapping("/{id}")
    public Response<?> getSettingById(@PathVariable Integer id) {
        Setting setting = settingService.getSettingById(id);
        if (setting == null) {
            return Response.notFound("设置不存在");
        }
        return Response.success(setting);
    }

    @PostMapping
    public Response<?> createSetting(@RequestBody Setting setting) {
        Setting createdSetting = settingService.createSetting(setting);
        return Response.success(createdSetting);
    }

    @PutMapping("/{id}")
    public Response<?> updateSetting(@PathVariable Integer id, @RequestBody Setting setting) {
        setting.setId(id);
        Setting updatedSetting = settingService.updateSetting(setting);
        return Response.success(updatedSetting);
    }

    @DeleteMapping("/{id}")
    public Response<?> deleteSetting(@PathVariable Integer id) {
        settingService.deleteSetting(id);
        return Response.success();
    }

    @GetMapping("/all")
    public Response<?> getAllSettings() {
        Map<String, String> settings = settingService.getAllSettings();
        return Response.success(settings);
    }

    @PutMapping("/update")
    public Response<?> updateSettingValue(@RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");
        if (key == null || value == null) {
            return Response.badRequest("参数错误");
        }
        settingService.updateSettingValue(key, value);
        return Response.success();
    }
}