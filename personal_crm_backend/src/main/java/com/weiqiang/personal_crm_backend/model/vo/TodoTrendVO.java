package com.weiqiang.personal_crm_backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard Todo Trend VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoTrendVO {
    private String date;
    private Long count;
}
