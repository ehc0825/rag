package kr.co.ehc0104.rag.nlp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.co.ehc0104.rag.common.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class NlpRequestCommon <B,R>{

    private static final String DEFAULT_REQUEST_METHOD = "POST";
    private final ObjectMapper objectMapper;
    private final Class<R> responseClass;
    private String requestMethod;

    protected NlpRequestCommon(String requestMethod,Class<R> responseClass) {
        this(responseClass);
        this.requestMethod = requestMethod;
    }


    protected NlpRequestCommon(Class<R> responseClass) {
        this.objectMapper = new ObjectMapper();
        this.responseClass = responseClass;
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    String requestMethod() {
        return Objects.requireNonNullElse(requestMethod, DEFAULT_REQUEST_METHOD);
    }

    abstract B requestBody();

    abstract String url();

    R response(InputStream inputStream) throws IOException {
        String responseStr = StreamUtil.inputStreamToString(inputStream);
        JavaType javaType = objectMapper.getTypeFactory().constructType(responseClass);
        return objectMapper.readValue(responseStr, javaType);
    }


    String toJsonString() {
        try {
            return objectMapper.writeValueAsString(requestBody());
        } catch (Exception ignored) {
            return "{}";
        }
    }

}
