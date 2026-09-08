package dev.langchain4j.model.voyageai;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The contextualized embeddings endpoint accepts {@code inputs} as {@code Union[List[List[str]], List[str]]}
 * (see <a href="https://docs.voyageai.com/docs/contextualized-chunk-embeddings">the spec</a>). Both shapes must
 * serialize verbatim. Values below are space-free so the assertions can ignore pretty-print whitespace.
 */
class ContextualizedEmbeddingRequestTest {

    private static String compactJson(ContextualizedEmbeddingRequest request) {
        return VoyageAiJsonUtils.toJson(request).replaceAll("\\s", "");
    }

    @Test
    void should_serialize_chunked_inputs_as_list_of_lists() {
        ContextualizedEmbeddingRequest request = ContextualizedEmbeddingRequest.builder()
                .chunkedInputs(List.of(asList("chunkA", "chunkB")))
                .model("voyage-context-4")
                .inputType("document")
                .build();

        String json = compactJson(request);

        assertThat(json).contains("\"inputs\":[[\"chunkA\",\"chunkB\"]]");
        assertThat(json).contains("\"model\":\"voyage-context-4\"");
        assertThat(json).contains("\"input_type\":\"document\"");
    }

    @Test
    void should_serialize_flat_inputs_as_list_of_strings() {
        ContextualizedEmbeddingRequest request = ContextualizedEmbeddingRequest.builder()
                .flatInputs(asList("documentA", "documentB"))
                .model("voyage-context-4")
                .build();

        String json = compactJson(request);

        assertThat(json).contains("\"inputs\":[\"documentA\",\"documentB\"]");
    }
}
