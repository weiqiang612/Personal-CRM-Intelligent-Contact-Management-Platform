package com.weiqiang.personal_crm_backend.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新增事项请求对象
 */
@Data
public class TodoCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人业务编号 (必填)
     */
    @NotBlank(message = "contactId cannot be blank")
    private String contactId;

    /**
     * 提醒时间 (必填)
     */
    @NotNull(message = "todoTime cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime todoTime;

    /**
     * 事项内容 (必填, 长度限制)
     */
    @NotBlank(message = "content cannot be blank")
    @Size(max = 500, message = "content cannot exceed 500 characters")
    private String content;

    /**
     * 优先级 (0 普通, 1 重要, 2 紧急) (必填)
     */
    @NotNull(message = "priority cannot be null")
    @Min(value = 0, message = "priority must be between 0 and 2")
    @Max(value = 2, message = "priority must be between 0 and 2")
    private Integer priority;
}
