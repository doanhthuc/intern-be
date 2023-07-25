package com.mgmtp.easyquizy.repository;

import com.mgmtp.easyquizy.model.event.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {

    @Query(" SELECT DISTINCT YEAR(e.startDate) AS eventYear " +
            "FROM events e " +
            "ORDER BY YEAR(e.startDate) ASC")
    List<Integer> findDistinctByStartDateYear();
}
