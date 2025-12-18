package com.security.backend.repository;

import com.security.backend.model.SecurityEvent;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface SecurityEventRepository extends CassandraRepository<SecurityEvent, UUID> {
    List<SecurityEvent> findByDeviceId(String deviceId);
}
