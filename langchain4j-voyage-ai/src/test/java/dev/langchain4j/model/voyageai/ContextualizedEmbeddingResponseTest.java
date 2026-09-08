package dev.langchain4j.model.voyageai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * The contextualized embeddings endpoint nests per-chunk embeddings inside a per-document {@code data} list.
 */
class ContextualizedEmbeddingResponseTest {

    @Test
    void should_read_nested_document_and_chunk_embeddings() {
        ContextualizedEmbeddingResponse response =
                VoyageAiJsonUtils.fromJson("""
                {"object":"list","model":"voyage-context-4","usage":{"total_tokens":42},"data":[
                  {"object":"list","index":0,"data":[
                    {"object":"embedding","embedding":[1.1,2.2],"index":0},
                    {"object":"embedding","embedding":[3.3,4.4],"index":1}]}]}""", ContextualizedEmbeddingResponse.class);

        assertThat(response.getModel()).isEqualTo("voyage-context-4");
        assertThat(response.getUsage().getTotalTokens()).isEqualTo(42);
        assertThat(response.getData()).hasSize(1);

        ContextualizedEmbeddingResponse.DocumentData document =
                response.getData().get(0);
        assertThat(document.getIndex()).isZero();
        assertThat(document.getData()).hasSize(2);
        assertThat(document.getData().get(0).getEmbedding()).containsExactly(1.1f, 2.2f);
        assertThat(document.getData().get(1).getEmbedding()).containsExactly(3.3f, 4.4f);
    }
}
