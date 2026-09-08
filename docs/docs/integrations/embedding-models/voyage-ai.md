---
sidebar_position: 19
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

- `VoyageAiEmbeddingModel`

## Capabilities

- **Multimodal** (`voyage-multimodal-3`, `voyage-multimodal-3.5` — auto-detected from the model name): embeds
  text and images into a shared vector space; interleaved text + image is fused into a single embedding.
  Provide image inputs as `ImageContent` (URL or base64) in an `EmbeddingRequest`.
- **Per-call parameters**: `input_type` (`EmbeddingInputType.QUERY` / `DOCUMENT`).
- **Listeners**: configure via `VoyageAiEmbeddingModel.builder().listeners(...)`.

See [Embedding Model](/tutorials/rag#embedding-model) for the request/response API and multimodal usage.

## Models

Set the model via `VoyageAiEmbeddingModel.builder().modelName(...)`. Current models
(see the [VoyageAI by MongoDB embeddings docs](https://docs.voyageai.com/docs/embeddings)):

| Model | Notes |
|-------|-------|
| `voyage-4-large` | Best general-purpose & multilingual quality |
| `voyage-4` | General-purpose & multilingual |
| `voyage-4-lite` | Optimized for latency and cost |
| `voyage-code-4` | Optimized for code retrieval |
| `voyage-code-3` | Previous-generation code model |
| `voyage-3.5`, `voyage-3.5-lite`, `voyage-3-large`, `voyage-3`, `voyage-3-lite` | Previous-generation general models |
| `voyage-finance-2`, `voyage-law-2`, `voyage-multilingual-2` | Domain-specialized |
| `voyage-multimodal-3.5`, `voyage-multimodal-3` | Multimodal (text + image), auto-detected from the model name |

The 4-series models default to 1024 dimensions (256, 512 and 2048 are also selectable per request).

## Examples

- [VoyageAiEmbeddingModelIT](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-voyage-ai/src/test/java/dev/langchain4j/model/voyageai/VoyageAiEmbeddingModelIT.java)
- [VoyageAiEmbeddingModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiEmbeddingModelExample.java)
