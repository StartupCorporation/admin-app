package com.deye.web.repository;

import com.deye.web.entity.CallbackRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CallbackRequestRepository extends JpaRepository<CallbackRequestEntity, UUID> {
}
