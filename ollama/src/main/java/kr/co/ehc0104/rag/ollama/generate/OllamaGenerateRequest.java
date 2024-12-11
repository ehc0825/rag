package kr.co.ehc0104.rag.ollama.generate;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.HashMap;
import java.util.Map;

public abstract class OllamaGenerateRequest<T extends OllamaGenerateRequest<T>>{

    private static final String URL = "/api/generate";
    private static final String DEFAULT_MODEL = "llama3.1:70b";
    private static final String REQUEST_METHOD = "POST";
    private final ObjectMapper objectMapper;
    private final boolean stream;
    private Integer[] context;
    private OllamaGenerateOption option;
    private String model;

    protected OllamaGenerateRequest(boolean stream) {
        this.stream = stream;
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    protected abstract String prompt();

    protected abstract String systemPrompt();

    /**
     * ollama generate url
     * @return url String
     */
    public String url(){
        return URL;
    }

    /**
     * request 에 context 값 추가
     * @param context 이전대화 문맥
     * @return request
     */
    public T  context(Integer[] context) {
        this.context = context;
        return (T) this;
    }

    /**
     * request 에 option 값 추가
     * @param option option
     * @return request
     */
    public T option(OllamaGenerateOption option) {
        this.option = option;
        return (T) this;
    }

    /**
     * request 에 model 값 변경 (default llama3.1:70b)
     * @param model 모델명
     * @return request
     */
    public T model(String model) {
        this.model = model;
        return (T) this;
    }

    public String requestMethod(){
        return REQUEST_METHOD;
    }


    public String toJsonString() throws JsonProcessingException {
        Map<String,Object> request = new HashMap();
        if(null != model) {
            request.put("model", model);
        }
        else {
            request.put("model", DEFAULT_MODEL);
        }
        request.put("prompt", prompt());
        request.put("system",systemPrompt());
        request.put("stream", stream);
        if(null != context) {
            request.put("context" , context);
        }
        if(null != option) {
            request.put("option", option);
        }

        return objectMapper.writeValueAsString(request);
    }

}
