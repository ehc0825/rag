package kr.co.ehc0104.rag.ollama.generate.common;

public class GenerateRequest extends GenerateRequestCommon<GenerateRequest>{

    /**
     * ollama 생성 request
     * @param systemPrompt 시스템 prompt
     * @param prompt 사용자 prompt
     */
    public GenerateRequest(String systemPrompt, String prompt) {
        super(false, systemPrompt, prompt);
    }

}
