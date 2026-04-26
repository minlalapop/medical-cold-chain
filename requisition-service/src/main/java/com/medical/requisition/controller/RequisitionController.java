package com.medical.requisition.controller;

import com.medical.requisition.dto.CreateRequisitionRequest;
import com.medical.requisition.enums.Department;
import com.medical.requisition.entity.Requisition;
import com.medical.requisition.service.RequisitionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requisitions")
@RequiredArgsConstructor
public class RequisitionController {

    private final RequisitionService requisitionService;

    @PostMapping
    public Requisition createRequisition(@Valid @RequestBody CreateRequisitionRequest request) {
        return requisitionService.createRequisition(request);
    }

    @GetMapping
    public List<Requisition> getAllRequisitions() {
        return requisitionService.getAllRequisitions();
    }

    @GetMapping("/pending")
    public List<Requisition> getPendingRequisitions() {
        return requisitionService.getPendingRequisitions();
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return requisitionService.getDepartments();
    }

    @GetMapping("/stats/departments")
    public Map<String, Long> getDepartmentStats() {
        return requisitionService.getDepartmentStats();
    }

    @GetMapping("/{id}")
    public Requisition getRequisitionById(@PathVariable Long id) {
        return requisitionService.getRequisitionById(id);
    }

    @PutMapping("/{id}/approve")
    public Requisition approveRequisition(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String pharmacistName = body.getOrDefault("pharmacistName", "Unknown pharmacist");
        return requisitionService.approveRequisition(id, pharmacistName);
    }

    @PutMapping("/{id}/reject")
    public Requisition rejectRequisition(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String pharmacistName = body.getOrDefault("pharmacistName", "Unknown pharmacist");
        String reason = body.getOrDefault("reason", "No reason provided");

        return requisitionService.rejectRequisition(id, pharmacistName, reason);
    }

    @PutMapping("/{id}/deliver")
    public Requisition deliverRequisition(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String deliveredBy = body != null ? body.getOrDefault("deliveredBy", "Unknown logistics") : "Unknown logistics";
        return requisitionService.deliverRequisition(id, deliveredBy);
    }

    @PutMapping("/{id}/cancel")
    public Requisition cancelRequisition(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String actorName = body.getOrDefault("actorName", "Unknown user");
        String reason = body.getOrDefault("reason", "Cancelled from dashboard");

        return requisitionService.cancelRequisition(id, actorName, reason);
    }
}
