package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.repository.SettingRepository;
import com.olaleyeone.data.entity.Setting;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Supplier;

@Named
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final Provider<TaskContext> taskContextProvider;
    private final SettingRepository settingRepository;

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public String getString(String name, String value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getString(name).orElseGet(() -> {
            initializeSetting(name, value);
            return value;
        });
    }

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public String getString(String name, Supplier<? extends String> value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getString(name).orElseGet(() -> {
            initializeSetting(name, value.get());
            return value.get();
        });
    }

    @Override
    public Optional<String> getString(String name) {
        Setting setting = settingRepository.findByName(name);
        if (setting == null) {
            return Optional.empty();
        }
        return Optional.of(setting.getValue());
    }

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public Integer getInteger(String name, int value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getInteger(name).orElseGet(() -> {
            initializeSetting(name, String.valueOf(value));
            return value;
        });
    }

    @Override
    public Optional<Integer> getInteger(String name) {
        Setting setting = settingRepository.findByName(name);
        if (setting == null) {
            return Optional.empty();
        }
        return Optional.of(Integer.valueOf(setting.getValue()));
    }

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public Long getLong(String name, long value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getLong(name).orElseGet(() -> {
            initializeSetting(name, String.valueOf(value));
            return value;
        });
    }

    @Override
    public Optional<Long> getLong(String name) {
        Setting setting = settingRepository.findByName(name);
        if (setting == null) {
            return Optional.empty();
        }
        return Optional.of(Long.valueOf(setting.getValue()));
    }

    private Setting initializeSetting(String name, String value) {
        taskContextProvider.get().setDescription(String.format("Initialize setting %s", name));
        Setting setting = new Setting();
        setting.setName(name);
        setting.setValue(value);
        settingRepository.save(setting);
        return setting;
    }
}
