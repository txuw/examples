package com.alibaba.cloud.ai.example.langgraph.custom.rag.tool;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.service.CloudRagService;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request.KnowledgeRequest;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author : zhengyuchao
 * @date : 2026/1/16
 */
@Component
public class KnowledgeTool implements BiFunction<KnowledgeRequest, ToolContext, String> {

    private static final Logger logger = LoggerFactory.getLogger(CloudRagService.class);


    private final List<String> urls = List.of(
            "https://java2ai.com/docs/quick-start"
    );

    private final SimpleVectorStore simpleVectorStore;


    public KnowledgeTool(EmbeddingModel embeddingModel) {
        this.simpleVectorStore = SimpleVectorStore
                .builder(embeddingModel).build();
    }

    @PreDestroy
    void init(){
        // 1. parse document
        for (String url : urls) {

            // 2. 创建 JsoupDocumentReader
            JsoupDocumentReader reader = new JsoupDocumentReader(url);

            // 3. 读取并转换为 Document 列表
            List<Document> documents = reader.get();

            logger.info("{} documents loaded", documents.size());

            // 2. split trunks
            List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
            logger.info("{} documents split", splitDocuments.size());

            simpleVectorStore.add(splitDocuments);
            logger.info("{} documents added to dashscope cloud vector store", splitDocuments.size());
        }
    }

    @Override
    public String apply(KnowledgeRequest knowledgeRequest, ToolContext toolContext) {
        String query = knowledgeRequest.getQuery();
        List<Document> documents = simpleVectorStore.similaritySearch(query);
        StringBuilder output = new StringBuilder();
        for (Document document : documents) {
            output.append(document.getFormattedContent()).append("\n");
        }
        return output.toString();
    }
}
