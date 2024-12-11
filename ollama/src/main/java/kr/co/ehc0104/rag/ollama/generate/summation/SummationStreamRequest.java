package kr.co.ehc0104.rag.ollama.generate.summation;

import java.util.List;

public class SummationStreamRequest extends SummationRequestCommon<SummationStreamRequest>{
    public SummationStreamRequest(String fileName, List<String> sentences) {
        super(true, fileName, sentences);
    }
    public SummationStreamRequest( String fileName,String paragraph) {
        super(true, fileName, paragraph);
    }
}
