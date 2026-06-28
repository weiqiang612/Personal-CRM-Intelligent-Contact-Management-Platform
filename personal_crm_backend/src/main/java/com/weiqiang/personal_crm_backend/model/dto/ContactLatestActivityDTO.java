package com.weiqiang.personal_crm_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for mapping contact's latest activity timestamp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactLatestActivityDTO {
    private String contactId;
    private LocalDateTime latestActivityTime;
    private String lastEventTitle;

    public ContactLatestActivityDTO(String contactId, LocalDateTime latestActivityTime) {
        this.contactId = contactId;
        this.latestActivityTime = latestActivityTime;
    }
}
