package kr.co.ehc0104.rag.ollama.generate.hypothentical;

public class HypotheticalDocRequest extends HypotheticalDocRequestCommon<HypotheticalDocRequest> {
    public HypotheticalDocRequest(int chunkSize, String query) {
        super( false, chunkSize, query);
    }
}
