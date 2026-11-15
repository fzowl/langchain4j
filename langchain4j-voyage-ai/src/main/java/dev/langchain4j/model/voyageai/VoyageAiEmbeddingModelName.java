package dev.langchain4j.model.voyageai;

import java.util.HashMap;
import java.util.Map;

public enum VoyageAiEmbeddingModelName {
    VOYAGE_4_LARGE("voyage-4-large", 1024, true),
    VOYAGE_4("voyage-4", 1024, true),
    VOYAGE_4_LITE("voyage-4-lite", 1024, true),

    VOYAGE_3_5("voyage-3.5", 1024, true),
    VOYAGE_3_5_LITE("voyage-3.5-lite", 1024, true),

    VOYAGE_3("voyage-3", 1024, false),
    VOYAGE_3_LITE("voyage-3-lite", 512, false),
    VOYAGE_3_LARGE("voyage-3-large", 1024, true),

    VOYAGE_FINANCE_2("voyage-finance-2", 1024, false),

    VOYAGE_MULTILINGUAL_2("voyage-multilingual-2", 1024, false),

    VOYAGE_LAW_2("voyage-law-2", 1024, false),

    VOYAGE_CODE_2("voyage-code-2", 1536, false),
    VOYAGE_CODE_3("voyage-code-3", 1024, true),
    VOYAGE_CODE_4("voyage-code-4", 1024, true),

    VOYAGE_MULTIMODAL_3("voyage-multimodal-3", 1024, false),
    VOYAGE_MULTIMODAL_3_5("voyage-multimodal-3.5", 1024, true);

    private final String stringValue;
    private final Integer dimension;
    private final boolean supportsOutputDimension;

    /**
     * @param dimension               the default dimension of the embeddings
     * @param supportsOutputDimension whether the model accepts {@code output_dimension} (Matryoshka:
     *                                2048, 1024, 512 or 256); the other models have a single fixed dimension
     */
    VoyageAiEmbeddingModelName(String stringValue, Integer dimension, boolean supportsOutputDimension) {
        this.stringValue = stringValue;
        this.dimension = dimension;
        this.supportsOutputDimension = supportsOutputDimension;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public Integer dimension() {
        return dimension;
    }

    private static final Map<String, Boolean> SUPPORTS_OUTPUT_DIMENSION =
            new HashMap<>(VoyageAiEmbeddingModelName.values().length);

    private static final Map<String, Integer> KNOWN_DIMENSION =
            new HashMap<>(VoyageAiEmbeddingModelName.values().length);

    static {
        for (VoyageAiEmbeddingModelName embeddingModelName : VoyageAiEmbeddingModelName.values()) {
            KNOWN_DIMENSION.put(embeddingModelName.toString(), embeddingModelName.dimension());
            SUPPORTS_OUTPUT_DIMENSION.put(embeddingModelName.toString(), embeddingModelName.supportsOutputDimension);
        }
    }

    /**
     * Whether the model accepts {@code output_dimension}. It is {@code false} only for the models known to have a
     * single fixed dimension; a model that is not in this enum is assumed to accept it, and Voyage AI rejects an
     * unsupported value.
     */
    static boolean supportsOutputDimension(String modelName) {
        return SUPPORTS_OUTPUT_DIMENSION.getOrDefault(modelName, true);
    }

    public static Integer knownDimension(String modelName) {
        return KNOWN_DIMENSION.get(modelName);
    }
}
