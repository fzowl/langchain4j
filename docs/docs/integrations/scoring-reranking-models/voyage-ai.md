---
sidebar_position: 5
---

# VoyageAI by MongoDB

> Voyage AI is now part of MongoDB; the service and its models are branded **VoyageAI by MongoDB**.

## Maven Dependency

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-voyage-ai</artifactId>
    <version>1.20.0-beta30</version>
</dependency>
```


## APIs

- `VoyageAiScoringModel`

## Models

Set the reranker via `VoyageAiScoringModel.builder().modelName(...)`. Current models
(see the [VoyageAI by MongoDB reranker docs](https://docs.voyageai.com/docs/reranker)):
`rerank-2.5`, `rerank-2.5-lite` (recommended), `rerank-2`, `rerank-2-lite`, `rerank-1`, `rerank-lite-1`.


## Examples
- [Advanced RAG with re-ranking](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_03_Advanced_RAG_with_ReRanking_Example.java)
- [VoyageAiScoringModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiScoringModelExample.java)
