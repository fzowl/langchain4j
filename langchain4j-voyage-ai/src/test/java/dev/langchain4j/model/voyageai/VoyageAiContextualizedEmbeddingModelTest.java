package dev.langchain4j.model.voyageai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.exception.InternalServerException;
import dev.langchain4j.http.client.MockHttpClient;
import dev.langchain4j.http.client.MockHttpClientBuilder;
import dev.langchain4j.http.client.SuccessfulHttpResponse;
import dev.langchain4j.model.output.Response;
import java.util.List;
import org.junit.jupiter.api.Test;

class VoyageAiContextualizedEmbeddingModelTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // results and chunks deliberately out of order: the model must restore the input order
    private static final String TWO_DOCUMENTS_RESPONSE = """
            {"object":"list","data":[
              {"object":"list","index":1,"data":[{"object":"embedding","index":0,"text":"B","embedding":[0.3,0.4]}]},
              {"object":"list","index":0,"data":[
                {"object":"embedding","index":1,"text":"A2","embedding":[0.1,0.1]},
                {"object":"embedding","index":0,"text":"A1","embedding":[0.2,0.2]}]}],
             "model":"voyage-context-4","usage":{"total_tokens":9},"chunker_version":"1.0.0"}""";

    private static MockHttpClient responding(String body) {
        return new MockHttpClient(
                SuccessfulHttpResponse.builder().statusCode(200).body(body).build());
    }

    private static VoyageAiContextualizedEmbeddingModel.Builder builder(MockHttpClient mock) {
        return VoyageAiContextualizedEmbeddingModel.builder()
                .httpClientBuilder(new MockHttpClientBuilder(mock))
                .baseUrl("https://api.voyageai.com/v1")
                .apiKey("test-api-key")
                .modelName(VoyageAiContextualizedEmbeddingModelName.VOYAGE_CONTEXT_4)
                .maxRetries(0);
    }

    private static JsonNode body(MockHttpClient mock) throws Exception {
        return MAPPER.readTree(mock.request().body());
    }

    @Test
    void should_send_flat_documents_with_auto_chunking() throws Exception {
        MockHttpClient mock = responding(TWO_DOCUMENTS_RESPONSE);

        builder(mock).build().embedDocuments(List.of("doc A", "doc B"));

        assertThat(mock.request().url()).isEqualTo("https://api.voyageai.com/v1/contextualizedembeddings");
        JsonNode body = body(mock);
        assertThat(body.get("inputs")).hasSize(2);
        assertThat(body.get("inputs").get(0).isTextual()).isTrue(); // flat list[str], never list[list[str]]
        assertThat(body.get("model").textValue()).isEqualTo("voyage-context-4");
        assertThat(body.get("input_type").textValue()).isEqualTo("document");
        assertThat(body.get("enable_auto_chunking").booleanValue()).isTrue();
        assertThat(body.get("chunk_size").intValue()).isEqualTo(32_000);
        assertThat(body.has("output_dimension")).isFalse();
    }

    @Test
    void should_return_the_chunks_of_each_document_in_order() {
        Response<List<List<VoyageAiContextualizedEmbeddingModel.Chunk>>> response =
                builder(responding(TWO_DOCUMENTS_RESPONSE)).build().embedDocuments(List.of("doc A", "doc B"));

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0))
                .extracting(VoyageAiContextualizedEmbeddingModel.Chunk::text)
                .containsExactly("A1", "A2");
        assertThat(response.content().get(0).get(0).embedding().vector()).containsExactly(0.2f, 0.2f);
        assertThat(response.content().get(1))
                .extracting(VoyageAiContextualizedEmbeddingModel.Chunk::text)
                .containsExactly("B");
        assertThat(response.tokenUsage().totalTokenCount()).isEqualTo(9);
    }

    @Test
    void should_send_queries_without_auto_chunking() throws Exception {
        MockHttpClient mock = responding("""
                {"data":[{"index":0,"data":[{"index":0,"embedding":[0.1,0.2]}]}],
                 "model":"voyage-context-4","usage":{"total_tokens":2}}""");

        Response<List<dev.langchain4j.data.embedding.Embedding>> response =
                builder(mock).dimensions(256).build().embedQueries(List.of("what?"));

        JsonNode body = body(mock);
        assertThat(body.get("input_type").textValue()).isEqualTo("query");
        assertThat(body.has("enable_auto_chunking")).isFalse();
        assertThat(body.has("chunk_size")).isFalse();
        assertThat(body.get("output_dimension").intValue()).isEqualTo(256);
        assertThat(response.content()).hasSize(1);
    }

    @Test
    void should_split_documents_into_requests_of_max_documents_per_batch() throws Exception {
        MockHttpClient mock = responding("""
                {"data":[{"index":0,"data":[{"index":0,"text":"x","embedding":[0.1]}]},
                         {"index":1,"data":[{"index":0,"text":"y","embedding":[0.2]}]}],
                 "model":"voyage-context-4","usage":{"total_tokens":1}}""");

        Response<List<List<VoyageAiContextualizedEmbeddingModel.Chunk>>> response =
                builder(mock).maxDocumentsPerBatch(2).build().embedDocuments(List.of("1", "2", "3", "4"));

        assertThat(mock.requests()).hasSize(2);
        assertThat(MAPPER.readTree(mock.requests().get(1).body()).get("inputs")).hasSize(2);
        assertThat(response.content()).hasSize(4);
        assertThat(response.tokenUsage().totalTokenCount()).isEqualTo(2);
    }

    @Test
    void should_fail_when_the_number_of_results_does_not_match_the_number_of_inputs() {
        assertThatThrownBy(() ->
                        builder(responding(TWO_DOCUMENTS_RESPONSE)).build().embedDocuments(List.of("only one")))
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("expected 1 results, but got 2");
    }

    @Test
    void should_fail_when_a_query_yields_more_than_one_embedding() {
        assertThatThrownBy(() ->
                        builder(responding(TWO_DOCUMENTS_RESPONSE)).build().embedQueries(List.of("q1", "q2")))
                .isInstanceOf(InternalServerException.class)
                .hasMessageContaining("expected one embedding per query");
    }

    @Test
    void should_reject_invalid_configuration_and_input() {
        MockHttpClient mock = new MockHttpClient();

        assertThatThrownBy(() -> builder(mock).modelName((String) null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("modelName");
        assertThatThrownBy(() -> builder(mock).maxDocumentsPerBatch(1001).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000");
        assertThatThrownBy(() -> builder(mock).build().embedDocuments(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("documents");
    }
}
