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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : zhengyuchao
 * @date : 2026/1/17
 */
public class GradeDocumentsNode implements NodeAction {


    private static final Logger logger = LoggerFactory.getLogger(GradeDocumentsNode.class);

    private final ChatClient chatClient;

    public GradeDocumentsNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
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
