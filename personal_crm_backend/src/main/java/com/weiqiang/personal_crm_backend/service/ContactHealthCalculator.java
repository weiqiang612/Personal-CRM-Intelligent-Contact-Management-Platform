package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.enums.RelationshipHealthStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Calculator for evaluating contact relationship health status.
 * Standardizes calculation logic and facilitates future extension (e.g. dynamic threshold based on contact type).
 */
@Component
public class ContactHealthCalculator {

    /**
     * Calculate health status based on contact's latest activity time
     *
     * @param latestActivityTime Most recent activity timestamp, null if no activity logged
     * @return Calculated RelationshipHealthStatus
     */
    public RelationshipHealthStatus calculateStatus(LocalDateTime latestActivityTime) {
        if (latestActivityTime == null) {
            return RelationshipHealthStatus.NO_ACTIVITY;
        }

        LocalDate today = LocalDate.now();
        LocalDate activityDate = latestActivityTime.toLocalDate();

        // Calculate days between activity date and today
        long daysDiff = ChronoUnit.DAYS.between(activityDate, today);
        if (daysDiff < 0) {
            // Future activity or same day edge case -> treat as active (0 days)
            daysDiff = 0;
        }

        if (daysDiff <= 7) {
            return RelationshipHealthStatus.ACTIVE;
        } else if (daysDiff <= 30) {
            return RelationshipHealthStatus.FOLLOW_UP;
        } else {
            return RelationshipHealthStatus.INACTIVE;
        }
    }

    /**
     * Overloaded helper method taking a Contact entity and latest activity time
     */
    public RelationshipHealthStatus calculateStatus(Contact contact, LocalDateTime latestActivityTime) {
        // Future extensions can inspect contact attributes (e.g. contact.getTags(), category, etc.)
        return calculateStatus(latestActivityTime);
    }
}
