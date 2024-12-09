package kr.co.ehc0104.rag.nlp;

public record TaskStatus(String taskId, String status) {


    public boolean isDone() throws Exception {
        if (status.contains("Failed")) {
            throw new Exception("Task failed: " + taskId + "cause:" + status);
        }
        return status.contains("Completed");

    }
}
