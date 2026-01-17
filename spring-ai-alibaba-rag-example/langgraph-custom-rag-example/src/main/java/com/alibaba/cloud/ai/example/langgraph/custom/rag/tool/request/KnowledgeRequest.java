package com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @author : zhengyuchao
 * @date : 2026/1/16
 */
public class KnowledgeRequest {

    @JsonPropertyDescription("用户的搜索文本")
    @JsonProperty(required = true)
    String query;

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "KnowledgeRequest{" +
                "query='" + query + '\'' +
                '}';
    }
}
