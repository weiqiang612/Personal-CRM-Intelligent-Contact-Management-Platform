package com.weiqiang.personal_crm_backend.enums;

import lombok.Getter;

/**
 * Contact Relationship Health Status Enum
 */
@Getter
public enum RelationshipHealthStatus {
    ACTIVE("活跃", 0, 7),
    FOLLOW_UP("待跟进", 8, 30),
    INACTIVE("长期未联系", 31, Integer.MAX_VALUE),
    NO_ACTIVITY("无动态", -1, -1);

    private final String label;
    private final int minDays;
    private final int maxDays;

    RelationshipHealthStatus(String label, int minDays, int maxDays) {
        this.label = label;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }
}
