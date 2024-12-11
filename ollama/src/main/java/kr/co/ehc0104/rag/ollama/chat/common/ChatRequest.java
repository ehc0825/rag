package kr.co.ehc0104.rag.ollama.chat.common;

public class ChatRequest extends ChatRequestCommon<ChatRequest> {

    public ChatRequest(String query) {
        super(false, query);
    }

    public ChatRequest(String query, int historySize) {
        super(false, query, historySize);
    }

}
