package kr.co.ehc0104.rag.nlp;

public class NlpSummarizeRequest extends NlpRequestCommon<NlpSummarizeRequest.Content, NlpSummarizeResponse>{


    private final Content content;

    protected NlpSummarizeRequest(String content) {
        super(NlpSummarizeResponse.class);
        this.content = new Content(content);
    }

    @Override
    Content requestBody() {
        return this.content;
    }

    @Override
    String url() {
        return "/summarize";
    }

    public record Content(String content) {}
}
