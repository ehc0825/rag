package kr.co.ehc0104.rag.ollama;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.co.ehc0104.rag.common.StreamUtil;
import kr.co.ehc0104.rag.ollama.embedding.OllamaEmbeddingResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OllamaEmbeddingRequest implements CommonOllamaRequest{

    private static final String REQUEST_METHOD = "POST";
    private static final String DEFAULT_MODEL = "bge-m3";
    private final ObjectMapper objectMapper;

    private final String prompt;
    private String model;

    public OllamaEmbeddingRequest(String prompt) {
        this.prompt = prompt;
        this.model = DEFAULT_MODEL;
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }


    public OllamaEmbeddingRequest model(String model) {
        this.model = model;
        return this;
    }

    @Override
    public String url(){
        return "/api/embeddings";
    }

    @Override
    public String requestMethod() {
        return REQUEST_METHOD;
    }

    @Override
    public void sendRequest(OutputStream outputStream) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.write(requestBodyString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String requestBodyString(){
        try {
            return objectMapper.writeValueAsString(new Embedding(this.model,this.prompt));
        } catch (Exception ignored) {
            return "{}";
        }
    }

    protected OllamaEmbeddingResponse response(InputStream inputStream) throws IOException {
        String responseString = StreamUtil.inputStreamToString(inputStream);
        return objectMapper.readValue(responseString, OllamaEmbeddingResponse.class);
    }

    public record Embedding(String model, String prompt){}



}