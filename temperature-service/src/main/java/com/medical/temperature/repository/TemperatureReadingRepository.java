package com.medical.temperature.repository;

import com.medical.temperature.entity.TemperatureReading;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {

    List<TemperatureReading> findByLocationId(Long locationId);
}
