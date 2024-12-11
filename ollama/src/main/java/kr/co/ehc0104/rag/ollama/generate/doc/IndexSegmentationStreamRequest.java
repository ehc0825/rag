package kr.co.ehc0104.rag.ollama.generate.doc;

public class IndexSegmentationStreamRequest extends IndexSegmentationRequestCommon<IndexSegmentationStreamRequest> {

    /**
     * ollama stream 목차 분리 request
     * @param subject 주제
     * @param query 사용자 요청문
     */
    public IndexSegmentationStreamRequest(String subject, String query) {
        super(true, subject, query);
    }
}
