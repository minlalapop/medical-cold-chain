package com.medical.temperature.repository;

import com.medical.temperature.entity.TemperatureIncident;
import com.medical.temperature.enums.IncidentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureIncidentRepository extends JpaRepository<TemperatureIncident, Long> {

    List<TemperatureIncident> findByStatus(IncidentStatus status);

    List<TemperatureIncident> findByLocationId(Long locationId);
}
