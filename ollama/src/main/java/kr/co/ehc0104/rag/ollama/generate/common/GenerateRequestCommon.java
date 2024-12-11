package kr.co.ehc0104.rag.ollama.generate.common;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

public class GenerateRequestCommon<T extends GenerateRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private final String prompt;
    private final String systemPrompt;

    protected GenerateRequestCommon(boolean stream, String prompt, String systemPrompt) {
        super(stream);
        this.prompt = prompt;
        this.systemPrompt = systemPrompt;
    }

    @Override
    protected String prompt() {
        return prompt;
    }

    @Override
    protected String systemPrompt() {
        return systemPrompt;
    }
}