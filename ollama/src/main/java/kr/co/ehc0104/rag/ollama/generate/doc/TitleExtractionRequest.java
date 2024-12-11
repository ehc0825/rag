package kr.co.ehc0104.rag.ollama.generate.doc;


public class TitleExtractionRequest extends TitleExtractionRequestCommon<TitleExtractionRequest>{

    /**
     * ollama 목차 분리 request
     * @param query 사용자 요청문
     */
    public TitleExtractionRequest(String query) {
        super(false, query);
    }

}
