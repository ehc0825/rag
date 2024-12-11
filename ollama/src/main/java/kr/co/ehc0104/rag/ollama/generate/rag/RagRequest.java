package kr.co.ehc0104.rag.ollama.generate.rag;

import java.util.List;

public class RagRequest extends RagRequestCommon<RagRequest>{

    /**
     * rag 생성 request
     * @param contexts 문맥
     * @param query 질문
     */
    public RagRequest(List<Context> contexts, String query) {
        super(false, contexts, query);
    }

}
