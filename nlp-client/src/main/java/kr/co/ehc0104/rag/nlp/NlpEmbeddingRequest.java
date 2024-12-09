package kr.co.ehc0104.rag.nlp;


public class NlpEmbeddingRequest extends NlpRequestCommon<NlpEmbeddingRequest.Content, NlpEmbeddingResponse> {

    private final Content content;
    private final EmbeddingModel embeddingModel;

    /**
     * NlpEmbeddingRequest
     * @param content 임베딩 대상 콘텐츠
     * @param embeddingModel 임베딩 모델
     */
    public NlpEmbeddingRequest(String content,EmbeddingModel embeddingModel) {
        super(NlpEmbeddingResponse.class);
        this.content = new Content(content);
        this.embeddingModel = embeddingModel;
    }

    @Override
    Content requestBody() {
        return content;
    }

    @Override
    String url() {
        return "/embedding/" +embeddingModel.value;
    }

    public record Content(String content) {
    }

    public enum EmbeddingModel {
        KO("ko"),ROBERTA("ko_roberta"),SORBERTA("ko_sroberta");

        private final String value;

        public String value() {
            return value;
        }

        EmbeddingModel(String value) {
            this.value = value;
        }
    }
}
