package kr.co.ehc0104.rag.nlp;

public class NlpChunkRequest extends NlpRequestCommon<NlpChunkRequest.Content, TaskId>{

    private final String content;

    public NlpChunkRequest(String content) {
        super("POST", TaskId.class);
        this.content = content;
    }

    @Override
    Content requestBody() {
        return new Content(content);
    }

    @Override
    String url() {
        return "/chunk";
    }

    public record Content(String content) { }

}
