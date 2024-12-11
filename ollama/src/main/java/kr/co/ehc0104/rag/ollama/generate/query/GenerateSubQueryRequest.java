package kr.co.ehc0104.rag.ollama.generate.query;

public class GenerateSubQueryRequest extends GenerateSubQueryRequestCommon<GenerateSubQueryRequest>{
    public GenerateSubQueryRequest(String query) {
        super(false, query);
    }
}
