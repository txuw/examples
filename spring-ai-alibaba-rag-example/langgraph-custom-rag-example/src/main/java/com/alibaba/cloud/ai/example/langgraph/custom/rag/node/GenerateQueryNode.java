package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.KnowledgeTool;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request.KnowledgeRequest;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengyuchao
 * @date : 2026/1/16
 */
public class GenerateQueryNode implements NodeAction {

    private static final Logger logger = LoggerFactory.getLogger(GenerateQueryNode.class);

    private final ChatClient chatClient;

    public GenerateQueryNode(ChatClient.Builder chatClientBuilder, KnowledgeTool knowledgeTool) {

        ToolCallback knowledgeToolCallback = FunctionToolCallback.builder("get_alibaba_knowledge", knowledgeTool)
                .description("用于查询spring ai alibaba教程知识库")
                .inputType(KnowledgeRequest.class)
                .build();

        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(knowledgeToolCallback)
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        String query = state.value("query", "");
        String content = chatClient.prompt(query).call().content();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("generate_query_content", content);

        return resultMap;
    }
}
