package dev.langchain4j.model.voyageai;

import java.util.List;

/**
 * Wire request for Voyage's contextualized chunk embeddings endpoint ({@code /contextualizedembeddings}).
 *
 * <p>Per the <a href="https://docs.voyageai.com/docs/contextualized-chunk-embeddings">official spec</a>, the
 * {@code inputs} field is {@code Union[List[List[str]], List[str]]}: both a list of documents (each an ordered
 * list of chunk strings) and a flat list of document strings are accepted. This class stores {@code inputs} as
 * an {@link Object} so either shape can be serialized verbatim by {@link VoyageAiJsonUtils}; use
 * {@link Builder#chunkedInputs(List)} for the {@code List[List[str]]} shape and {@link Builder#flatInputs(List)}
 * for the {@code List[str]} shape. Field names are serialized in snake_case.
 */
class ContextualizedEmbeddingRequest {

    private Object inputs;
    private String model;
    private String inputType;
    private Boolean truncation;
    private String encodingFormat;

    ContextualizedEmbeddingRequest() {}

    ContextualizedEmbeddingRequest(
            Object inputs, String model, String inputType, Boolean truncation, String encodingFormat) {
        this.inputs = inputs;
        this.model = model;
        this.inputType = inputType;
        this.truncation = truncation;
        this.encodingFormat = encodingFormat;
    }

    public Object getInputs() {
        return inputs;
    }

    public String getModel() {
        return model;
    }

    public String getInputType() {
        return inputType;
    }

    public Boolean getTruncation() {
        return truncation;
    }

    public String getEncodingFormat() {
        return encodingFormat;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private Object inputs;
        private String model;
        private String inputType;
        private Boolean truncation;
        private String encodingFormat;

        /**
         * Sets {@code inputs} as {@code List[List[str]]}: each inner list holds the ordered chunks of one document.
         */
        Builder chunkedInputs(List<List<String>> inputs) {
            this.inputs = inputs;
            return this;
        }

        /**
         * Sets {@code inputs} as {@code List[str]}: each string is a standalone document (backend auto-chunking).
         */
        Builder flatInputs(List<String> inputs) {
            this.inputs = inputs;
            return this;
        }

        Builder model(String model) {
            this.model = model;
            return this;
        }

        Builder inputType(String inputType) {
            this.inputType = inputType;
            return this;
        }

        Builder truncation(Boolean truncation) {
            this.truncation = truncation;
            return this;
        }

        Builder encodingFormat(String encodingFormat) {
            this.encodingFormat = encodingFormat;
            return this;
        }

        ContextualizedEmbeddingRequest build() {
            return new ContextualizedEmbeddingRequest(inputs, model, inputType, truncation, encodingFormat);
        }
    }
}
