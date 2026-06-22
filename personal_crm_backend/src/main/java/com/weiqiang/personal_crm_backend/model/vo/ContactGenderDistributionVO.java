package com.weiqiang.personal_crm_backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contact Gender Distribution VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactGenderDistributionVO {
    private Integer gender;
    private String name;
    private Long count;
}
