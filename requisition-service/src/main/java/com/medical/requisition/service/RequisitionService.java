package com.medical.requisition.service;

import com.medical.requisition.client.AuditClient;
import com.medical.requisition.client.AlertClient;
import com.medical.requisition.client.InventoryClient;
import com.medical.requisition.dto.AlertResponse;
import com.medical.requisition.dto.CreateRequisitionRequest;
import com.medical.requisition.dto.RequisitionItemRequest;
import com.medical.requisition.dto.StockResponse;
import com.medical.requisition.entity.Requisition;
import com.medical.requisition.entity.RequisitionItem;
import com.medical.requisition.enums.Department;
import com.medical.requisition.enums.RequisitionStatus;
import com.medical.requisition.exception.BadRequestException;
import com.medical.requisition.exception.ResourceNotFoundException;
import com.medical.requisition.repository.RequisitionRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final InventoryClient inventoryClient;
    private final AuditClient auditClient;
    private final AlertClient alertClient;

    public Requisition createRequisition(CreateRequisitionRequest request) {
        Requisition requisition = Requisition.builder()
                .requesterName(request.getRequesterName())
                .department(request.getDepartment().name())
                .status(RequisitionStatus.PENDING)
                .build();

        for (RequisitionItemRequest itemRequest : request.getItems()) {
            RequisitionItem item = RequisitionItem.builder()
                    .productId(itemRequest.getProductId())
                    .productName(itemRequest.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .build();

            requisition.addItem(item);
        }

        Requisition savedRequisition = requisitionRepository.save(requisition);
        auditClient.log(request.getRequesterName(), "CREATE", savedRequisition.getId(), "Requisition created");
        return savedRequisition;
    }

    public List<Requisition> getAllRequisitions() {
        return requisitionRepository.findAll();
    }

    public List<Requisition> getPendingRequisitions() {
        return requisitionRepository.findByStatus(RequisitionStatus.PENDING);
    }

    public List<Department> getDepartments() {
        return Arrays.asList(Department.values());
    }

    public Map<String, Long> getDepartmentStats() {
        Map<String, Long> stats = new TreeMap<>();

        for (Requisition requisition : requisitionRepository.findAll()) {
            stats.merge(requisition.getDepartment(), 1L, Long::sum);
        }

        return stats;
    }

    public Requisition getRequisitionById(Long id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requisition not found with id: " + id));
    }

    @Transactional
    public Requisition approveRequisition(Long id, String pharmacistName) {
        Requisition requisition = getRequisitionById(id);

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new BadRequestException("Only PENDING requisitions can be approved");
        }

        alertClient.refreshAlerts();
        List<AlertResponse> openAlerts = alertClient.getOpenAlerts();

        for (RequisitionItem item : requisition.getItems()) {
            openAlerts.stream()
                    .filter(alert -> item.getProductId().equals(alert.getProductId()))
                    .findFirst()
                    .ifPresent(alert -> {
                        throw new BadRequestException(
                                "Cannot approve " + item.getProductName()
                                        + ": open " + alert.getType()
                                        + " alert - " + alert.getMessage()
                        );
                    });

            boolean available = inventoryClient.checkStock(item.getProductId(), item.getQuantity());

            if (!available) {
                StockResponse stock = inventoryClient.getStock(item.getProductId());
                String productName = item.getProductName() != null && !item.getProductName().isBlank()
                        ? item.getProductName()
                        : stock.getProductName();

                throw new BadRequestException(
                        "No usable stock for " + productName
                                + ". Requested: " + item.getQuantity()
                                + ", available: " + stock.getTotalQuantity()
                                + ". Batches expired or expiring today cannot be used."
                );
            }
        }

        requisition.setStatus(RequisitionStatus.APPROVED);
        requisition.setValidatedBy(pharmacistName);
        requisition.setValidatedAt(LocalDateTime.now());

        Requisition savedRequisition = requisitionRepository.save(requisition);
        auditClient.log(pharmacistName, "APPROVE", savedRequisition.getId(), "Requisition approved");
        return savedRequisition;
    }

    @Transactional
    public Requisition rejectRequisition(Long id, String pharmacistName, String reason) {
        Requisition requisition = getRequisitionById(id);

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new BadRequestException("Only PENDING requisitions can be rejected");
        }

        requisition.setStatus(RequisitionStatus.REJECTED);
        requisition.setValidatedBy(pharmacistName);
        requisition.setValidatedAt(LocalDateTime.now());
        requisition.setRejectionReason(reason);

        Requisition savedRequisition = requisitionRepository.save(requisition);
        auditClient.log(pharmacistName, "REJECT", savedRequisition.getId(), "Requisition rejected: " + reason);
        return savedRequisition;
    }

    @Transactional
    public Requisition deliverRequisition(Long id, String deliveredBy) {
        Requisition requisition = getRequisitionById(id);

        if (requisition.getStatus() != RequisitionStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED requisitions can be delivered");
        }

        for (RequisitionItem item : requisition.getItems()) {
            inventoryClient.consumeStock(
                    item.getProductId(),
                    item.getQuantity(),
                    "Delivered requisition #" + requisition.getId()
            );
        }

        requisition.setStatus(RequisitionStatus.DELIVERED);
        requisition.setDeliveredAt(LocalDateTime.now());

        Requisition savedRequisition = requisitionRepository.save(requisition);
        auditClient.log(deliveredBy, "DELIVER", savedRequisition.getId(), "Requisition delivered");
        return savedRequisition;
    }

    @Transactional
    public Requisition cancelRequisition(Long id, String actorName, String reason) {
        Requisition requisition = getRequisitionById(id);

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new BadRequestException("Only PENDING requisitions can be cancelled");
        }

        requisition.setStatus(RequisitionStatus.CANCELLED);
        requisition.setRejectionReason(reason);
        requisition.setValidatedBy(actorName);
        requisition.setValidatedAt(LocalDateTime.now());

        Requisition savedRequisition = requisitionRepository.save(requisition);
        auditClient.log(actorName, "REJECT", savedRequisition.getId(), "Requisition cancelled: " + reason);
        return savedRequisition;
    }
}
