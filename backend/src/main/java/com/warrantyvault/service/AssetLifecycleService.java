package com.warrantyvault.service;

import com.warrantyvault.dto.request.AssetRegistrationRequest;
import com.warrantyvault.dto.request.AssetUpdateRequest;
import com.warrantyvault.dto.response.AssetResponse;
import com.warrantyvault.exception.ResourceNotFoundException;
import com.warrantyvault.model.entity.HouseholdAsset;
import com.warrantyvault.model.entity.MaintenanceSchedule;
import com.warrantyvault.model.entity.User;
import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;
import com.warrantyvault.repository.HouseholdAssetRepository;
import com.warrantyvault.repository.MaintenanceScheduleRepository;
import com.warrantyvault.repository.ServiceLogRepository;
import com.warrantyvault.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(AssetLifecycleService.class);

    private final HouseholdAssetRepository assetRepository;
    private final UserRepository userRepository;
    private final ServiceLogRepository serviceLogRepository;
    private final MaintenanceScheduleRepository scheduleRepository;

    public AssetLifecycleService(HouseholdAssetRepository assetRepository,
                                 UserRepository userRepository,
                                 ServiceLogRepository serviceLogRepository,
                                 MaintenanceScheduleRepository scheduleRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Compute warranty expiry date strictly from purchase date and total duration.
     * Formula: purchaseDate.plusMonths(warrantyMonths + extendedWarrantyMonths)
     */
    public LocalDate calculateWarrantyExpiryDate(LocalDate purchaseDate, int warrantyMonths, Integer extendedWarrantyMonths) {
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date cannot be null when calculating warranty expiry");
        }
        int extended = (extendedWarrantyMonths != null) ? extendedWarrantyMonths : 0;
        int totalMonths = warrantyMonths + extended;
        return purchaseDate.plusMonths(totalMonths);
    }

    /**
     * Calculates and injects transient warranty flags: isUnderWarranty and daysUntilWarrantyExpiry.
     */
    public void populateTransientWarrantyStatus(HouseholdAsset asset, LocalDate referenceDate) {
        if (asset == null || asset.getWarrantyExpiryDate() == null) {
            return;
        }
        LocalDate today = (referenceDate != null) ? referenceDate : LocalDate.now();
        boolean underWarranty = !today.isAfter(asset.getWarrantyExpiryDate());
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, asset.getWarrantyExpiryDate());

        asset.setIsUnderWarranty(underWarranty);
        asset.setDaysUntilWarrantyExpiry(daysUntilExpiry);
    }

    /**
     * Registers a new asset with auto-calculated warranty expiry and transient lifetime properties.
     */
    @Transactional
    public AssetResponse registerAsset(AssetRegistrationRequest request) {
        Long targetUserId = request.getUserId() != null ? request.getUserId() : 1L;
        User user = userRepository.findById(targetUserId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name("Primary User")
                            .email("user" + targetUserId + "@warrantyvault.local")
                            .build();
                    return userRepository.save(newUser);
                });

        int extendedMonths = request.getExtendedWarrantyMonths() != null ? request.getExtendedWarrantyMonths() : 0;
        LocalDate expiryDate = calculateWarrantyExpiryDate(
                request.getPurchaseDate(),
                request.getWarrantyMonths(),
                extendedMonths
        );

        BigDecimal price = request.getPurchasePrice() != null ? request.getPurchasePrice() : BigDecimal.ZERO;

        HouseholdAsset asset = HouseholdAsset.builder()
                .user(user)
                .assetName(request.getAssetName().trim())
                .category(request.getCategory())
                .purchaseDate(request.getPurchaseDate())
                .purchasePrice(price)
                .warrantyMonths(request.getWarrantyMonths())
                .extendedWarrantyMonths(extendedMonths)
                .warrantyExpiryDate(expiryDate)
                .serialNumber(request.getSerialNumber() != null ? request.getSerialNumber().trim() : null)
                .invoiceUrl(request.getInvoiceUrl() != null ? request.getInvoiceUrl().trim() : null)
                .status(AssetStatus.ACTIVE)
                .build();

        HouseholdAsset savedAsset = assetRepository.save(asset);

        // Provision initial maintenance tasks if submitted
        if (request.getInitialMaintenanceTasks() != null && !request.getInitialMaintenanceTasks().isEmpty()) {
            for (AssetRegistrationRequest.InitialMaintenanceTask taskReq : request.getInitialMaintenanceTasks()) {
                MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                        .asset(savedAsset)
                        .taskName(taskReq.getTaskName().trim())
                        .intervalMonths(taskReq.getIntervalMonths())
                        .lastServicedDate(savedAsset.getPurchaseDate())
                        .nextDueDate(savedAsset.getPurchaseDate().plusMonths(taskReq.getIntervalMonths()))
                        .build();
                scheduleRepository.save(schedule);
                savedAsset.getMaintenanceSchedules().add(schedule);
            }
        }

        log.info("Registered new asset [ID: {} - '{}'], Expiry Date calculated: {}",
                savedAsset.getId(), savedAsset.getAssetName(), savedAsset.getWarrantyExpiryDate());

        populateTransientWarrantyStatus(savedAsset, LocalDate.now());
        return AssetResponse.fromEntity(savedAsset, LocalDate.now(), BigDecimal.ZERO);
    }

    /**
     * Lists assets for a user with category and status filters, including transient calculations.
     */
    @Transactional(readOnly = true)
    public List<AssetResponse> getAssets(Long userId, AssetCategory category, AssetStatus status) {
        Long targetUserId = userId != null ? userId : 1L;
        LocalDate today = LocalDate.now();

        List<HouseholdAsset> assets = assetRepository.findFilteredAssets(targetUserId, category, status);

        return assets.stream().map(asset -> {
            BigDecimal serviceCost = serviceLogRepository.sumCostByAssetId(asset.getId());
            return AssetResponse.fromEntity(asset, today, serviceCost);
        }).collect(Collectors.toList());
    }

    /**
     * Fetches a single asset with full lifetime overview, TCO, and transient attributes.
     */
    @Transactional(readOnly = true)
    public AssetResponse getAssetSummary(Long assetId) {
        HouseholdAsset asset = assetRepository.findByIdWithDetails(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));

        LocalDate today = LocalDate.now();
        BigDecimal serviceCost = serviceLogRepository.sumCostByAssetId(assetId);

        return AssetResponse.fromEntity(asset, today, serviceCost);
    }

    /**
     * Updates asset details and recalculates warranty expiry if relevant date/duration fields changed.
     */
    @Transactional
    public AssetResponse updateAsset(Long assetId, AssetUpdateRequest request) {
        HouseholdAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));

        if (request.getAssetName() != null && !request.getAssetName().isBlank()) {
            asset.setAssetName(request.getAssetName().trim());
        }
        if (request.getCategory() != null) {
            asset.setCategory(request.getCategory());
        }
        if (request.getPurchasePrice() != null) {
            asset.setPurchasePrice(request.getPurchasePrice());
        }
        if (request.getSerialNumber() != null) {
            asset.setSerialNumber(request.getSerialNumber().trim());
        }
        if (request.getInvoiceUrl() != null) {
            asset.setInvoiceUrl(request.getInvoiceUrl().trim());
        }
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }

        // Recalculate warranty expiration if purchase date or warranty duration changed
        boolean warrantyRecalcRequired = false;
        if (request.getPurchaseDate() != null && !request.getPurchaseDate().equals(asset.getPurchaseDate())) {
            asset.setPurchaseDate(request.getPurchaseDate());
            warrantyRecalcRequired = true;
        }
        if (request.getWarrantyMonths() != null && !request.getWarrantyMonths().equals(asset.getWarrantyMonths())) {
            asset.setWarrantyMonths(request.getWarrantyMonths());
            warrantyRecalcRequired = true;
        }
        if (request.getExtendedWarrantyMonths() != null && !request.getExtendedWarrantyMonths().equals(asset.getExtendedWarrantyMonths())) {
            asset.setExtendedWarrantyMonths(request.getExtendedWarrantyMonths());
            warrantyRecalcRequired = true;
        }

        if (warrantyRecalcRequired) {
            LocalDate recalculatedExpiry = calculateWarrantyExpiryDate(
                    asset.getPurchaseDate(),
                    asset.getWarrantyMonths(),
                    asset.getExtendedWarrantyMonths()
            );
            asset.setWarrantyExpiryDate(recalculatedExpiry);
            log.info("Recalculated warranty expiry for asset ID {} to {}", assetId, recalculatedExpiry);
        }

        HouseholdAsset updated = assetRepository.save(asset);
        LocalDate today = LocalDate.now();
        BigDecimal serviceCost = serviceLogRepository.sumCostByAssetId(assetId);

        return AssetResponse.fromEntity(updated, today, serviceCost);
    }

    /**
     * Retires an asset from active duty.
     */
    @Transactional
    public AssetResponse retireAsset(Long assetId) {
        HouseholdAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));

        asset.setStatus(AssetStatus.RETIRED);
        HouseholdAsset updated = assetRepository.save(asset);

        LocalDate today = LocalDate.now();
        BigDecimal serviceCost = serviceLogRepository.sumCostByAssetId(assetId);
        return AssetResponse.fromEntity(updated, today, serviceCost);
    }
}
