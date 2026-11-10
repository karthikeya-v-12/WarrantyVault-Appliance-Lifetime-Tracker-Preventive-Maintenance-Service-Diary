package com.warrantyvault.dto.request;

import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AssetUpdateRequest {

    @Size(max = 255, message = "Asset name must be under 255 characters")
    private String assetName;

    private AssetCategory category;

    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price cannot be negative")
    private BigDecimal purchasePrice;

    @Min(value = 0, message = "Warranty months must be greater than or equal to 0")
    private Integer warrantyMonths;

    @Min(value = 0, message = "Extended warranty months must be greater than or equal to 0")
    private Integer extendedWarrantyMonths;

    @Size(max = 100, message = "Serial number must be under 100 characters")
    private String serialNumber;

    @Size(max = 500, message = "Invoice URL must be under 500 characters")
    private String invoiceUrl;

    private AssetStatus status;

    public AssetUpdateRequest() {}

    public AssetUpdateRequest(String assetName, AssetCategory category, LocalDate purchaseDate,
                              BigDecimal purchasePrice, Integer warrantyMonths, Integer extendedWarrantyMonths,
                              String serialNumber, String invoiceUrl, AssetStatus status) {
        this.assetName = assetName;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.warrantyMonths = warrantyMonths;
        this.extendedWarrantyMonths = extendedWarrantyMonths;
        this.serialNumber = serialNumber;
        this.invoiceUrl = invoiceUrl;
        this.status = status;
    }

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

    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
}
