package com.alibaba.cloud.ai.example.langgraph.custom.rag.config;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.node.GenerateAnswerNode;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.node.GenerateQueryNode;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.node.GradeDocumentsNode;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.node.RewriteNode;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.KnowledgeTool;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.KeyStrategyFactoryBuilder;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * @author : zhengyuchao
 * @date : 2026/1/17
 */

@Configuration
@EnableConfigurationProperties({ RagGraphConfiguration.class })
public class RagGraphConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RagGraphConfiguration.class);

    private final KnowledgeTool knowledgeTool;

    public RagGraphConfiguration(KnowledgeTool knowledgeTool) {
        this.knowledgeTool = knowledgeTool;
    }

    @Bean
    public StateGraph ragGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
                .addPatternStrategy("query", new ReplaceStrategy())
                .addPatternStrategy("content", new ReplaceStrategy())
                .build();

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode("GenerateQueryNode", node_async(new GenerateQueryNode(chatClientBuilder, knowledgeTool)))
                .addNode("GradeDocumentsNode",node_async(new GradeDocumentsNode(chatClientBuilder)))
                .addNode("RewriteNode",node_async(new RewriteNode(chatClientBuilder)))
                .addNode("GenerateAnswerNode",node_async(new GenerateAnswerNode(chatClientBuilder)))
                .addEdge(StateGraph.START, "GenerateQueryNode")
                .addEdge("GenerateQueryNode","GradeDocumentsNode")
                .addConditionalEdges("GradeDocumentsNode",
                    edge_async(state -> {
                        return (String) state.value("next_node").orElse(StateGraph.END);
                    }),
                    Map.of(
                            "query", "query",
                            "content", "content"
                    )
                )
                .addEdge("GenerateAnswerNode",StateGraph.END)
                .addEdge("RewriteNode","GenerateQueryNode");

        return stateGraph;
    }
}
