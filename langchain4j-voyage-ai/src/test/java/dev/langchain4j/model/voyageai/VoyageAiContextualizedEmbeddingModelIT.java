package dev.langchain4j.model.voyageai;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "VOYAGE_API_KEY", matches = ".+")
class VoyageAiContextualizedEmbeddingModelIT {

    @Test
    void should_embed_single_chunk() {

        EmbeddingModel model = VoyageAiContextualizedEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_CONTEXT_4)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(false) // embeddings are huge in logs
                .build();

        Response<Embedding> response = model.embed("Hello World");

        assertThat(response.content().dimension()).isEqualTo(model.dimension());
        assertThat(response.tokenUsage().inputTokenCount()).isNotNegative();
        assertThat(response.tokenUsage().totalTokenCount()).isNotNegative();
        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_embed_multiple_chunks_of_a_document() {

        EmbeddingModel model = VoyageAiContextualizedEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_CONTEXT_4)
                .timeout(Duration.ofSeconds(60))
                .inputType("document")
                .logRequests(true)
                .logResponses(false)
                .build();

        TextSegment chunk1 = TextSegment.from("The Eiffel Tower is located in Paris.");
        TextSegment chunk2 = TextSegment.from("It is one of the most visited monuments in the world.");

        Response<List<Embedding>> response = model.embedAll(asList(chunk1, chunk2));

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).dimension()).isEqualTo(model.dimension());
        assertThat(response.content().get(1).dimension()).isEqualTo(model.dimension());
    }

    @Test
    void should_embed_separate_documents_with_flat_inputs() {

        EmbeddingModel model = VoyageAiContextualizedEmbeddingModel.builder()
                .apiKey(System.getenv("VOYAGE_API_KEY"))
                .modelName(VoyageAiEmbeddingModelName.VOYAGE_CONTEXT_4)
                .timeout(Duration.ofSeconds(60))
                .inputType("document")
                .separateDocuments(true)
                .logRequests(true)
                .logResponses(false)
                .build();

        TextSegment doc1 = TextSegment.from("The Eiffel Tower is located in Paris.");
        TextSegment doc2 = TextSegment.from("The Colosseum is located in Rome.");

        Response<List<Embedding>> response = model.embedAll(asList(doc1, doc2));

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).dimension()).isEqualTo(model.dimension());
        assertThat(response.content().get(1).dimension()).isEqualTo(model.dimension());
    }
}
