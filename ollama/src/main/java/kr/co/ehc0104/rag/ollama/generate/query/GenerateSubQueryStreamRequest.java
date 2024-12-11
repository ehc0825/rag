package kr.co.ehc0104.rag.ollama.generate.query;

public class GenerateSubQueryStreamRequest extends GenerateSubQueryRequestCommon<GenerateSubQueryStreamRequest>{
    public GenerateSubQueryStreamRequest(String query) {
        super(true, query);
    }
}
