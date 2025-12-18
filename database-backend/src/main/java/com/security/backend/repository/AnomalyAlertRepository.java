package com.security.backend.repository;

import com.security.backend.model.AnomalyAlert;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface AnomalyAlertRepository extends CassandraRepository<AnomalyAlert, UUID> {
    List<AnomalyAlert> findByDeviceId(String deviceId);
}
