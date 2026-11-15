package dev.langchain4j.model.voyageai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@EnabledIfEnvironmentVariable(named = "VOYAGE_API_KEY", matches = ".+")
class VoyageAiContextualizedEmbeddingModelIT {

    private static VoyageAiContextualizedEmbeddingModel.Builder model(String modelName) {
        return VoyageAiContextualizedEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(modelName)
                .maxRetries(0);
    }

    @ParameterizedTest
    @EnumSource(VoyageAiContextualizedEmbeddingModelName.class)
    void should_embed_documents_and_return_their_chunks(VoyageAiContextualizedEmbeddingModelName modelName) {

        VoyageAiContextualizedEmbeddingModel model = model(modelName.toString()).build();

        Response<List<List<VoyageAiContextualizedEmbeddingModel.Chunk>>> response =
                model.embedDocuments(List.of("Cats purr and sleep a lot.", "Dogs bark and fetch sticks."));

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).get(0).text()).contains("Cats");
        assertThat(response.content().get(1).get(0).text()).contains("Dogs");
        assertThat(response.content().get(0).get(0).embedding().dimension()).isEqualTo(1024);
        assertThat(response.tokenUsage().totalTokenCount()).isPositive();
    }

    @Test
    void should_embed_queries_and_documents_in_the_same_space() {

        VoyageAiContextualizedEmbeddingModel model =
                model("voyage-context-4").dimensions(256).build();

        Embedding query =
                model.embedQueries(List.of("which animal purrs?")).content().get(0);
        List<List<VoyageAiContextualizedEmbeddingModel.Chunk>> documents = model.embedDocuments(
                        List.of("Cats purr and sleep a lot.", "Stock markets fell sharply on Monday."))
                .content();

        assertThat(query.dimension()).isEqualTo(256);
        assertThat(CosineSimilarity.between(query, documents.get(0).get(0).embedding()))
                .isGreaterThan(
                        CosineSimilarity.between(query, documents.get(1).get(0).embedding()));
    }

    @Test
    void should_embed_several_queries_one_embedding_each() {

        VoyageAiContextualizedEmbeddingModel model = model("voyage-context-4").build();

        List<Embedding> embeddings = model.embedQueries(List.of("which animal purrs?", "which animal barks?"))
                .content();

        assertThat(embeddings).hasSize(2);
    }

    @Test
    void should_fail_for_an_unknown_model() {

        VoyageAiContextualizedEmbeddingModel model =
                model("voyage-context-does-not-exist").build();

        assertThatThrownBy(() -> model.embedDocuments(List.of("hello")))
                .hasMessageContaining("voyage-context-does-not-exist is not supported");
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        String ciDelaySeconds = System.getenv("CI_DELAY_SECONDS_VOYAGE_AI");
        if (ciDelaySeconds != null) {
            Thread.sleep(Integer.parseInt(ciDelaySeconds) * 1000L);
        }
    }
}
