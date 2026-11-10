package com.warrantyvault.dto.response;

import com.warrantyvault.model.entity.ServiceLog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServiceLogResponse {
    private Long id;
    private Long assetId;
    private String assetName;
    private LocalDate serviceDate;
    private String serviceCenter;
    private String technicianContact;
    private BigDecimal cost;
    private String workNotes;
    private String receiptUrl;
    private LocalDateTime createdAt;

    public ServiceLogResponse() {}

    public ServiceLogResponse(Long id, Long assetId, String assetName, LocalDate serviceDate,
                              String serviceCenter, String technicianContact, BigDecimal cost,
                              String workNotes, String receiptUrl, LocalDateTime createdAt) {
        this.id = id;
        this.assetId = assetId;
        this.assetName = assetName;
        this.serviceDate = serviceDate;
        this.serviceCenter = serviceCenter;
        this.technicianContact = technicianContact;
        this.cost = cost;
        this.workNotes = workNotes;
        this.receiptUrl = receiptUrl;
        this.createdAt = createdAt;
    }

    public static ServiceLogResponse fromEntity(ServiceLog log) {
        if (log == null) return null;
        ServiceLogResponse response = new ServiceLogResponse();
        response.setId(log.getId());
        response.setAssetId(log.getAsset() != null ? log.getAsset().getId() : null);
        response.setAssetName(log.getAsset() != null ? log.getAsset().getAssetName() : null);
        response.setServiceDate(log.getServiceDate());
        response.setServiceCenter(log.getServiceCenter());
        response.setTechnicianContact(log.getTechnicianContact());
        response.setCost(log.getCost());
        response.setWorkNotes(log.getWorkNotes());
        response.setReceiptUrl(log.getReceiptUrl());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

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
}
