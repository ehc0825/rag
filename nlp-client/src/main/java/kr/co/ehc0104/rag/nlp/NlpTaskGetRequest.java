package kr.co.ehc0104.rag.nlp;

public class NlpTaskGetRequest extends NlpTaskRequestCommon{

    public NlpTaskGetRequest(String taskId) {
        super("GET", taskId);
    }

}

