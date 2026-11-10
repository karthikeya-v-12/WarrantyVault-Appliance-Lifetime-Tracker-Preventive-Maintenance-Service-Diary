package com.warrantyvault.service;

import com.warrantyvault.dto.request.AssetRegistrationRequest;
import com.warrantyvault.dto.response.AssetResponse;
import com.warrantyvault.model.entity.HouseholdAsset;
import com.warrantyvault.model.entity.User;
import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;
import com.warrantyvault.repository.HouseholdAssetRepository;
import com.warrantyvault.repository.MaintenanceScheduleRepository;
import com.warrantyvault.repository.ServiceLogRepository;
import com.warrantyvault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetLifecycleServiceTest {

    @Mock
    private HouseholdAssetRepository assetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceLogRepository serviceLogRepository;

    @Mock
    private MaintenanceScheduleRepository scheduleRepository;

    @InjectMocks
    private AssetLifecycleService lifecycleService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Alex Morgan")
                .email("alex.morgan@example.com")
                .build();
    }

    @Test
    @DisplayName("calculateWarrantyExpiryDate should correctly add warranty and extended warranty months")
    void shouldCalculateWarrantyExpiryDateCorrectly() {
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);
        int warrantyMonths = 12;
        int extendedMonths = 6;

        LocalDate expiryDate = lifecycleService.calculateWarrantyExpiryDate(purchaseDate, warrantyMonths, extendedMonths);

        assertEquals(LocalDate.of(2025, 7, 15), expiryDate);
    }

    @Test
    @DisplayName("populateTransientWarrantyStatus should accurately flag isUnderWarranty and calculate days left")
    void shouldPopulateTransientWarrantyStatusCorrectly() {
        LocalDate today = LocalDate.of(2025, 1, 1);

        HouseholdAsset activeAsset = HouseholdAsset.builder()
                .warrantyExpiryDate(LocalDate.of(2025, 1, 21))
                .build();
        lifecycleService.populateTransientWarrantyStatus(activeAsset, today);
        assertTrue(activeAsset.getIsUnderWarranty());
        assertEquals(20L, activeAsset.getDaysUntilWarrantyExpiry());

        HouseholdAsset expiredAsset = HouseholdAsset.builder()
                .warrantyExpiryDate(LocalDate.of(2024, 12, 1))
                .build();
        lifecycleService.populateTransientWarrantyStatus(expiredAsset, today);
        assertFalse(expiredAsset.getIsUnderWarranty());
        assertEquals(-31L, expiredAsset.getDaysUntilWarrantyExpiry());
    }

    @Test
    @DisplayName("registerAsset should auto-compute warranty expiry and persist asset")
    void shouldRegisterAssetWithAutoCalculatedExpiry() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        AssetRegistrationRequest request = new AssetRegistrationRequest();
        request.setUserId(1L);
        request.setAssetName("MacBook Pro 16");
        request.setCategory(AssetCategory.ELECTRONICS);
        request.setPurchaseDate(LocalDate.of(2024, 5, 10));
        request.setPurchasePrice(BigDecimal.valueOf(2499.00));
        request.setWarrantyMonths(12);
        request.setExtendedWarrantyMonths(12);
        request.setSerialNumber("C02ABCDEF123");

        when(assetRepository.save(any(HouseholdAsset.class))).thenAnswer(invocation -> {
            HouseholdAsset assetToSave = invocation.getArgument(0);
            assetToSave.setId(100L);
            return assetToSave;
        });

        AssetResponse response = lifecycleService.registerAsset(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("MacBook Pro 16", response.getAssetName());
        assertEquals(AssetCategory.ELECTRONICS, response.getCategory());
        assertEquals(24, response.getTotalWarrantyMonths());
        assertEquals(LocalDate.of(2026, 5, 10), response.getWarrantyExpiryDate());
        assertEquals(AssetStatus.ACTIVE, response.getStatus());

        verify(assetRepository, times(1)).save(any(HouseholdAsset.class));
    }
}
