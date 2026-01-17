# LangGraph Custom RAG Example 模块

## 模块说明

本模块使用 **Spring AI Alibaba** 参考 **LangGraph** 用例实现一个自定义的 RAG (检索增强生成) 流程。
https://docs.langchain.com/oss/python/langgraph/agentic-rag

本项目实现了一个具备 **自我校正 (Self-Correction)** 能力的 RAG 工作流：
1. **查询生成 (Generate Query)**：根据用户原始问题生成更适合检索的查询。
2. **文档分级 (Grade Documents)**：检索相关文档并评估其与查询的相关性。
3. **重写 (Rewrite)**：如果检索到的文档相关性不足，则自动重构查询并重新开始检索流程。
4. **生成回答 (Generate Answer)**：基于高质量的相关文档生成最终答案。

## 接口文档

### CloudRagController 接口

#### 1. graphCall 方法

**接口路径：** `GET /ai/rag/graph/call`

**功能描述：** 触发 LangGraph 驱动的 RAG 流程，并返回最终执行结果。

**主要参数：**
- `message` (String, 可选): 用户的问题。默认值为 "spring ai alibaba的环境要求"。

**返回格式：** JSON (包含 graph 执行过程中的状态数据，如 `query`, `content`, `answer` 等)。

**示例请求：**
```bash
GET http://localhost:8080/ai/rag/graph/call?message=如何在Spring Ai Alibaba中添加子图
```

## 技术实现

### 核心组件
- **Spring Boot**: 应用基础框架。
- **Spring AI Alibaba**: 集成阿里云 DashScope (通义千问) 模型及 RAG 能力。
- **LangGraph (Java 版)**: 实现复杂的工作流控制和状态管理。
- **DashScope Cloud Retrieval**: 结合百炼平台的云端知识库检索能力。

### 关键节点说明
- `generate_query`: 负责问题重构，优化检索关键词。
- `grade_documents`: 对检索到的文档进行质量过滤，决定是进入回答环节还是重写环节。
- `rewrite`: 当现有文档无法回答问题时，对 Query 进行再次优化。
- `generate_answer`: 汇总高质量上下文并调用 LLM 生成最终答案。

### 配置要点
- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量。
- 默认端口：8080。

## 快速开始

1. **设置 API Key**:
   ```bash
   export AI_DASHSCOPE_API_KEY=your_api_key_here
   ```
2. **启动应用**: 运行 `LangGraphCustomRagExampleApplication.java`。
3. **测试接口**: 使用浏览器或 IDEA 的 HTTP Client 运行 `example.http` 中的请求。

## 注意事项

1. **知识库配置**: 确保已在阿里云百炼平台配置了对应的知识库（Workspace ID 等信息）。
2. **网络连接**: 需要能够访问阿里云 DashScope 服务。
3. **执行流程**: 可以通过控制台日志查看各个 Node 的流转情况。
