package kr.co.ehc0104.rag.ollama.generate.recommend;

import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.List;

public class RecommendQuestionRequest extends RecommendQuestionRequestCommon<RecommendQuestionRequest>{


    /**
     * 추천 질문 request
     * @param contexts 문맥
     * @param query 질문
     * @param maxQuestions 최대 질문 수
     */
    public RecommendQuestionRequest(List<Context> contexts, String query, int maxQuestions) {
        super(false, contexts, query, maxQuestions);
    }
}