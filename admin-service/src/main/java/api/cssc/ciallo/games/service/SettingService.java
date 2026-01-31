package api.cssc.ciallo.games.service;

import api.cssc.ciallo.games.entity.Setting;
import java.util.List;
import java.util.Map;

public interface SettingService {
    Setting getSettingById(Integer id);
    Setting getSettingByKey(String key);
    List<Setting> getSettings();
    Setting createSetting(Setting setting);
    Setting updateSetting(Setting setting);
    void deleteSetting(Integer id);
    String getSettingValue(String key);
    void updateSettingValue(String key, String value);
    Map<String, String> getAllSettings();
}