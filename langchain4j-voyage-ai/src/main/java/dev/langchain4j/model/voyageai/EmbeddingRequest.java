package dev.langchain4j.model.voyageai;

import java.util.List;

class EmbeddingRequest {

    private List<String> input;
    private String model;
    private String inputType;
    private Boolean truncation;
    private String encodingFormat;
    private Integer outputDimension;

    EmbeddingRequest() {}

    EmbeddingRequest(
            List<String> input,
            String model,
            String inputType,
            Boolean truncation,
            String encodingFormat,
            Integer outputDimension) {
        this.input = input;
        this.model = model;
        this.inputType = inputType;
        this.truncation = truncation;
        this.encodingFormat = encodingFormat;
        this.outputDimension = outputDimension;
    }

    public List<String> getInput() {
        return input;
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

    public Integer getOutputDimension() {
        return outputDimension;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private List<String> input;
        private String model;
        private String inputType;
        private Boolean truncation;
        private String encodingFormat;
        private Integer outputDimension;

        Builder input(List<String> input) {
            this.input = input;
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

        Builder outputDimension(Integer outputDimension) {
            this.outputDimension = outputDimension;
            return this;
        }

        EmbeddingRequest build() {
            return new EmbeddingRequest(input, model, inputType, truncation, encodingFormat, outputDimension);
        }
    }
}
