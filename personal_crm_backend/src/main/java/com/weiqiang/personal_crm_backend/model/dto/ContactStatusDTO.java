package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 联系人状态变更请求对象
 */
@Data
public class ContactStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人状态：0 正常，1 黑名单
     */
    @NotNull(message = "status cannot be null")
    private Integer status;
}
