package kr.co.ehc0104.rag.nlp;

import java.util.List;

public class NlpReRankRequest extends NlpRequestCommon<NlpReRankRequest.RequestContext, NlpReRankResponse> {

    private final RequestContext content;
    private final ReRankerType reRankerType;

    /**
     * re-rank 요청
     * @param contexts 문맥
     * @param question 질문
     * @param reRankerType re-ranker 타입
     */
    public NlpReRankRequest(List<String> contexts, String question, ReRankerType reRankerType) {
        super(NlpReRankResponse.class);
        this.content = new RequestContext(contexts, question);
        this.reRankerType = reRankerType;
    }


    @Override
    RequestContext requestBody() {
        return content;
    }

    @Override
    String url() {
        return "/re-rank/" + reRankerType.value;
    }


    public record RequestContext(List<String> contexts, String question) {
    }

    public enum ReRankerType {
        GEMMA("bge_gemma_reranker"),M3("bge_m3_reranker"),KO_RERANKER("ko_reranker");

        private final String value;

        ReRankerType(String value) {
            this.value = value;
        }
    }
}
