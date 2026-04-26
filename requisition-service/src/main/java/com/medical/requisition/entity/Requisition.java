package com.medical.requisition.entity;

import com.medical.requisition.enums.RequisitionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Requisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requesterName;

    private String department;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;

    private String rejectionReason;

    private String validatedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime validatedAt;

    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RequisitionItem> items = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.requestedAt = LocalDateTime.now();
        this.status = RequisitionStatus.PENDING;
    }

    public void addItem(RequisitionItem item) {
        items.add(item);
        item.setRequisition(this);
    }
}
