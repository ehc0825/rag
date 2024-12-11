package kr.co.ehc0104.rag.ollama.chat.rag;

import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.List;

public class ChatRagStreamRequest extends ChatRagRequestCommon<ChatRagStreamRequest> {

    public ChatRagStreamRequest(List<Context> contexts, String query, int historySize) {
        super(true, contexts, query, historySize);
    }
    public ChatRagStreamRequest(List<Context> contexts, String query) {
        super(true, contexts, query);
    }
}
