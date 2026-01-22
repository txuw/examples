package com.alibaba.cloud.ai.example.evaluation.hook;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.JumpTo;
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
public class CriticAgentHook  extends ModelHook {

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


        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        return CompletableFuture.completedFuture(Map.of());
    }
}
