package com.warrantyvault.dto.request;

import com.warrantyvault.model.enums.AssetCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AssetRegistrationRequest {

    private Long userId;

    @NotBlank(message = "Asset name is required")
    @Size(max = 255, message = "Asset name must be under 255 characters")
    private String assetName;

    @NotNull(message = "Asset category is required")
    private AssetCategory category;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price cannot be negative")
    private BigDecimal purchasePrice;

    @NotNull(message = "Warranty duration in months is required")
    @Min(value = 0, message = "Warranty months must be greater than or equal to 0")
    private Integer warrantyMonths;

    @Min(value = 0, message = "Extended warranty months must be greater than or equal to 0")
    private Integer extendedWarrantyMonths;

    @Size(max = 100, message = "Serial number must be under 100 characters")
    private String serialNumber;

    @Size(max = 500, message = "Invoice URL must be under 500 characters")
    private String invoiceUrl;

    private List<InitialMaintenanceTask> initialMaintenanceTasks;

    public AssetRegistrationRequest() {
    }

    public AssetRegistrationRequest(Long userId, String assetName, AssetCategory category,
                                    LocalDate purchaseDate, BigDecimal purchasePrice,
                                    Integer warrantyMonths, Integer extendedWarrantyMonths,
                                    String serialNumber, String invoiceUrl,
                                    List<InitialMaintenanceTask> initialMaintenanceTasks) {
        this.userId = userId;
        this.assetName = assetName;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.warrantyMonths = warrantyMonths;
        this.extendedWarrantyMonths = extendedWarrantyMonths;
        this.serialNumber = serialNumber;
        this.invoiceUrl = invoiceUrl;
        this.initialMaintenanceTasks = initialMaintenanceTasks;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }

    public List<InitialMaintenanceTask> getInitialMaintenanceTasks() { return initialMaintenanceTasks; }
    public void setInitialMaintenanceTasks(List<InitialMaintenanceTask> initialMaintenanceTasks) {
        this.initialMaintenanceTasks = initialMaintenanceTasks;
    }

    public static class InitialMaintenanceTask {
        @NotBlank(message = "Task name is required")
        private String taskName;

        @NotNull(message = "Interval in months is required")
        @Min(value = 1, message = "Interval must be at least 1 month")
        private Integer intervalMonths;

        public InitialMaintenanceTask() {}

        public InitialMaintenanceTask(String taskName, Integer intervalMonths) {
            this.taskName = taskName;
            this.intervalMonths = intervalMonths;
        }

        public String getTaskName() { return taskName; }
        public void setTaskName(String taskName) { this.taskName = taskName; }

        public Integer getIntervalMonths() { return intervalMonths; }
        public void setIntervalMonths(Integer intervalMonths) { this.intervalMonths = intervalMonths; }
    }
}
