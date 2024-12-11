package kr.co.ehc0104.rag.ollama.chat.common;


import kr.co.ehc0104.rag.ollama.OllamaChatRequest;

public class ChatRequestCommon <T extends ChatRequestCommon<T>> extends OllamaChatRequest<T> {

    protected String systemPrompt = "You are kind assistant. You must be given in the language requested by the user.";

    protected ChatRequestCommon(boolean stream, String query) {
        super(stream, query);
    }

    protected ChatRequestCommon(boolean stream, String query, int historySize) {
        super(stream, query, historySize);
    }

    @Override
    protected String prompt() {
        return query();
    }

    @Override
    protected String getSystemPrompt() {
        return systemPrompt;
    }

    public T setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
        return (T) this;
    }


}
