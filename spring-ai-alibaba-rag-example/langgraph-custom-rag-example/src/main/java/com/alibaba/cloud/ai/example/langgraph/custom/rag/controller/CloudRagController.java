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

package com.alibaba.cloud.ai.example.langgraph.custom.rag.controller;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.example.langgraph.custom.rag.service.RagService;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Title Cloud rag controller.<br>
 * Description Cloud rag controller.<br>
 *
 * @author txuw
 */

@RestController
@RequestMapping("/ai")
public class CloudRagController {

	private final RagService cloudRagService;

	public CloudRagController(RagService cloudRagService) {
		this.cloudRagService = cloudRagService;
	}

	@GetMapping("/rag/graph/call")
	public Map<String,Object> generate(@RequestParam(value = "message",
			defaultValue = "spring ai alibaba的环境要求") String message) {
		return cloudRagService.graphCall(message);
	}

}
