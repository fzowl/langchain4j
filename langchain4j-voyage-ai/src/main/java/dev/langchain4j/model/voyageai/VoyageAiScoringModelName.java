package dev.langchain4j.model.voyageai;

public enum VoyageAiScoringModelName {
    // Current models (see https://docs.voyageai.com/docs/reranker).
    RERANK_3("rerank-3"),
    RERANK_3_LITE("rerank-3-lite"),
    RERANK_2_5("rerank-2.5"),
    RERANK_2_5_LITE("rerank-2.5-lite"),

    // Previous-generation models, still accessible via the API.
    RERANK_2("rerank-2"),
    RERANK_2_LITE("rerank-2-lite"),
    RERANK_1("rerank-1"),
    RERANK_LITE_1("rerank-lite-1");

    private final String stringValue;

    VoyageAiScoringModelName(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
