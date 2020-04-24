package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.data.entity.Setting;
import com.olaleyeone.auth.repository.SettingRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Supplier;

@Named
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final Provider<ActivityLogger> activityLoggerProvider;
    private final SettingRepository settingRepository;

    @Transactional
    @Override
    public String getString(String name, String value) {
        return getString(name).orElseGet(() -> {
            activityLoggerProvider.get().log("NEW SETTING", String.format("Creating setting %s", name));
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            settingRepository.save(setting);
            return value;
        });
    }

    @Transactional
    @Override
    public String getString(String name, Supplier<? extends String> value) {
        return getString(name).orElseGet(() -> {
            activityLoggerProvider.get().log("NEW SETTING", String.format("Creating setting %s", name));
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value.get());
            settingRepository.save(setting);
            return value.get();
        });
    }

    @Override
    public Optional<String> getString(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(setting.getValue()) : Optional.empty();
    }

    @Transactional
    @Override
    public Integer getInteger(String name, int value) {
        return getInteger(name).orElseGet(() -> {
            activityLoggerProvider.get().log("NEW SETTING", String.format("Creating setting %s", name));
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(String.valueOf(value));
            settingRepository.save(setting);
            return value;
        });
    }

    @Override
    public Optional<Integer> getInteger(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(Integer.valueOf(setting.getValue())) : Optional.empty();
    }

    @Transactional
    @Override
    public Long getLong(String name, long value) {
        return getLong(name).orElseGet(() -> {
            activityLoggerProvider.get().log("NEW SETTING", String.format("Creating setting %s", name));
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(String.valueOf(value));
            settingRepository.save(setting);
            return value;
        });
    }

    @Override
    public Optional<Long> getLong(String name) {
        Setting setting = settingRepository.findByName(name);
        return setting != null ? Optional.of(Long.valueOf(setting.getValue())) : Optional.empty();
    }
}
