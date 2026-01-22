package com.alibaba.cloud.ai.example.evaluation.hook;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import com.alibaba.cloud.ai.graph.state.RemoveByHash;
import org.springframework.ai.chat.messages.Message;

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
        Optional<Object> messagesOpt = state.value("reviser_agent_output");
        if (!messagesOpt.isPresent()) {
            return CompletableFuture.completedFuture(Map.of());
        }

        List<Message> messages = (List<Message>) messagesOpt.get();

        // 构建新的消息列表，保持原顺序
        List<Object> newMessages = new ArrayList<>();
        for (Message msg : messages) {
            // 根据条件决定保留或删除
            if (msg.getText().contains(_END_OF_EDIT_MARK)) {
                // 标记删除
                newMessages.add(RemoveByHash.of(msg));
            }else {
                newMessages.add(msg);
            }
        }

        return CompletableFuture.completedFuture(Map.of("reviser_agent_output", newMessages));
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        return CompletableFuture.completedFuture(Map.of());
    }
}
