package com.github.olaleyeone.entitysearch.util;

import com.github.olaleyeone.test.entity.EntityTest;
import com.github.olaleyeone.test.entity.data.EntityRecord;
import com.github.olaleyeone.test.entity.repository.EntityRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OffsetBasedPageRequestTest extends EntityTest {

    List<EntityRecord> entityRecords;

    @Autowired
    private EntityRecordRepository entityRecordRepository;

    @BeforeEach
    void setUp() {
        entityRecords = new ArrayList<>();
        int size = 10;
        for (int i = 0; i < size; i++) {
            EntityRecord entityRecord = new EntityRecord();
            entityRecord.setName(Character.getName(i));
            entityRecord.setActive(true);
            entityManager.persist(entityRecord);
            entityRecords.add(entityRecord);
        }
    }

    @Test
    void getPageSize() {
        Page<EntityRecord> page = entityRecordRepository.findAll(new OffsetBasedPageRequest(4L, 2, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(page);
        assertEquals(2, page.getContent().size());
        assertTrue(page.hasPrevious());
        assertTrue(page.hasNext());
    }

    @Test
    void getPrevious() {
        Page<EntityRecord> page = entityRecordRepository.findAll(new OffsetBasedPageRequest(2L, 2, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(page);
        assertTrue(page.hasPrevious());
        page = entityRecordRepository.findAll(page.previousPageable());
        assertFalse(page.hasPrevious());
    }

    @Test
    void getNext() {
        Page<EntityRecord> page = entityRecordRepository.findAll(new OffsetBasedPageRequest(6L, 2, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(page);
        assertTrue(page.hasNext());
        page = entityRecordRepository.findAll(page.nextPageable());
        assertFalse(page.hasNext());
    }

    @Test
    void getOffset() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(5L, 10, Sort.by(Sort.Order.asc("id")));
        Page<EntityRecord> page = entityRecordRepository.findAll(pageRequest);
        assertNotNull(page);
        assertEquals(5, page.getContent().size());
        assertFalse(pageRequest.hasPrevious());
        assertEquals(0L, pageRequest.previousOrFirst().getOffset());
    }

    @Test
    void getSort() {
        Page<EntityRecord> page = entityRecordRepository.findAll(new OffsetBasedPageRequest(0L, 10, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(page);
        List<Long> ids = entityRecords.stream().map(EntityRecord::getId).sorted().collect(Collectors.toList());
        List<Long> fetchedIds = page.stream().map(EntityRecord::getId).collect(Collectors.toList());
        assertEquals(ids, fetchedIds);
    }

    @Test
    void getSort2() {
        Page<EntityRecord> page = entityRecordRepository.findAll(new OffsetBasedPageRequest(0L, 10, Sort.by(Sort.Order.desc("id"))));
        assertNotNull(page);
        List<Long> ids = entityRecords.stream().map(EntityRecord::getId).sorted().collect(Collectors.toList());
        List<Long> fetchedIds = page.stream().map(EntityRecord::getId).collect(Collectors.toList());
        assertNotEquals(ids, fetchedIds);
    }

    @Test
    void getFirst() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(10L, 10, Sort.by(Sort.Order.desc("id")));
        Pageable first = offsetBasedPageRequest.first();
        assertNotNull(first);
        assertEquals(0L, first.getOffset());
        assertEquals(offsetBasedPageRequest.getLimit(), first.getPageSize());
        assertEquals(offsetBasedPageRequest.getSort(), first.getSort());
    }

    @Test
    void illegalOffset() {
        assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPageRequest(-1L, 10, Sort.by(Sort.Order.desc("id"))));
    }

    @Test
    void illegalLimit() {
        assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPageRequest(0L, 0, Sort.by(Sort.Order.desc("id"))));
    }
}