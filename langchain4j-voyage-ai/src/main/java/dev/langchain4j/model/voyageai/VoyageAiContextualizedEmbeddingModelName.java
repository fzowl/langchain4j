package dev.langchain4j.model.voyageai;

/**
 * Voyage AI contextualized chunk embedding models, served on the {@code /contextualizedembeddings} endpoint.
 * Use them with {@link VoyageAiContextualizedEmbeddingModel}.
 */
public enum VoyageAiContextualizedEmbeddingModelName {
    VOYAGE_CONTEXT_4("voyage-context-4"),
    VOYAGE_CONTEXT_3("voyage-context-3");

    private final String stringValue;

    VoyageAiContextualizedEmbeddingModelName(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
