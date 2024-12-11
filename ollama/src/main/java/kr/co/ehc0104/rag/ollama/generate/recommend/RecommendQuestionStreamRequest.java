package kr.co.ehc0104.rag.ollama.generate.recommend;

import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.List;

public class RecommendQuestionStreamRequest extends RecommendQuestionRequestCommon<RecommendQuestionStreamRequest>{

    /**
     * 추천 질문 stream request
     * @param contexts 문맥
     * @param query 질문
     * @param maxQuestions 최대 질문 수
     */
    public RecommendQuestionStreamRequest(List<Context> contexts, String query, int maxQuestions) {
        super(true, contexts, query, maxQuestions);
    }
}
