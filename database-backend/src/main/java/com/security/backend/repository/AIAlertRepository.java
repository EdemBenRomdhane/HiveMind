package com.security.backend.repository;

import com.security.backend.model.AIAlert;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface AIAlertRepository extends CassandraRepository<AIAlert, UUID> {
    List<AIAlert> findByDeviceId(String deviceId);
}
