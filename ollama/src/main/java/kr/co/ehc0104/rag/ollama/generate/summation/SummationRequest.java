package kr.co.ehc0104.rag.ollama.generate.summation;

import java.util.List;

public class SummationRequest extends SummationRequestCommon<SummationRequest>{
    public SummationRequest(String fileName, List<String> sentences) {
        super(false, fileName, sentences);
    }
    public SummationRequest( String fileName,String paragraph) {
        super(false, fileName, paragraph);
    }
}
