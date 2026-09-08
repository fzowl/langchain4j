---
sidebar_position: 5
---

# VoyageAI by MongoDB

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

The reranker model names below are known to `VoyageAiScoringModelName`; any other name accepted by the API can be
passed as a `String`. See the [VoyageAI by MongoDB reranker docs](https://docs.voyageai.com/docs/reranker) for the
authoritative, up-to-date list.

- **Current**: `rerank-3`, `rerank-3-lite`, `rerank-2.5`, `rerank-2.5-lite`
- **Previous generation** (still accessible): `rerank-2`, `rerank-2-lite`, `rerank-1`, `rerank-lite-1`


## Examples
- [Advanced RAG with re-ranking](https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_3_advanced/_03_Advanced_RAG_with_ReRanking_Example.java)
- [VoyageAiScoringModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiScoringModelExample.java)
