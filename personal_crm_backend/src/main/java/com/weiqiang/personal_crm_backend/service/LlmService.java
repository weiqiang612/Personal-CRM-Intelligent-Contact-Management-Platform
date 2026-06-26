package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.model.dto.OpenAiMessage;

import java.util.List;

/**
 * 大语言模型接口适配服务
 */
public interface LlmService {

    /**
     * 与大语言模型对话，并返回识别结果
     * @param messages 历史对话及系统 Prompt 列表
     * @return 模型返回的原始文本 (预期为 JSON 格式)
     */
    String chat(List<OpenAiMessage> messages);
}
