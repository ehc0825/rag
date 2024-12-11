package kr.co.ehc0104.rag.ollama.chat.common;

public class ChatStreamRequest extends ChatRequestCommon<ChatStreamRequest> {


    public ChatStreamRequest(String query) {
        super(true, query);
    }

    public ChatStreamRequest(String query, int historySize) {
        super(true, query, historySize);
    }

}
