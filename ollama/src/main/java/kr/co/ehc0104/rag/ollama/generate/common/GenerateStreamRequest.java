package kr.co.ehc0104.rag.ollama.generate.common;

public class GenerateStreamRequest extends GenerateRequestCommon<GenerateStreamRequest> {


    /**
     * ollama 생성(stream) request
     * @param systemPrompt 시스템 prompt
     * @param prompt 사용자 prompt
     */
    public GenerateStreamRequest(String systemPrompt, String prompt) {
        super(true, systemPrompt, prompt);
    }

}
