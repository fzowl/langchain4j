---
sidebar_position: 19
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

- `VoyageAiEmbeddingModel`
- `VoyageAiContextualizedEmbeddingModel`

## Models

The model names below are known to `VoyageAiEmbeddingModelName`. Any other model name accepted by the API can be
passed as a `String`. See the [VoyageAI by MongoDB model docs](https://docs.voyageai.com/docs/embeddings) for the
authoritative, up-to-date list.

- **General-purpose**: `voyage-4-large`, `voyage-4`, `voyage-4-lite`, `voyage-4-nano`
- **Code**: `voyage-code-4`
- **Domain-specific**: `voyage-finance-2`, `voyage-law-2`
- **Contextualized chunks** (via `VoyageAiContextualizedEmbeddingModel`): `voyage-context-4`
- **Multimodal**: `voyage-multimodal-3.5`
- **Previous generation** (still accessible): `voyage-3.5`, `voyage-3.5-lite`, `voyage-3-large`, `voyage-3`,
  `voyage-3-lite`, `voyage-multilingual-2`, `voyage-code-3`, `voyage-code-2`, `voyage-context-3`,
  `voyage-multimodal-3`

## Capabilities

- **Multimodal** (`voyage-multimodal-3`, `voyage-multimodal-3.5` — auto-detected from the model name): embeds
  text and images into a shared vector space; interleaved text + image is fused into a single embedding.
  Provide image inputs as `ImageContent` (URL or base64) in an `EmbeddingRequest`.
- **Contextualized chunk embeddings** (`voyage-context-4` via `VoyageAiContextualizedEmbeddingModel`): embeds
  text segments as chunks that share context, so each chunk embedding is influenced by the others. The
  [`/contextualizedembeddings`](https://docs.voyageai.com/docs/contextualized-chunk-embeddings) endpoint accepts
  `inputs` as `Union[List[List[str]], List[str]]`; both shapes are supported via
  `VoyageAiContextualizedEmbeddingModel.builder().separateDocuments(...)`.
- **Per-call parameters**: `input_type` (`EmbeddingInputType.QUERY` / `DOCUMENT`).
- **Listeners**: configure via `VoyageAiEmbeddingModel.builder().listeners(...)`.

See [Embedding Model](/tutorials/rag#embedding-model) for the request/response API and multimodal usage.

## Examples

- [VoyageAiEmbeddingModelIT](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-voyage-ai/src/test/java/dev/langchain4j/model/voyageai/VoyageAiEmbeddingModelIT.java)
- [VoyageAiContextualizedEmbeddingModelIT](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-voyage-ai/src/test/java/dev/langchain4j/model/voyageai/VoyageAiContextualizedEmbeddingModelIT.java)
- [VoyageAiEmbeddingModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiEmbeddingModelExample.java)
