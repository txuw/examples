/*
* Copyright 2024 the original author or authors.
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

package com.alibaba.cloud.ai.example.langgraph.custom.rag.service;

import com.alibaba.cloud.ai.advisor.DocumentRetrievalAdvisor;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Title Cloud rag service.<br>
 * Description Cloud rag service.<br>
 *
 * @author yuanci.ytb
 * @since 1.0.0-M2
 */

@Service()
public class CloudRagService implements RagService {

	private static final Logger logger = LoggerFactory.getLogger(CloudRagService.class);

	private final List<String> urls = List.of(
			"https://java2ai.com/docs/quick-start"
	);

	private final SimpleVectorStore simpleVectorStore;

	private final ChatClient chatClient;


	public CloudRagService(ChatClient.Builder builder, EmbeddingModel embeddingModel) {

		this.chatClient = builder.build();
		this.simpleVectorStore = SimpleVectorStore
				.builder(embeddingModel).build();
	}

	@Override
	public void importDocuments() {
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
	public List<Document> search(String message) {
		return simpleVectorStore.similaritySearch(SearchRequest
				.builder()
				.query(message)
				.topK(2)
				.build());
	}

	@Override
    public Flux<ChatResponse> retrieve(String message) {
		return chatClient.prompt().user(message).stream().chatResponse();
	}

}
