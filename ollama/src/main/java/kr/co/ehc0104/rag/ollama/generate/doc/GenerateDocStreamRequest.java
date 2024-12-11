package kr.co.ehc0104.rag.ollama.generate.doc;

import java.util.List;

public class GenerateDocStreamRequest extends GenerateDocRequestCommon<GenerateDocStreamRequest> {

    /**
     * ollama stream 문서 생성 request
     * @param subject 주제
     * @param indexList 목차
     * @param hint 생성 목차에 대한 hint
     */
    public GenerateDocStreamRequest(String subject, List<String> indexList, Hint hint) {
        super(true, subject, indexList, hint);
    }
}
