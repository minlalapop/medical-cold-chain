package com.medical.temperature.entity;

import com.medical.temperature.enums.IncidentSeverity;
import com.medical.temperature.enums.IncidentStatus;
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
public class TemperatureIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long locationId;

    private String locationName;

    private double temperature;

    private double expectedMin;

    private double expectedMax;

    @Enumerated(EnumType.STRING)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private String resolvedBy;

    private String resolutionNote;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = IncidentStatus.OPEN;
    }
}
