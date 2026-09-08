package dev.langchain4j.model.voyageai;

import java.util.HashMap;
import java.util.Map;

public enum VoyageAiEmbeddingModelName {
    // Current general-purpose models (see https://docs.voyageai.com/docs/embeddings).
    // All voyage-4 models default to 1024 dimensions (also support 256, 512, 2048).
    VOYAGE_4_LARGE("voyage-4-large", 1024),
    VOYAGE_4("voyage-4", 1024),
    VOYAGE_4_LITE("voyage-4-lite", 1024),
    VOYAGE_4_NANO("voyage-4-nano", 1024),

    // Current domain-specific models.
    VOYAGE_CODE_4("voyage-code-4", 1024),
    VOYAGE_FINANCE_2("voyage-finance-2", 1024),
    VOYAGE_LAW_2("voyage-law-2", 1024),

    // Current contextualized-chunk model, served on the /contextualizedembeddings endpoint
    // (see https://docs.voyageai.com/docs/contextualized-chunk-embeddings and
    // {@link VoyageAiContextualizedEmbeddingModel}).
    VOYAGE_CONTEXT_4("voyage-context-4", 1024),

    // Current multimodal model, served on the /multimodalembeddings endpoint.
    VOYAGE_MULTIMODAL_3_5("voyage-multimodal-3.5", 1024),

    // Previous-generation models, still accessible via the API.
    VOYAGE_3_5("voyage-3.5", 1024),
    VOYAGE_3_5_LITE("voyage-3.5-lite", 1024),
    VOYAGE_3_LARGE("voyage-3-large", 1024),
    VOYAGE_3("voyage-3", 1024),
    VOYAGE_3_LITE("voyage-3-lite", 512),
    VOYAGE_MULTILINGUAL_2("voyage-multilingual-2", 1024),
    VOYAGE_CODE_3("voyage-code-3", 1024),
    VOYAGE_CODE_2("voyage-code-2", 1536),
    VOYAGE_CONTEXT_3("voyage-context-3", 1024),
    VOYAGE_MULTIMODAL_3("voyage-multimodal-3", 1024);

    private final String stringValue;
    private final Integer dimension;

    VoyageAiEmbeddingModelName(String stringValue, Integer dimension) {
        this.stringValue = stringValue;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public Integer dimension() {
        return dimension;
    }

    private static final Map<String, Integer> KNOWN_DIMENSION =
            new HashMap<>(VoyageAiEmbeddingModelName.values().length);

    static {
        for (VoyageAiEmbeddingModelName embeddingModelName : VoyageAiEmbeddingModelName.values()) {
            KNOWN_DIMENSION.put(embeddingModelName.toString(), embeddingModelName.dimension());
        }
    }

    public static Integer knownDimension(String modelName) {
        return KNOWN_DIMENSION.get(modelName);
    }
}
