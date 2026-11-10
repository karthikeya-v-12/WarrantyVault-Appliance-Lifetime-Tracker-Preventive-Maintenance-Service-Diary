package com.warrantyvault.repository;

import com.warrantyvault.model.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    List<MaintenanceSchedule> findByAssetIdOrderByNextDueDateAsc(Long assetId);

    Optional<MaintenanceSchedule> findByAssetIdAndTaskNameIgnoreCase(Long assetId, String taskName);

    @Query("SELECT m FROM MaintenanceSchedule m JOIN FETCH m.asset a " +
           "WHERE a.status = 'ACTIVE' AND m.nextDueDate <= :cutoffDate " +
           "ORDER BY m.nextDueDate ASC")
    List<MaintenanceSchedule> findPendingAndUpcomingSchedules(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT m FROM MaintenanceSchedule m JOIN FETCH m.asset a " +
           "WHERE a.user.id = :userId AND a.status = 'ACTIVE' AND m.nextDueDate <= :cutoffDate " +
           "ORDER BY m.nextDueDate ASC")
    List<MaintenanceSchedule> findUserPendingAndUpcomingSchedules(
            @Param("userId") Long userId,
            @Param("cutoffDate") LocalDate cutoffDate
    );

    @Query("SELECT m FROM MaintenanceSchedule m JOIN FETCH m.asset a " +
           "WHERE a.user.id = :userId AND a.status = 'ACTIVE' " +
           "AND m.nextDueDate >= :startDate AND m.nextDueDate <= :endDate " +
           "ORDER BY m.nextDueDate ASC")
    List<MaintenanceSchedule> findUserSchedulesInRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
