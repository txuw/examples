/*
 * Copyright 2026-2027 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.example.langgraph.custom.rag.tool;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.service.CloudRagService;
import com.alibaba.cloud.ai.example.langgraph.custom.rag.tool.request.KnowledgeRequest;
import jakarta.annotation.PostConstruct;
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
 * @author : txuw
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

    @PostConstruct
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
        logger.info("=================================  KnowledgeTool 开始 =================================");
        logger.info("KnowledgeTool 请求体: "+knowledgeRequest.toString());
        String query = knowledgeRequest.getQuery();
        List<Document> documents = simpleVectorStore.similaritySearch(query);
        StringBuilder output = new StringBuilder();
        for (Document document : documents) {
            output.append(document.getFormattedContent()).append("\n");
        }

        logger.info("KnowledgeTool 返回值: "+output);
        logger.info("=================================  KnowledgeTool 结束 =================================");
        return output.toString();
    }
}
