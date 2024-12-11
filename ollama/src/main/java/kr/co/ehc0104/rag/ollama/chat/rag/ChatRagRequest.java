package kr.co.ehc0104.rag.ollama.chat.rag;

import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.List;

public class ChatRagRequest extends ChatRagRequestCommon<ChatRagRequest>{

    public ChatRagRequest(List<Context> contexts, String query, int historySize) {
        super(false, contexts, query, historySize);
    }
    public ChatRagRequest(List<Context> contexts, String query) {
        super(false, contexts, query);
    }
}
