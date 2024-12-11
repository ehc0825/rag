package kr.co.ehc0104.rag.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.co.ehc0104.rag.common.StreamUtil;
import kr.co.ehc0104.rag.ollama.tag.OllamaModelTagResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OllamaModelTagRequest implements CommonOllamaRequest {

    private final ObjectMapper objectMapper;

    public OllamaModelTagRequest() {
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public String url() {
        return "/api/tags" ;
    }

    @Override
    public String requestMethod() {
        return "GET" ;
    }

    @Override
    public void sendRequest(OutputStream outputStream) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.write("".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected OllamaModelTagResponse response(InputStream inputStream) throws IOException {
        String responseString = StreamUtil.inputStreamToString(inputStream);
        return objectMapper.readValue(responseString, OllamaModelTagResponse.class);
    }
}
