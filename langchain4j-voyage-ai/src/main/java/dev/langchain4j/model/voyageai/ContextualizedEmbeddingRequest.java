package dev.langchain4j.model.voyageai;

import java.util.List;

/**
 * Wire request for Voyage's contextualized embeddings endpoint ({@code /contextualizedembeddings}).
 * Field names are serialized in snake_case by {@link VoyageAiJsonUtils}.
 * <p>
 * With {@code enableAutoChunking} the {@code inputs} are flat: one string per document.
 */
class ContextualizedEmbeddingRequest {

    private final List<String> inputs;
    private final String model;
    private final String inputType;
    private final Boolean enableAutoChunking;
    private final Integer chunkSize;
    private final Integer outputDimension;

    ContextualizedEmbeddingRequest(
            List<String> inputs,
            String model,
            String inputType,
            Boolean enableAutoChunking,
            Integer chunkSize,
            Integer outputDimension) {
        this.inputs = inputs;
        this.model = model;
        this.inputType = inputType;
        this.enableAutoChunking = enableAutoChunking;
        this.chunkSize = chunkSize;
        this.outputDimension = outputDimension;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public String getModel() {
        return model;
    }

    public String getInputType() {
        return inputType;
    }

    public Boolean getEnableAutoChunking() {
        return enableAutoChunking;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public Integer getOutputDimension() {
        return outputDimension;
    }
}
