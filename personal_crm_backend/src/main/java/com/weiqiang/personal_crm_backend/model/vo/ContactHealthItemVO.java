package com.weiqiang.personal_crm_backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VO representing contact summary item in relationship health statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactHealthItemVO {
    private String contactId;
    private String name;
    private String avatarUrl;
    private Long daysAgo;
    private String lastEventTitle;
}
