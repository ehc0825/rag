package kr.co.ehc0104.rag.nlp;

public class NlpTaskDeleteRequest extends NlpTaskRequestCommon{
    public NlpTaskDeleteRequest(String taskId) {
        super("DELETE", taskId);
    }
}
