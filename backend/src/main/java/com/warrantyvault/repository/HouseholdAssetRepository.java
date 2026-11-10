package com.warrantyvault.repository;

import com.warrantyvault.model.entity.HouseholdAsset;
import com.warrantyvault.model.enums.AssetCategory;
import com.warrantyvault.model.enums.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdAssetRepository extends JpaRepository<HouseholdAsset, Long> {

    List<HouseholdAsset> findByUserId(Long userId);

    @Query("SELECT a FROM HouseholdAsset a " +
           "WHERE a.user.id = :userId " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.createdAt DESC")
    List<HouseholdAsset> findFilteredAssets(
            @Param("userId") Long userId,
            @Param("category") AssetCategory category,
            @Param("status") AssetStatus status
    );

    @Query("SELECT a FROM HouseholdAsset a " +
           "WHERE a.status = 'ACTIVE' " +
           "AND a.warrantyExpiryDate >= :startDate AND a.warrantyExpiryDate <= :endDate " +
           "ORDER BY a.warrantyExpiryDate ASC")
    List<HouseholdAsset> findActiveExpiringWarranties(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a FROM HouseholdAsset a " +
           "WHERE a.user.id = :userId AND a.status = 'ACTIVE' " +
           "AND a.warrantyExpiryDate >= :startDate AND a.warrantyExpiryDate <= :endDate " +
           "ORDER BY a.warrantyExpiryDate ASC")
    List<HouseholdAsset> findUserActiveExpiringWarranties(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a FROM HouseholdAsset a LEFT JOIN FETCH a.maintenanceSchedules LEFT JOIN FETCH a.serviceLogs WHERE a.id = :id")
    Optional<HouseholdAsset> findByIdWithDetails(@Param("id") Long id);
}
