package com.alibaba.cloud.ai.example.evaluation.hook;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import com.alibaba.cloud.ai.graph.state.RemoveByHash;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.content.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author : zhengyuchao
 * @date : 2026/1/22
 */
public class ReviserAgentHook  extends ModelHook {


    private static final String _END_OF_EDIT_MARK = "---END-OF-EDIT---";


    @Override
    public String getName() {
        return "";
    }

    @Override
    public CompletableFuture<Map<String, Object>> beforeModel(OverAllState state, RunnableConfig config) {
        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        Optional<Object> messagesOpt = state.value("reviser_agent_output");
        if (!messagesOpt.isPresent()) {
            return CompletableFuture.completedFuture(Map.of());
        }
        if(messagesOpt.get() instanceof AssistantMessage){
            AssistantMessage message = (AssistantMessage) messagesOpt.get();
            // 构建新的消息列表，保持原顺序
            String text = message.getText();
            String newMessage = "";
            if(text.contains(_END_OF_EDIT_MARK)){
                newMessage = text.replace(_END_OF_EDIT_MARK,"");
            }
            AssistantMessage newAssistantMessage = AssistantMessage.builder()
                    .content(newMessage)
                    .media(message.getMedia())
                    .properties(message.getMetadata())
                    .toolCalls(message.getToolCalls())
                    .build();

            return CompletableFuture.completedFuture(Map.of("reviser_agent_output", newAssistantMessage));
        }
        return CompletableFuture.completedFuture(Map.of());
    }
}
