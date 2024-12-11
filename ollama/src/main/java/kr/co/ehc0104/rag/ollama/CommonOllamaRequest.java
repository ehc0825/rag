package kr.co.ehc0104.rag.ollama;

import java.io.OutputStream;

public interface CommonOllamaRequest {

    String url();
    String requestMethod();
    void sendRequest(OutputStream outputStream);
}
