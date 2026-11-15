package dev.langchain4j.model.voyageai;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.http.client.MockHttpClient;
import dev.langchain4j.http.client.MockHttpClientBuilder;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class VoyageAiBaseUrlTest {

    private static final String VOYAGE_HOST = "https://api.voyageai.com/v1/";
    private static final String MONGODB_HOST = "https://ai.mongodb.com/v1/";

    /** Runs a call on a mock HTTP client and returns the URL it was sent to; the mock's reply is not valid. */
    private static String urlOf(Consumer<MockHttpClientBuilder> call) {
        MockHttpClient mock = new MockHttpClient();
        try {
            call.accept(new MockHttpClientBuilder(mock));
        } catch (Exception ignored) {
            // the reply of the mock is empty, only the request matters
        }
        return mock.request().url();
    }

    private static String embeddingUrl(String apiKey, String baseUrl) {
        return urlOf(http -> VoyageAiEmbeddingModel.builder()
                .httpClientBuilder(http)
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("voyage-4-lite")
                .maxRetries(0)
                .build()
                .embed("hello"));
    }

    @Test
    void should_use_the_voyage_host_for_a_regular_key() {
        assertThat(embeddingUrl("pa-key", null)).isEqualTo(VOYAGE_HOST + "embeddings");
    }

    @Test
    void should_use_the_mongodb_host_for_an_al_key() {
        assertThat(embeddingUrl("al-key", null)).isEqualTo(MONGODB_HOST + "embeddings");
    }

    @Test
    void should_prefer_an_explicit_base_url_over_the_key() {
        assertThat(embeddingUrl("al-key", "https://proxy.example.com/v1"))
                .isEqualTo("https://proxy.example.com/v1/embeddings");
    }

    @Test
    void should_choose_the_host_by_key_for_the_scoring_and_contextualized_models() {
        assertThat(urlOf(http -> VoyageAiScoringModel.builder()
                        .httpClientBuilder(http)
                        .apiKey("al-key")
                        .modelName("rerank-3")
                        .maxRetries(0)
                        .build()
                        .scoreAll(List.of(dev.langchain4j.data.segment.TextSegment.from("a")), "q")))
                .isEqualTo(MONGODB_HOST + "rerank");

        assertThat(urlOf(http -> VoyageAiContextualizedEmbeddingModel.builder()
                        .httpClientBuilder(http)
                        .apiKey("pa-key")
                        .modelName("voyage-context-4")
                        .maxRetries(0)
                        .build()
                        .embedDocuments(List.of("doc"))))
                .isEqualTo(VOYAGE_HOST + "contextualizedembeddings");
    }
}
