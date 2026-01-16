package com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @author : zhengyuchao
 * @date : 2026/1/16
 */
public class KnowledgeRequest {

    @JsonPropertyDescription("需要查询天气的城市名称，例如：'北京'。请确保传入的是具体的城市名。")
    @JsonProperty(required = true)
    String query;

    public String getQuery() {
        return query;
    }
}
