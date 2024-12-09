package kr.co.ehc0104.rag.nlp;

import java.util.List;

public class ClusteringRequest extends NlpRequestCommon<ClusteringRequest.ClusterRequest, TaskId>{


    private final List<String> contents;
    private final NlpEmbeddingRequest.EmbeddingModel embeddingModel;
    protected ClusteringRequest(List<String> contents, NlpEmbeddingRequest.EmbeddingModel embeddingModel) {
        super("POST", TaskId.class);
        this.contents = contents;
        this.embeddingModel = embeddingModel;
    }

    @Override
    ClusterRequest requestBody() {
        return new ClusterRequest(contents);
    }

    @Override
    String url() {
        return "/cluster-documents/" + embeddingModel.value();
    }

    public record ClusterRequest(List<String> contents) {}
}