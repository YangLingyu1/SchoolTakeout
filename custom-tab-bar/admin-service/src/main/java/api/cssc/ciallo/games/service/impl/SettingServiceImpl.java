package api.cssc.ciallo.games.service.impl;

import api.cssc.ciallo.games.entity.Setting;
import api.cssc.ciallo.games.repository.SettingRepository;
import api.cssc.ciallo.games.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Override
    public Setting getSettingById(Integer id) {
        return settingRepository.findById(id).orElse(null);
    }

    @Override
    public Setting getSettingByKey(String key) {
        Optional<Setting> setting = settingRepository.findByKey(key);
        return setting.orElse(null);
    }

    @Override
    public List<Setting> getSettings() {
        return settingRepository.findAll();
    }

    @Override
    public Setting createSetting(Setting setting) {
        return settingRepository.save(setting);
    }

    @Override
    public Setting updateSetting(Setting setting) {
        return settingRepository.save(setting);
    }

    @Override
    public void deleteSetting(Integer id) {
        settingRepository.deleteById(id);
    }

    @Override
    public String getSettingValue(String key) {
        Optional<Setting> setting = settingRepository.findByKey(key);
        return setting.map(Setting::getValue).orElse(null);
    }

    @Override
    public void updateSettingValue(String key, String value) {
        Optional<Setting> settingOpt = settingRepository.findByKey(key);
        if (settingOpt.isPresent()) {
            Setting setting = settingOpt.get();
            setting.setValue(value);
            settingRepository.save(setting);
        } else {
            Setting newSetting = new Setting();
            newSetting.setKey(key);
            newSetting.setValue(value);
            settingRepository.save(newSetting);
        }
    }

    @Override
    public Map<String, String> getAllSettings() {
        List<Setting> settings = settingRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (Setting setting : settings) {
            result.put(setting.getKey(), setting.getValue());
        }
        return result;
    }
}