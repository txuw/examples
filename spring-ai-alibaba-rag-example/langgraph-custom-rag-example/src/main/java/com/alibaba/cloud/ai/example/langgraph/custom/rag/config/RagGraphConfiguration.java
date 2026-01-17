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
 * @author : txuw
 * @date : 2026/1/17
 */

@Configuration
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
                .addPatternStrategy("answer", new ReplaceStrategy())
                .build();

        // Node
        GenerateQueryNode generateQueryNode = new GenerateQueryNode(chatClientBuilder, knowledgeTool);
        GradeDocumentsNode gradeDocumentsNode = new GradeDocumentsNode(chatClientBuilder);
        RewriteNode rewriteNode = new RewriteNode(chatClientBuilder);
        GenerateAnswerNode generateAnswerNode = new GenerateAnswerNode(chatClientBuilder);

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                // Node
                .addNode(GenerateQueryNode.NAME, node_async(generateQueryNode))
                .addNode(GradeDocumentsNode.NAME,node_async(gradeDocumentsNode))
                .addNode(RewriteNode.NAME,node_async(rewriteNode))
                .addNode(GenerateAnswerNode.NAME,node_async(generateAnswerNode))
                // Edge
                .addEdge(StateGraph.START, GenerateQueryNode.NAME)
                .addEdge(GenerateQueryNode.NAME,GradeDocumentsNode.NAME)
                .addConditionalEdges(GradeDocumentsNode.NAME,
                    edge_async(state -> {
                        return (String) state.value("next_node").orElse(GenerateQueryNode.NAME);
                    }),
                    Map.of(
                            GenerateAnswerNode.NAME, GenerateAnswerNode.NAME,
                            RewriteNode.NAME, RewriteNode.NAME
                    )
                )
                .addEdge(GenerateAnswerNode.NAME,StateGraph.END)
                .addEdge(RewriteNode.NAME,GenerateQueryNode.NAME);

        return stateGraph;
    }
}
