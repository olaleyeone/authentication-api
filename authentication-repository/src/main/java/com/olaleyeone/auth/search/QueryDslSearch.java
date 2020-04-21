package com.olaleyeone.auth.search;

import com.olaleyeone.auth.data.entity.QSetting;
import com.olaleyeone.auth.data.entity.Setting;
import com.querydsl.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

class QueryDslSearch {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Setting> getAllSettings() {
        return new JPAQuery<Setting>(entityManager)
                .from(QSetting.setting)
                .fetch();
    }
}
