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
            taskContextProvider.get().setDescription(String.format("Initialize setting %s", name));
            Setting setting = new Setting();
            setting.setName(name);
            setting.setValue(value);
            settingRepository.save(setting);
            return value;
        });
    }

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public String getString(String name, Supplier<? extends String> value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getString(name).orElseGet(() -> {
            taskContextProvider.get().setDescription(String.format("Initialize setting %s", name));
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

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public Integer getInteger(String name, int value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getInteger(name).orElseGet(() -> {
            taskContextProvider.get().setDescription(String.format("Initialize setting %s", name));
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

    @Activity("FETCH SETTING")
    @Transactional
    @Override
    public Long getLong(String name, long value) {
        taskContextProvider.get().setDescription(String.format("Fetch setting %s", name));
        return getLong(name).orElseGet(() -> {
            taskContextProvider.get().setDescription(String.format("Initialize setting %s", name));
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
