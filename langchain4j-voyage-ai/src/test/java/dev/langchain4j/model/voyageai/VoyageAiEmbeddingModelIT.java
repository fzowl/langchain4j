package dev.langchain4j.model.voyageai;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@EnabledIfEnvironmentVariable(named = "VOYAGE_API_KEY", matches = ".+")
class VoyageAiEmbeddingModelIT {

    @Test
    void should_embed_single_text() {

        // given
        EmbeddingModel model = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_3_LITE)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(false) // embeddings are huge in logs
                .build();

        // when
        Response<Embedding> response = model.embed("Hello World");

        // then
        assertThat(response.content().dimension()).isEqualTo(model.dimension());

        // FIXME: it's strange that Voyage may return totalTokens=0
        assertThat(response.tokenUsage().inputTokenCount()).isNotNegative();
        assertThat(response.tokenUsage().outputTokenCount()).isNull();
        assertThat(response.tokenUsage().totalTokenCount()).isNotNegative();

        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_respect_encoding_format() {

        // given
        EmbeddingModel model = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_3_LITE)
                .timeout(Duration.ofSeconds(60))
                .encodingFormat("base64")
                .logRequests(true)
                .logResponses(false) // embeddings are huge in logs
                .build();

        // when
        Response<Embedding> response = model.embed("Hello World");

        // then
        assertThat(response.content().dimension()).isEqualTo(model.dimension());

        assertThat(response.tokenUsage().inputTokenCount()).isNotNegative();
        assertThat(response.tokenUsage().outputTokenCount()).isNull();
        assertThat(response.tokenUsage().totalTokenCount()).isNotNegative();

        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_embed_multiple_segments() {

        // given
        EmbeddingModel model = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_3_LITE)
                .timeout(Duration.ofSeconds(60))
                .inputType("query")
                .logRequests(true)
                .logResponses(false) // embeddings are huge in logs
                .build();

        TextSegment segment1 = TextSegment.from("hello");
        TextSegment segment2 = TextSegment.from("hi");

        // when
        Response<List<Embedding>> response = model.embedAll(asList(segment1, segment2));

        // then
        assertThat(response.content()).hasSize(2);

        Embedding embedding1 = response.content().get(0);
        assertThat(embedding1.dimension()).isEqualTo(model.dimension());

        Embedding embedding2 = response.content().get(1);
        assertThat(embedding2.dimension()).isEqualTo(model.dimension());

        assertThat(CosineSimilarity.between(embedding1, embedding2)).isGreaterThan(0.8);

        assertThat(response.tokenUsage().inputTokenCount()).isNotNegative();
        assertThat(response.tokenUsage().outputTokenCount()).isNull();
        assertThat(response.tokenUsage().totalTokenCount()).isNotNegative();

        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_embed_any_number_of_segments() {

        // given
        EmbeddingModel model = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_3_LITE)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(false) // embeddings are huge in logs
                .maxSegmentsPerBatch(96)
                .build();

        List<TextSegment> segments = new ArrayList<>();
        int segmentCount = 97;
        for (int i = 0; i < segmentCount; i++) {
            segments.add(TextSegment.from("text"));
        }

        // when
        Response<List<Embedding>> response = model.embedAll(segments);

        // then
        assertThat(response.content()).hasSize(segmentCount);

        assertThat(response.tokenUsage().inputTokenCount()).isNotNegative();
        assertThat(response.tokenUsage().outputTokenCount()).isNull();
        assertThat(response.tokenUsage().totalTokenCount()).isNotNegative();

        assertThat(response.finishReason()).isNull();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"voyage-4-large", "voyage-4", "voyage-4-lite", "voyage-code-4", "voyage-3.5", "voyage-3.5-lite"})
    void should_embed_with_each_current_model_in_its_native_and_a_smaller_dimension(String modelName) {

        EmbeddingModel nativeModel = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(modelName)
                .build();
        EmbeddingModel smallModel = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(modelName)
                .dimensions(256)
                .build();

        assertThat(nativeModel.embed("Hello World").content().dimension()).isEqualTo(1024);
        assertThat(smallModel.embed("Hello World").content().dimension()).isEqualTo(256);
        assertThat(smallModel.dimension()).isEqualTo(256);
    }

    @Test
    void should_report_the_voyage_error_detail_for_an_unknown_model() {

        EmbeddingModel model = VoyageAiEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName("voyage-does-not-exist")
                .maxRetries(0)
                .build();

        assertThatThrownBy(() -> model.embed("Hello World"))
                .hasMessageContaining("detail")
                .hasMessageContaining("voyage-does-not-exist is not supported");
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        String ciDelaySeconds = System.getenv("CI_DELAY_SECONDS_VOYAGE_AI");
        if (ciDelaySeconds != null) {
            Thread.sleep(Integer.parseInt(ciDelaySeconds) * 1000L);
        }
    }
}
