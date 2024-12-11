package kr.co.ehc0104.rag.ollama.generate.hypothentical;

public class HypotheticalDocStreamRequest extends HypotheticalDocRequestCommon<HypotheticalDocStreamRequest> {
    public HypotheticalDocStreamRequest(int chunkSize, String query) {
        super(true, chunkSize, query);
    }
}
