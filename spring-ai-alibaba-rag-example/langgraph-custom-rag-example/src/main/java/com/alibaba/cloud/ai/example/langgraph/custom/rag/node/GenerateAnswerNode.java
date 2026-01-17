package com.alibaba.cloud.ai.example.langgraph.custom.rag.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengyuchao
 * @date : 2026/1/17
 */
public class GenerateAnswerNode implements NodeAction {

    private static final Logger logger = LoggerFactory.getLogger(GenerateAnswerNode.class);

    private final ChatClient chatClient;

    public final static String NAME = "GenerateAnswerNode";

    public GenerateAnswerNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state)  {

        String systemPrompt = """
                你是负责答疑任务的助手。
                使用以下检索到的上下文来回答问题。
                如果你不知道答案，就说你不知道。
                最多使用三个句子并保持答案简洁。
                问题：{query}
                上下文：{content}
                """;
        String query = state.value("query", "");
        String generateQueryContent = state.value("content", "");
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
        Message message = systemPromptTemplate.createMessage(Map.of("content", generateQueryContent, "query", query));

        logger.info("node :"+NAME+" 发起请求:"+message.getText() );

        ChatClient.CallResponseSpec callResponseSpec = chatClient.prompt(message.getText())
                .call();
        ChatResponse response = callResponseSpec.chatResponse();
        Generation result = response.getResult();
        String id = result.getMetadata().getOrDefault("requestId", "");
        String content = result.getOutput().getText();

        logger.info("node :"+NAME+" id: "+id+" 返回值: "+content);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("answer", content);

        return resultMap;
    }

}
