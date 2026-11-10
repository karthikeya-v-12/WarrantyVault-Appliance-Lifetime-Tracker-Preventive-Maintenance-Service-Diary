package com.warrantyvault.dto.response;

import com.warrantyvault.model.entity.HouseholdAsset;
import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AssetResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String assetName;
    private AssetCategory category;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private Integer warrantyMonths;
    private Integer extendedWarrantyMonths;
    private Integer totalWarrantyMonths;
    private LocalDate warrantyExpiryDate;

    // Transient Lifetime Attributes
    private Boolean isUnderWarranty;
    private Long daysUntilWarrantyExpiry;
    private String warrantyUrgencyStatus; // ACTIVE, EXPIRING_SOON (<= 30 days), EXPIRED

    private String serialNumber;
    private String invoiceUrl;
    private AssetStatus status;

    private BigDecimal totalServiceCost;
    private BigDecimal totalCostOfOwnership;

    private List<MaintenanceScheduleResponse> maintenanceSchedules;
    private List<ServiceLogResponse> serviceLogs;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetResponse() {}

    public static AssetResponse fromEntity(HouseholdAsset asset, LocalDate referenceDate, BigDecimal totalServiceCost) {
        if (asset == null) return null;
        asset.populateTransientWarrantyStatus(referenceDate);

        BigDecimal serviceCost = totalServiceCost != null ? totalServiceCost : BigDecimal.ZERO;
        BigDecimal purchasePrice = asset.getPurchasePrice() != null ? asset.getPurchasePrice() : BigDecimal.ZERO;
        BigDecimal tco = purchasePrice.add(serviceCost);

        String urgencyStatus = "ACTIVE";
        if (asset.getDaysUntilWarrantyExpiry() != null) {
            if (asset.getDaysUntilWarrantyExpiry() < 0) {
                urgencyStatus = "EXPIRED";
            } else if (asset.getDaysUntilWarrantyExpiry() <= 30) {
                urgencyStatus = "EXPIRING_SOON";
            }
        }

        int totalMonths = (asset.getWarrantyMonths() != null ? asset.getWarrantyMonths() : 0)
                + (asset.getExtendedWarrantyMonths() != null ? asset.getExtendedWarrantyMonths() : 0);

        List<MaintenanceScheduleResponse> schedules = asset.getMaintenanceSchedules() != null
                ? asset.getMaintenanceSchedules().stream()
                    .map(s -> MaintenanceScheduleResponse.fromEntity(s, referenceDate))
                    .collect(Collectors.toList())
                : Collections.emptyList();

        List<ServiceLogResponse> logs = asset.getServiceLogs() != null
                ? asset.getServiceLogs().stream()
                    .map(ServiceLogResponse::fromEntity)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setUserId(asset.getUser() != null ? asset.getUser().getId() : null);
        response.setUserName(asset.getUser() != null ? asset.getUser().getName() : null);
        response.setAssetName(asset.getAssetName());
        response.setCategory(asset.getCategory());
        response.setPurchaseDate(asset.getPurchaseDate());
        response.setPurchasePrice(purchasePrice);
        response.setWarrantyMonths(asset.getWarrantyMonths());
        response.setExtendedWarrantyMonths(asset.getExtendedWarrantyMonths());
        response.setTotalWarrantyMonths(totalMonths);
        response.setWarrantyExpiryDate(asset.getWarrantyExpiryDate());
        response.setIsUnderWarranty(asset.getIsUnderWarranty());
        response.setDaysUntilWarrantyExpiry(asset.getDaysUntilWarrantyExpiry());
        response.setWarrantyUrgencyStatus(urgencyStatus);
        response.setSerialNumber(asset.getSerialNumber());
        response.setInvoiceUrl(asset.getInvoiceUrl());
        response.setStatus(asset.getStatus());
        response.setTotalServiceCost(serviceCost);
        response.setTotalCostOfOwnership(tco);
        response.setMaintenanceSchedules(schedules);
        response.setServiceLogs(logs);
        response.setCreatedAt(asset.getCreatedAt());
        response.setUpdatedAt(asset.getUpdatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public AssetCategory getCategory() { return category; }
    public void setCategory(AssetCategory category) { this.category = category; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public Integer getExtendedWarrantyMonths() { return extendedWarrantyMonths; }
    public void setExtendedWarrantyMonths(Integer extendedWarrantyMonths) { this.extendedWarrantyMonths = extendedWarrantyMonths; }

    public Integer getTotalWarrantyMonths() { return totalWarrantyMonths; }
    public void setTotalWarrantyMonths(Integer totalWarrantyMonths) { this.totalWarrantyMonths = totalWarrantyMonths; }

    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }

    public Boolean getIsUnderWarranty() { return isUnderWarranty; }
    public void setIsUnderWarranty(Boolean isUnderWarranty) { this.isUnderWarranty = isUnderWarranty; }

    public Long getDaysUntilWarrantyExpiry() { return daysUntilWarrantyExpiry; }
    public void setDaysUntilWarrantyExpiry(Long daysUntilWarrantyExpiry) { this.daysUntilWarrantyExpiry = daysUntilWarrantyExpiry; }

    public String getWarrantyUrgencyStatus() { return warrantyUrgencyStatus; }
    public void setWarrantyUrgencyStatus(String warrantyUrgencyStatus) { this.warrantyUrgencyStatus = warrantyUrgencyStatus; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }

    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }

    public BigDecimal getTotalServiceCost() { return totalServiceCost; }
    public void setTotalServiceCost(BigDecimal totalServiceCost) { this.totalServiceCost = totalServiceCost; }

    public BigDecimal getTotalCostOfOwnership() { return totalCostOfOwnership; }
    public void setTotalCostOfOwnership(BigDecimal totalCostOfOwnership) { this.totalCostOfOwnership = totalCostOfOwnership; }

    public List<MaintenanceScheduleResponse> getMaintenanceSchedules() { return maintenanceSchedules; }
    public void setMaintenanceSchedules(List<MaintenanceScheduleResponse> maintenanceSchedules) { this.maintenanceSchedules = maintenanceSchedules; }

    public List<ServiceLogResponse> getServiceLogs() { return serviceLogs; }
    public void setServiceLogs(List<ServiceLogResponse> serviceLogs) { this.serviceLogs = serviceLogs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
