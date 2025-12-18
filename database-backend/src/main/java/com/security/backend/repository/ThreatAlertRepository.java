package com.security.backend.repository;

import com.security.backend.model.ThreatAlert;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface ThreatAlertRepository extends CassandraRepository<ThreatAlert, UUID> {
    List<ThreatAlert> findByDeviceId(String deviceId);

    List<ThreatAlert> findBySeverity(String severity);

    List<ThreatAlert> findBySource(String source);
}
