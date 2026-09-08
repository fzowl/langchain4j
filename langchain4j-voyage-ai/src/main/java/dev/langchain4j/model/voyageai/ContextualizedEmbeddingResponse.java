package dev.langchain4j.model.voyageai;

import java.util.List;

/**
 * Wire response for Voyage's contextualized chunk embeddings endpoint ({@code /contextualizedembeddings}).
 *
 * <p>The top-level {@code data} is a list of documents; each document carries its own {@code data} list of
 * per-chunk embeddings (reusing {@link EmbeddingResponse.EmbeddingData}, which already decodes both the numeric
 * array and the base64 encodings).
 */
class ContextualizedEmbeddingResponse {

    private String object;
    private List<DocumentData> data;
    private String model;
    private TokenUsage usage;

    public String getObject() {
        return object;
    }

    public List<DocumentData> getData() {
        return data;
    }

    public String getModel() {
        return model;
    }

    public TokenUsage getUsage() {
        return usage;
    }

    static class DocumentData {

        private String object;
        private List<EmbeddingResponse.EmbeddingData> data;
        private Integer index;

        public String getObject() {
            return object;
        }

        public List<EmbeddingResponse.EmbeddingData> getData() {
            return data;
        }

        public Integer getIndex() {
            return index;
        }
    }
}
