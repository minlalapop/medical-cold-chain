package com.medical.alert.entity;

import com.medical.alert.enums.AlertPriority;
import com.medical.alert.enums.AlertStatus;
import com.medical.alert.enums.AlertType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    private AlertPriority priority;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    private String title;

    @Column(length = 1000)
    private String message;

    private Long productId;
    private String productName;

    private Long batchId;
    private String batchNumber;

    private Long locationId;
    private String locationName;

    private Long sourceIncidentId;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private String resolvedBy;
    private String resolutionNote;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = AlertStatus.OPEN;
    }
}
