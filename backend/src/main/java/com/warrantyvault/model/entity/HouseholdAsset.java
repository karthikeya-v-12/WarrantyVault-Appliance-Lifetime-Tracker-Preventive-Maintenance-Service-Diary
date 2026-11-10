package com.warrantyvault.model.entity;

import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "household_assets")
public class HouseholdAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private AssetCategory category;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "purchase_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name = "warranty_months", nullable = false)
    private Integer warrantyMonths;

    @Column(name = "extended_warranty_months", nullable = false)
    private Integer extendedWarrantyMonths = 0;

    @Column(name = "warranty_expiry_date", nullable = false)
    private LocalDate warrantyExpiryDate;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "invoice_url", length = 500)
    private String invoiceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AssetStatus status = AssetStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceSchedule> maintenanceSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceLog> serviceLogs = new ArrayList<>();

    @Transient
    private Boolean isUnderWarranty;

    @Transient
    private Long daysUntilWarrantyExpiry;

    public HouseholdAsset() {
    }

    public HouseholdAsset(Long id, User user, String assetName, AssetCategory category,
                          LocalDate purchaseDate, BigDecimal purchasePrice, Integer warrantyMonths,
                          Integer extendedWarrantyMonths, LocalDate warrantyExpiryDate,
                          String serialNumber, String invoiceUrl, AssetStatus status,
                          LocalDateTime createdAt, LocalDateTime updatedAt,
                          List<MaintenanceSchedule> maintenanceSchedules,
                          List<ServiceLog> serviceLogs) {
        this.id = id;
        this.user = user;
        this.assetName = assetName;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice != null ? purchasePrice : BigDecimal.ZERO;
        this.warrantyMonths = warrantyMonths;
        this.extendedWarrantyMonths = extendedWarrantyMonths != null ? extendedWarrantyMonths : 0;
        this.warrantyExpiryDate = warrantyExpiryDate;
        this.serialNumber = serialNumber;
        this.invoiceUrl = invoiceUrl;
        this.status = status != null ? status : AssetStatus.ACTIVE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.maintenanceSchedules = maintenanceSchedules != null ? maintenanceSchedules : new ArrayList<>();
        this.serviceLogs = serviceLogs != null ? serviceLogs : new ArrayList<>();
    }

    public static HouseholdAssetBuilder builder() {
        return new HouseholdAssetBuilder();
    }

    /**
     * Compute warranty expiry date based on purchase date and total warranty duration.
     */
    public LocalDate computeCalculatedExpiryDate() {
        if (this.purchaseDate == null || this.warrantyMonths == null) {
            return null;
        }
        int totalMonths = this.warrantyMonths + (this.extendedWarrantyMonths != null ? this.extendedWarrantyMonths : 0);
        return this.purchaseDate.plusMonths(totalMonths);
    }

    /**
     * Updates transient status fields dynamically based on a reference date.
     */
    public void populateTransientWarrantyStatus(LocalDate referenceDate) {
        if (this.warrantyExpiryDate != null && referenceDate != null) {
            this.isUnderWarranty = !referenceDate.isAfter(this.warrantyExpiryDate);
            this.daysUntilWarrantyExpiry = ChronoUnit.DAYS.between(referenceDate, this.warrantyExpiryDate);
        }
    }

    @PrePersist
    @PreUpdate
    protected void ensureWarrantyCalculated() {
        if (this.extendedWarrantyMonths == null) {
            this.extendedWarrantyMonths = 0;
        }
        if (this.status == null) {
            this.status = AssetStatus.ACTIVE;
        }
        if (this.purchaseDate != null && this.warrantyMonths != null) {
            this.warrantyExpiryDate = computeCalculatedExpiryDate();
        }
    }

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getInvoiceUrl() { return invoiceUrl; }
    public void setInvoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; }

    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<MaintenanceSchedule> getMaintenanceSchedules() { return maintenanceSchedules; }
    public void setMaintenanceSchedules(List<MaintenanceSchedule> maintenanceSchedules) {
        this.maintenanceSchedules = (maintenanceSchedules != null) ? maintenanceSchedules : new ArrayList<>();
    }

    public List<ServiceLog> getServiceLogs() { return serviceLogs; }
    public void setServiceLogs(List<ServiceLog> serviceLogs) {
        this.serviceLogs = (serviceLogs != null) ? serviceLogs : new ArrayList<>();
    }

    public Boolean getIsUnderWarranty() { return isUnderWarranty; }
    public void setIsUnderWarranty(Boolean underWarranty) { isUnderWarranty = underWarranty; }

    public Long getDaysUntilWarrantyExpiry() { return daysUntilWarrantyExpiry; }
    public void setDaysUntilWarrantyExpiry(Long daysUntilWarrantyExpiry) { this.daysUntilWarrantyExpiry = daysUntilWarrantyExpiry; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HouseholdAsset that = (HouseholdAsset) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class HouseholdAssetBuilder {
        private Long id;
        private User user;
        private String assetName;
        private AssetCategory category;
        private LocalDate purchaseDate;
        private BigDecimal purchasePrice = BigDecimal.ZERO;
        private Integer warrantyMonths;
        private Integer extendedWarrantyMonths = 0;
        private LocalDate warrantyExpiryDate;
        private String serialNumber;
        private String invoiceUrl;
        private AssetStatus status = AssetStatus.ACTIVE;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<MaintenanceSchedule> maintenanceSchedules = new ArrayList<>();
        private List<ServiceLog> serviceLogs = new ArrayList<>();

        HouseholdAssetBuilder() {}

        public HouseholdAssetBuilder id(Long id) { this.id = id; return this; }
        public HouseholdAssetBuilder user(User user) { this.user = user; return this; }
        public HouseholdAssetBuilder assetName(String assetName) { this.assetName = assetName; return this; }
        public HouseholdAssetBuilder category(AssetCategory category) { this.category = category; return this; }
        public HouseholdAssetBuilder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public HouseholdAssetBuilder purchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; return this; }
        public HouseholdAssetBuilder warrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; return this; }
        public HouseholdAssetBuilder extendedWarrantyMonths(Integer extendedWarrantyMonths) { this.extendedWarrantyMonths = extendedWarrantyMonths; return this; }
        public HouseholdAssetBuilder warrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; return this; }
        public HouseholdAssetBuilder serialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }
        public HouseholdAssetBuilder invoiceUrl(String invoiceUrl) { this.invoiceUrl = invoiceUrl; return this; }
        public HouseholdAssetBuilder status(AssetStatus status) { this.status = status; return this; }
        public HouseholdAssetBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public HouseholdAssetBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public HouseholdAssetBuilder maintenanceSchedules(List<MaintenanceSchedule> schedules) { this.maintenanceSchedules = schedules; return this; }
        public HouseholdAssetBuilder serviceLogs(List<ServiceLog> logs) { this.serviceLogs = logs; return this; }

        public HouseholdAsset build() {
            return new HouseholdAsset(id, user, assetName, category, purchaseDate, purchasePrice,
                    warrantyMonths, extendedWarrantyMonths, warrantyExpiryDate, serialNumber,
                    invoiceUrl, status, createdAt, updatedAt, maintenanceSchedules, serviceLogs);
        }
    }
}
