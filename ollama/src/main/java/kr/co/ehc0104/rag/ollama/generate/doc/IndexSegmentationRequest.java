package kr.co.ehc0104.rag.ollama.generate.doc;

public class IndexSegmentationRequest extends IndexSegmentationRequestCommon<IndexSegmentationRequest>{

    /**
     * ollama 목차 분리 request
     * @param subject 주제
     * @param query 사용자 요청문
     */
    public IndexSegmentationRequest(String subject, String query) {
        super(false, subject, query);
    }

}
