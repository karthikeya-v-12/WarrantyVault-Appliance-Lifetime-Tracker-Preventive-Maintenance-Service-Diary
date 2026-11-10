package com.warrantyvault.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "service_logs")
public class ServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private HouseholdAsset asset;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "service_center", length = 255)
    private String serviceCenter;

    @Column(name = "technician_contact", length = 100)
    private String technicianContact;

    @Column(name = "cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "work_notes", columnDefinition = "TEXT")
    private String workNotes;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ServiceLog() {
    }

    public ServiceLog(Long id, HouseholdAsset asset, LocalDate serviceDate, String serviceCenter,
                      String technicianContact, BigDecimal cost, String workNotes,
                      String receiptUrl, LocalDateTime createdAt) {
        this.id = id;
        this.asset = asset;
        this.serviceDate = serviceDate;
        this.serviceCenter = serviceCenter;
        this.technicianContact = technicianContact;
        this.cost = cost != null ? cost : BigDecimal.ZERO;
        this.workNotes = workNotes;
        this.receiptUrl = receiptUrl;
        this.createdAt = createdAt;
    }

    public static ServiceLogBuilder builder() {
        return new ServiceLogBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HouseholdAsset getAsset() { return asset; }
    public void setAsset(HouseholdAsset asset) { this.asset = asset; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public String getServiceCenter() { return serviceCenter; }
    public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }

    public String getTechnicianContact() { return technicianContact; }
    public void setTechnicianContact(String technicianContact) { this.technicianContact = technicianContact; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getWorkNotes() { return workNotes; }
    public void setWorkNotes(String workNotes) { this.workNotes = workNotes; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceLog that = (ServiceLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class ServiceLogBuilder {
        private Long id;
        private HouseholdAsset asset;
        private LocalDate serviceDate;
        private String serviceCenter;
        private String technicianContact;
        private BigDecimal cost = BigDecimal.ZERO;
        private String workNotes;
        private String receiptUrl;
        private LocalDateTime createdAt;

        ServiceLogBuilder() {}

        public ServiceLogBuilder id(Long id) { this.id = id; return this; }
        public ServiceLogBuilder asset(HouseholdAsset asset) { this.asset = asset; return this; }
        public ServiceLogBuilder serviceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; return this; }
        public ServiceLogBuilder serviceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; return this; }
        public ServiceLogBuilder technicianContact(String technicianContact) { this.technicianContact = technicianContact; return this; }
        public ServiceLogBuilder cost(BigDecimal cost) { this.cost = cost; return this; }
        public ServiceLogBuilder workNotes(String workNotes) { this.workNotes = workNotes; return this; }
        public ServiceLogBuilder receiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; return this; }
        public ServiceLogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ServiceLog build() {
            return new ServiceLog(id, asset, serviceDate, serviceCenter, technicianContact, cost, workNotes, receiptUrl, createdAt);
        }
    }
}
