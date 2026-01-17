package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.KnowledgeTool;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request.KnowledgeRequest;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    public final static String NAME = "GenerateQueryNode";

    public GenerateQueryNode(ChatClient.Builder chatClientBuilder, KnowledgeTool knowledgeTool) {

        ToolCallback knowledgeToolCallback = FunctionToolCallback.builder("get_alibaba_knowledge", knowledgeTool)
                .description("用于查询spring ai alibaba教程知识库")
                .inputType(KnowledgeRequest.class)
                .build();
        this.chatClient = chatClientBuilder.build().mutate()
                .defaultToolCallbacks(knowledgeToolCallback)
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String query = state.value("query", "");

        logger.info("node :"+NAME+" 发起请求:"+query );

        ChatClient.CallResponseSpec callResponseSpec = chatClient.prompt(query)
                .call();
        String id = callResponseSpec.chatResponse().getMetadata().getId();
        String content = callResponseSpec.content();

        logger.info("node :"+NAME+" id: "+id+" 返回值: "+content);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", content);

        return resultMap;
    }
}
