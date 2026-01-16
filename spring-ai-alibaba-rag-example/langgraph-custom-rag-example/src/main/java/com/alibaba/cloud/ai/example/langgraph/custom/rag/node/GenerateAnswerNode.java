package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengyuchao
 * @date : 2026/1/17
 */
public class GenerateAnswerNode implements NodeAction {


    private static final Logger logger = LoggerFactory.getLogger(GenerateAnswerNode.class);

    private final ChatClient chatClient;

    public GenerateAnswerNode(ChatClient.Builder chatClientBuilder) {
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
