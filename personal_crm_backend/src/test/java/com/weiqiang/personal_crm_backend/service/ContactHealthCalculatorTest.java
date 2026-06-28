package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.enums.RelationshipHealthStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for ContactHealthCalculator
 */
public class ContactHealthCalculatorTest {

    private final ContactHealthCalculator calculator = new ContactHealthCalculator();

    @Test
    void testCalculateStatus_NoActivity() {
        RelationshipHealthStatus status = calculator.calculateStatus(null);
        assertEquals(RelationshipHealthStatus.NO_ACTIVITY, status);
    }

    @Test
    void testCalculateStatus_Active() {
        // Today or within 7 days
        RelationshipHealthStatus statusToday = calculator.calculateStatus(LocalDateTime.now());
        assertEquals(RelationshipHealthStatus.ACTIVE, statusToday);

        RelationshipHealthStatus status7Days = calculator.calculateStatus(LocalDateTime.now().minusDays(7));
        assertEquals(RelationshipHealthStatus.ACTIVE, status7Days);
    }

    @Test
    void testCalculateStatus_FollowUp() {
        // 8 to 30 days
        RelationshipHealthStatus status8Days = calculator.calculateStatus(LocalDateTime.now().minusDays(8));
        assertEquals(RelationshipHealthStatus.FOLLOW_UP, status8Days);

        RelationshipHealthStatus status30Days = calculator.calculateStatus(LocalDateTime.now().minusDays(30));
        assertEquals(RelationshipHealthStatus.FOLLOW_UP, status30Days);
    }

    @Test
    void testCalculateStatus_Inactive() {
        // More than 30 days
        RelationshipHealthStatus status31Days = calculator.calculateStatus(LocalDateTime.now().minusDays(31));
        assertEquals(RelationshipHealthStatus.INACTIVE, status31Days);

        RelationshipHealthStatus status60Days = calculator.calculateStatus(LocalDateTime.now().minusDays(60));
        assertEquals(RelationshipHealthStatus.INACTIVE, status60Days);
    }
}
