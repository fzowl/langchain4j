---
sidebar_position: 5
---

# Voyage AI by MongoDB

## Maven Dependency

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-voyage-ai</artifactId>
    <version>1.23.0-beta33</version>
</dependency>
```


## Voyage AI by MongoDB Models

- `VoyageAiScoringModel`

By default requests go to `https://api.voyageai.com/v1/`, or to `https://ai.mongodb.com/v1/` when the API key
starts with `al-`. A `baseUrl(...)` set on the builder always wins.

The recommended rerankers are `rerank-3` and `rerank-3-lite` (`VoyageAiScoringModelName.RERANK_3`,
`RERANK_3_LITE`). `rerank-2.5`, `rerank-2.5-lite` and the older `rerank-2`, `rerank-2-lite`, `rerank-1`
and `rerank-lite-1` remain available.


## Observability

Attach [listeners](/tutorials/observability#scoringmodel-listener) with `VoyageAiScoringModel.builder().listeners(...)` to observe
requests, responses and errors.

## Examples
- [Advanced RAG with re-ranking](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_03_Advanced_RAG_with_ReRanking_Example.java)
- [VoyageAiScoringModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiScoringModelExample.java)
