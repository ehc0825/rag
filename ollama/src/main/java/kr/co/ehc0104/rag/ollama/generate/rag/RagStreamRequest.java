package kr.co.ehc0104.rag.ollama.generate.rag;

import java.util.List;

public class RagStreamRequest extends RagRequestCommon<RagStreamRequest> {

    /**
     * rag 생성 stream request
     * @param contexts 문맥
     * @param query 질문
     */
    public RagStreamRequest(List<Context> contexts, String query) {
        super(true, contexts, query);
    }
}
