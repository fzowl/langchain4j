package dev.langchain4j.model.voyageai;

import java.util.List;

/**
 * Wire response of Voyage's contextualized embeddings endpoint: one result per input, each holding the
 * embeddings of the chunks of that input.
 */
class ContextualizedEmbeddingResponse {

    private List<Result> data;
    private String model;
    private TokenUsage usage;

    public List<Result> getData() {
        return data;
    }

    public String getModel() {
        return model;
    }

    public TokenUsage getUsage() {
        return usage;
    }

    static class Result {

        private List<EmbeddingResponse.EmbeddingData> data;
        private Integer index;

        public List<EmbeddingResponse.EmbeddingData> getData() {
            return data;
        }

        public Integer getIndex() {
            return index;
        }
    }
}
