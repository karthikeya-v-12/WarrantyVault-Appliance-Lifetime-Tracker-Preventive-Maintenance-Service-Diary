package com.warrantyvault.dto.response;

import com.warrantyvault.model.entity.MaintenanceSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaintenanceScheduleResponse {
    private Long id;
    private Long assetId;
    private String assetName;
    private String taskName;
    private Integer intervalMonths;
    private LocalDate lastServicedDate;
    private LocalDate nextDueDate;
    private Boolean isOverdue;
    private Long daysUntilDue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MaintenanceScheduleResponse() {}

    public MaintenanceScheduleResponse(Long id, Long assetId, String assetName, String taskName,
                                       Integer intervalMonths, LocalDate lastServicedDate,
                                       LocalDate nextDueDate, Boolean isOverdue, Long daysUntilDue,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.assetId = assetId;
        this.assetName = assetName;
        this.taskName = taskName;
        this.intervalMonths = intervalMonths;
        this.lastServicedDate = lastServicedDate;
        this.nextDueDate = nextDueDate;
        this.isOverdue = isOverdue;
        this.daysUntilDue = daysUntilDue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MaintenanceScheduleResponse fromEntity(MaintenanceSchedule schedule, LocalDate referenceDate) {
        if (schedule == null) return null;
        schedule.populateTransientScheduleStatus(referenceDate);

        MaintenanceScheduleResponse response = new MaintenanceScheduleResponse();
        response.setId(schedule.getId());
        response.setAssetId(schedule.getAsset() != null ? schedule.getAsset().getId() : null);
        response.setAssetName(schedule.getAsset() != null ? schedule.getAsset().getAssetName() : null);
        response.setTaskName(schedule.getTaskName());
        response.setIntervalMonths(schedule.getIntervalMonths());
        response.setLastServicedDate(schedule.getLastServicedDate());
        response.setNextDueDate(schedule.getNextDueDate());
        response.setIsOverdue(schedule.getIsOverdue());
        response.setDaysUntilDue(schedule.getDaysUntilDue());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Integer getIntervalMonths() { return intervalMonths; }
    public void setIntervalMonths(Integer intervalMonths) { this.intervalMonths = intervalMonths; }

    public LocalDate getLastServicedDate() { return lastServicedDate; }
    public void setLastServicedDate(LocalDate lastServicedDate) { this.lastServicedDate = lastServicedDate; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean overdue) { isOverdue = overdue; }

    public Long getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(Long daysUntilDue) { this.daysUntilDue = daysUntilDue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
