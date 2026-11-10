package com.warrantyvault.repository;

import com.warrantyvault.model.entity.ServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {

    List<ServiceLog> findByAssetIdOrderByServiceDateDesc(Long assetId);

    @Query("SELECT COALESCE(SUM(s.cost), 0) FROM ServiceLog s WHERE s.asset.id = :assetId")
    BigDecimal sumCostByAssetId(@Param("assetId") Long assetId);

    @Query("SELECT s FROM ServiceLog s JOIN FETCH s.asset a WHERE a.user.id = :userId ORDER BY s.serviceDate DESC")
    List<ServiceLog> findRecentServicesByUserId(@Param("userId") Long userId);
}
