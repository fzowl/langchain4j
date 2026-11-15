package dev.langchain4j.model.voyageai;

import static dev.langchain4j.internal.Exceptions.illegalArgument;
import static dev.langchain4j.internal.RetryUtils.withRetryMappingExceptions;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotEmpty;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.exception.InternalServerException;
import dev.langchain4j.http.client.HttpClientBuilder;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * Embeds with the <a href="https://docs.voyageai.com/docs/contextualized-chunk-embeddings">Voyage AI by MongoDB
 * contextualized chunk embeddings API</a> ({@code voyage-context-4}, {@code voyage-context-3}): the embedding of
 * a chunk is influenced by the other chunks of the same document.
 * <p>
 * Documents are always sent with {@code input_type=document}, {@code enable_auto_chunking=true} and
 * {@code chunk_size=32000}: each document is a single string, Voyage AI splits it into chunks and returns the
 * text and the embedding of every chunk. A document may therefore yield more than one embedding, so this model
 * is not an {@link dev.langchain4j.model.embedding.EmbeddingModel}, which returns one embedding per input.
 * Queries are sent with {@code input_type=query} and are not chunked.
 */
public class VoyageAiContextualizedEmbeddingModel {

    static final int CHUNK_SIZE = 32_000;
    private static final int MAX_INPUTS_PER_REQUEST = 1000;

    private final VoyageAiClient client;
    private final Integer maxRetries;
    private final String modelName;
    private final Integer dimensions;
    private final int maxDocumentsPerBatch;

    public VoyageAiContextualizedEmbeddingModel(Builder builder) {
        this.maxRetries = getOrDefault(builder.maxRetries, 2);
        this.modelName = ensureNotBlank(builder.modelName, "modelName");
        this.dimensions = builder.dimensions;
        this.maxDocumentsPerBatch =
                ensureGreaterThanZero(getOrDefault(builder.maxDocumentsPerBatch, 128), "maxDocumentsPerBatch");
        if (this.maxDocumentsPerBatch > MAX_INPUTS_PER_REQUEST) {
            throw illegalArgument(
                    "'maxDocumentsPerBatch' (%s) must not exceed %s, the limit of Voyage AI",
                    this.maxDocumentsPerBatch, MAX_INPUTS_PER_REQUEST);
        }

        this.client = VoyageAiClient.builder()
                .httpClientBuilder(builder.httpClientBuilder)
                .baseUrl(builder.baseUrl)
                .apiKey(ensureNotBlank(builder.apiKey, "apiKey"))
                .timeout(getOrDefault(builder.timeout, ofSeconds(60)))
                .logRequests(getOrDefault(builder.logRequests, false))
                .logResponses(getOrDefault(builder.logResponses, false))
                .logger(builder.logger)
                .customHeaders(builder.customHeadersSupplier)
                .build();
    }

    /**
     * A chunk of a document, as split by Voyage AI, with its contextualized embedding.
     *
     * @param text      the text of the chunk
     * @param embedding the embedding of the chunk
     */
    public record Chunk(String text, Embedding embedding) {}

    public String modelName() {
        return modelName;
    }

    /**
     * Embeds whole documents. Voyage AI splits every document into chunks.
     *
     * @param documents the documents, one string per document; a list larger than {@code maxDocumentsPerBatch}
     *                  is split into several requests
     * @return for each document, in the order of {@code documents}, its chunks in document order
     */
    public Response<List<List<Chunk>>> embedDocuments(List<String> documents) {
        ensureNotEmpty(documents, "documents");

        List<List<Chunk>> result = new ArrayList<>();
        int totalTokens = 0;
        for (int i = 0; i < documents.size(); i += maxDocumentsPerBatch) {
            List<String> batch = documents.subList(i, Math.min(i + maxDocumentsPerBatch, documents.size()));
            ContextualizedEmbeddingResponse response = send(
                    new ContextualizedEmbeddingRequest(batch, modelName, "document", true, CHUNK_SIZE, dimensions));
            for (List<EmbeddingResponse.EmbeddingData> chunks : resultsInOrder(response, batch.size())) {
                result.add(toChunks(chunks));
            }
            totalTokens += tokens(response);
        }
        return Response.from(result, new TokenUsage(totalTokens));
    }

    /**
     * Embeds queries, one embedding per query. Queries are not chunked.
     *
     * @param queries the queries
     * @return the embedding of each query, in the order of {@code queries}
     */
    public Response<List<Embedding>> embedQueries(List<String> queries) {
        ensureNotEmpty(queries, "queries");

        List<Embedding> result = new ArrayList<>();
        int totalTokens = 0;
        for (int i = 0; i < queries.size(); i += maxDocumentsPerBatch) {
            List<String> batch = queries.subList(i, Math.min(i + maxDocumentsPerBatch, queries.size()));
            ContextualizedEmbeddingResponse response =
                    send(new ContextualizedEmbeddingRequest(batch, modelName, "query", null, null, dimensions));
            for (List<EmbeddingResponse.EmbeddingData> chunks : resultsInOrder(response, batch.size())) {
                if (chunks.size() != 1) {
                    throw new InternalServerException(format(
                            "Contextualized embedding failed: expected one embedding per query, but got %s",
                            chunks.size()));
                }
                result.add(Embedding.from(chunks.get(0).getEmbedding()));
            }
            totalTokens += tokens(response);
        }
        return Response.from(result, new TokenUsage(totalTokens));
    }

    private ContextualizedEmbeddingResponse send(ContextualizedEmbeddingRequest request) {
        return withRetryMappingExceptions(() -> client.contextualizedEmbed(request), maxRetries);
    }

    /** The chunks of each input, ordered by input index and, within an input, by chunk index. */
    private static List<List<EmbeddingResponse.EmbeddingData>> resultsInOrder(
            ContextualizedEmbeddingResponse response, int expectedInputs) {
        List<ContextualizedEmbeddingResponse.Result> results = response.getData();
        if (results == null || results.size() != expectedInputs) {
            throw new InternalServerException(format(
                    "Contextualized embedding failed: expected %s results, but got %s",
                    expectedInputs, results == null ? 0 : results.size()));
        }
        return results.stream()
                .sorted(Comparator.comparingInt(ContextualizedEmbeddingResponse.Result::getIndex))
                .map(result -> result.getData().stream()
                        .sorted(Comparator.comparingInt(EmbeddingResponse.EmbeddingData::getIndex))
                        .toList())
                .toList();
    }

    private static List<Chunk> toChunks(List<EmbeddingResponse.EmbeddingData> chunks) {
        return chunks.stream()
                .map(chunk -> new Chunk(chunk.getText(), Embedding.from(chunk.getEmbedding())))
                .toList();
    }

    private static int tokens(ContextualizedEmbeddingResponse response) {
        return response.getUsage() != null && response.getUsage().getTotalTokens() != null
                ? response.getUsage().getTotalTokens()
                : 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpClientBuilder httpClientBuilder;
        private Supplier<Map<String, String>> customHeadersSupplier;
        private String baseUrl;
        private Duration timeout;
        private Integer maxRetries;
        private String apiKey;
        private String modelName;
        private Integer dimensions;
        private Integer maxDocumentsPerBatch;
        private Boolean logRequests;
        private Boolean logResponses;
        private Logger logger;

        public Builder httpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        public Builder customHeaders(Map<String, String> customHeaders) {
            this.customHeadersSupplier = () -> customHeaders;
            return this;
        }

        public Builder customHeaders(Supplier<Map<String, String>> customHeadersSupplier) {
            this.customHeadersSupplier = customHeadersSupplier;
            return this;
        }

        /**
         * Sets the base URL of the Voyage AI API.
         * Defaults to {@code "https://api.voyageai.com/v1/"}, or to {@code "https://ai.mongodb.com/v1/"} when the
         * API key starts with {@code al-}. A base URL set here always wins.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the HTTP request timeout. Defaults to 60 seconds.
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the maximum number of retries on transient errors. Defaults to {@code 2}.
         */
        public Builder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the Voyage AI API key used to authenticate requests.
         * See <a href="https://dashboard.voyageai.com/api-keys">Voyage AI by MongoDB dashboard</a> to obtain a key.
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Name of the model. Required.
         *
         * @see VoyageAiContextualizedEmbeddingModelName
         */
        public Builder modelName(VoyageAiContextualizedEmbeddingModelName modelName) {
            this.modelName = modelName.toString();
            return this;
        }

        /**
         * Name of the model. Required.
         *
         * @see VoyageAiContextualizedEmbeddingModelName
         */
        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        /**
         * Sets the number of dimensions of the embeddings ({@code output_dimension}): 2048, 1024 (default), 512
         * or 256. Not sent when {@code null} (default).
         */
        public Builder dimensions(Integer dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        /**
         * Sets the maximum number of documents (or queries) in one request. Defaults to {@code 128}.
         * Voyage AI accepts at most 1000 inputs and a limited number of tokens per request (see the
         * <a href="https://docs.voyageai.com/docs/contextualized-chunk-embeddings">documentation</a>), and this
         * model does not count tokens: lower this value if your documents are long.
         */
        public Builder maxDocumentsPerBatch(Integer maxDocumentsPerBatch) {
            this.maxDocumentsPerBatch = maxDocumentsPerBatch;
            return this;
        }

        public Builder logRequests(Boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public Builder logResponses(Boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        /**
         * @param logger an alternate {@link Logger} to be used instead of the default one provided by Langchain4J for logging requests and responses.
         */
        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public VoyageAiContextualizedEmbeddingModel build() {
            return new VoyageAiContextualizedEmbeddingModel(this);
        }
    }
}
