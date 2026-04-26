package com.medical.inventory.service;

import com.medical.inventory.client.AuditClient;
import com.medical.inventory.dto.LocationRequest;
import com.medical.inventory.entity.Location;
import com.medical.inventory.exception.ResourceNotFoundException;
import com.medical.inventory.repository.LocationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final AuditClient auditClient;

    public Location createLocation(LocationRequest request) {
        Location location = Location.builder()
                .name(request.getName())
                .type(request.getType())
                .build();

        Location savedLocation = locationRepository.save(location);
        auditClient.log("system", "CREATE", "Location", savedLocation.getId(), "Location created: " + savedLocation.getName());
        return savedLocation;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    public void deleteLocation(Long id) {
        Location location = getLocationById(id);
        locationRepository.delete(location);
        auditClient.log("system", "DELETE", "Location", id, "Location deleted: " + location.getName());
    }
}
