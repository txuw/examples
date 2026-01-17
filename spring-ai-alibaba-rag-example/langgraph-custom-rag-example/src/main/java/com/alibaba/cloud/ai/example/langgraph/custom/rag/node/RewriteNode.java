package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengyuchao
 * @date : 2026/1/17
 */
public class RewriteNode implements NodeAction {


    private static final Logger logger = LoggerFactory.getLogger(RewriteNode.class);

    private final ChatClient chatClient;

    public final static String NAME = "RewriteNode";

    public RewriteNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {


        String query = state.value("query", "");
        String systemPrompt = """
                看看输入并尝试推理其潜在的语义意图/含义。
                这是最初的问题
                {query}
                提出一个更完善的问题
                """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
        Message message = systemPromptTemplate.createMessage(Map.of("query", query));

        logger.info("node :"+NAME+" 发起请求:"+query );

        ChatClient.CallResponseSpec callResponseSpec = chatClient.prompt(message.getText())
                .call();
        String id = callResponseSpec.chatResponse().getMetadata().getId();
        String content = callResponseSpec.content();

        logger.info("node :"+NAME+" id: "+id+" 返回值: "+content);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("query", content);

        return resultMap;
    }

}
