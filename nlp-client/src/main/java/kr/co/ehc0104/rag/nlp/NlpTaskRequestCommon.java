package kr.co.ehc0104.rag.nlp;

public class NlpTaskRequestCommon extends NlpRequestCommon<Object, TaskStatus>{
    private final String taskId;
    protected NlpTaskRequestCommon(String requestMethod,String taskId) {
        super(requestMethod, TaskStatus.class);
        this.taskId = taskId;
    }

    @Override
    Object requestBody() {
        return new Object();
    }

    @Override
    String url() {
        return "/status/"+ taskId;
    }
}
