package com.medical.alert.repository;

import com.medical.alert.entity.Alert;
import com.medical.alert.enums.AlertStatus;
import com.medical.alert.enums.AlertType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByType(AlertType type);

    Optional<Alert> findByTypeAndBatchIdAndStatus(
            AlertType type,
            Long batchId,
            AlertStatus status
    );

    Optional<Alert> findByTypeAndSourceIncidentIdAndStatus(
            AlertType type,
            Long sourceIncidentId,
            AlertStatus status
    );

    boolean existsByTypeAndBatchIdAndStatus(
            AlertType type,
            Long batchId,
            AlertStatus status
    );

    boolean existsByTypeAndBatchId(AlertType type, Long batchId);

    boolean existsByTypeAndSourceIncidentIdAndStatus(
            AlertType type,
            Long sourceIncidentId,
            AlertStatus status
    );

    boolean existsByTypeAndSourceIncidentId(AlertType type, Long sourceIncidentId);
}
