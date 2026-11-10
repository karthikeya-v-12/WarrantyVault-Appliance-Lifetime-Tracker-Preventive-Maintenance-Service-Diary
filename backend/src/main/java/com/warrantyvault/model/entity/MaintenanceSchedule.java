package com.warrantyvault.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private HouseholdAsset asset;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "interval_months", nullable = false)
    private Integer intervalMonths;

    @Column(name = "last_serviced_date")
    private LocalDate lastServicedDate;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    private Boolean isOverdue;

    @Transient
    private Long daysUntilDue;

    public MaintenanceSchedule() {
    }

    public MaintenanceSchedule(Long id, HouseholdAsset asset, String taskName, Integer intervalMonths,
                               LocalDate lastServicedDate, LocalDate nextDueDate,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.asset = asset;
        this.taskName = taskName;
        this.intervalMonths = intervalMonths;
        this.lastServicedDate = lastServicedDate;
        this.nextDueDate = nextDueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MaintenanceScheduleBuilder builder() {
        return new MaintenanceScheduleBuilder();
    }

    public void updateServiceCompletion(LocalDate serviceDate) {
        this.lastServicedDate = serviceDate;
        if (this.intervalMonths != null && this.intervalMonths > 0) {
            this.nextDueDate = serviceDate.plusMonths(this.intervalMonths);
        }
    }

    public void populateTransientScheduleStatus(LocalDate referenceDate) {
        if (this.nextDueDate != null && referenceDate != null) {
            this.isOverdue = referenceDate.isAfter(this.nextDueDate);
            this.daysUntilDue = ChronoUnit.DAYS.between(referenceDate, this.nextDueDate);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HouseholdAsset getAsset() { return asset; }
    public void setAsset(HouseholdAsset asset) { this.asset = asset; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Integer getIntervalMonths() { return intervalMonths; }
    public void setIntervalMonths(Integer intervalMonths) { this.intervalMonths = intervalMonths; }

    public LocalDate getLastServicedDate() { return lastServicedDate; }
    public void setLastServicedDate(LocalDate lastServicedDate) { this.lastServicedDate = lastServicedDate; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean overdue) { isOverdue = overdue; }

    public Long getDaysUntilDue() { return daysUntilDue; }
    public void setDaysUntilDue(Long daysUntilDue) { this.daysUntilDue = daysUntilDue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceSchedule that = (MaintenanceSchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class MaintenanceScheduleBuilder {
        private Long id;
        private HouseholdAsset asset;
        private String taskName;
        private Integer intervalMonths;
        private LocalDate lastServicedDate;
        private LocalDate nextDueDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        MaintenanceScheduleBuilder() {}

        public MaintenanceScheduleBuilder id(Long id) { this.id = id; return this; }
        public MaintenanceScheduleBuilder asset(HouseholdAsset asset) { this.asset = asset; return this; }
        public MaintenanceScheduleBuilder taskName(String taskName) { this.taskName = taskName; return this; }
        public MaintenanceScheduleBuilder intervalMonths(Integer intervalMonths) { this.intervalMonths = intervalMonths; return this; }
        public MaintenanceScheduleBuilder lastServicedDate(LocalDate lastServicedDate) { this.lastServicedDate = lastServicedDate; return this; }
        public MaintenanceScheduleBuilder nextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; return this; }
        public MaintenanceScheduleBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public MaintenanceScheduleBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public MaintenanceSchedule build() {
            return new MaintenanceSchedule(id, asset, taskName, intervalMonths, lastServicedDate, nextDueDate, createdAt, updatedAt);
        }
    }
}
