package kr.co.ehc0104.rag.nlp;

public class TaskResultRequest <B> extends NlpRequestCommon<Object,B>{

    private final String taskId;

    protected TaskResultRequest(String taskId, Class<B> responseClass) {
        super("GET", responseClass);
        this.taskId = taskId;
    }

    @Override
    Object requestBody() {
        return new Object();
    }

    @Override
    String url() {
        return "/task/result/"+ taskId;
    }
}
