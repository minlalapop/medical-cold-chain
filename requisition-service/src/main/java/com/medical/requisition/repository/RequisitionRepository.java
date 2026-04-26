package com.medical.requisition.repository;

import com.medical.requisition.entity.Requisition;
import com.medical.requisition.enums.RequisitionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitionRepository extends JpaRepository<Requisition, Long> {

    List<Requisition> findByStatus(RequisitionStatus status);

    List<Requisition> findByRequesterNameIgnoreCase(String requesterName);
}
