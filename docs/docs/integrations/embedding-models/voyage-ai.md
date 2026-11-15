---
sidebar_position: 19
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

## APIs

- `VoyageAiEmbeddingModel`
- `VoyageAiContextualizedEmbeddingModel`

## Capabilities

- **Models**: `voyage-4-large`, `voyage-4`, `voyage-4-lite` and `voyage-code-4` are the current text models
  (`voyage-3-large`, `voyage-3.5`, `voyage-3.5-lite` and `voyage-code-3` are the previous generation, and
  `voyage-finance-2` and `voyage-law-2` are the domain-specific ones). Pass the model as
  `VoyageAiEmbeddingModelName` or as a string.
- **Dimensions** (`output_dimension`, Matryoshka): the models above, except `voyage-finance-2` and `voyage-law-2`,
  return 1024 dimensions by default and also accept 2048, 512 and 256, as does `voyage-multimodal-3.5`. Set it with
  `VoyageAiEmbeddingModel.builder().dimensions(256)`, or per call with `EmbeddingRequest.builder().dimensions(256)`.
  A model with a single fixed dimension rejects it. Nothing is sent unless you ask for it.
- **Contextualized chunk embeddings** (`voyage-context-4`, `voyage-context-3`): `VoyageAiContextualizedEmbeddingModel`
  embeds each chunk of a document in the context of the whole document. `embedDocuments` takes one string per
  document, Voyage AI splits it into chunks (`enable_auto_chunking`, `chunk_size` 32000) and the result holds the
  text and the embedding of every chunk, per document. `embedQueries` embeds queries, which are not chunked.
- **Multimodal** (`voyage-multimodal-3`, `voyage-multimodal-3.5` — auto-detected from the model name): embeds
  text and images into a shared vector space; interleaved text + image is fused into a single embedding.
  Provide image inputs as `ImageContent` (URL or base64) in an `EmbeddingRequest`.
- **Per-call parameters**: `input_type` (`EmbeddingInputType.QUERY` / `DOCUMENT`), and Voyage AI's own
  `truncation` and `encoding_format` via `VoyageAiEmbeddingRequestParameters`. A value set on the builder is
  the default for every call, and a value set on the request overrides it for that call. `truncation(false)`
  makes a call fail instead of shortening an input that exceeds the model's context. `encoding_format`
  (`"base64"`) only asks for a smaller response; the embeddings you get back are the same, and only the text
  models accept it, so a multimodal model rejects it.
- **Listeners**: configure via `VoyageAiEmbeddingModel.builder().listeners(...)`.

See [Embedding Model](/tutorials/rag#embedding-model) for the request/response API and multimodal usage.

## Examples

- [VoyageAiEmbeddingModelIT](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-voyage-ai/src/test/java/dev/langchain4j/model/voyageai/VoyageAiEmbeddingModelIT.java)
- [VoyageAiEmbeddingModelExample](https://github.com/langchain4j/langchain4j-examples/blob/main/voyage-ai-examples/src/main/java/VoyageAiEmbeddingModelExample.java)
