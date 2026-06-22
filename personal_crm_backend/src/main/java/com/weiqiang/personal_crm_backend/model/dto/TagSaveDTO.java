package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签保存/修改请求对象
 */
@Data
public class TagSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @NotBlank(message = "tag name cannot be blank")
    @Size(min = 1, max = 50, message = "tag name length must be between 1 and 50 characters")
    private String name;

    /**
     * 标签颜色
     */
    @Size(max = 20, message = "tag color length must be within 20 characters")
    private String color;
}
