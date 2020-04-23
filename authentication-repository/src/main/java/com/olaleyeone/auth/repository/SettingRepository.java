package com.olaleyeone.auth.repository;

import com.olaleyeone.data.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    Setting findByName(String name);

    List<Setting> findByNameIn(List<String> string);
}
