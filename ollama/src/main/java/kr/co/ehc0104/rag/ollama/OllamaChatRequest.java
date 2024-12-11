package kr.co.ehc0104.rag.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.co.ehc0104.rag.ollama.chat.ChatHistory;
import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateOption;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OllamaChatRequest<T extends OllamaChatRequest<T>> implements CommonOllamaRequest{

    private static final String URL = "/api/chat";
    private static final String DEFAULT_MODEL = "llama3.1:70b";
    private static final String REQUEST_METHOD = "POST";

    private final List<ChatHistory> chatHistoryList = new ArrayList<>();
    private final ObjectMapper objectMapper;
    private final boolean stream;
    private final int historySize;
    private String model;
    private OllamaGenerateOption option;
    private String query;

    protected OllamaChatRequest(boolean stream,String query) {
        this(stream,query, 3);
    }

    protected OllamaChatRequest(boolean stream,String query, int historySize) {
        this.stream = stream;
        this.objectMapper = new ObjectMapper();
        this.query = query;
        if(historySize<3 || historySize>6) throw new IllegalArgumentException("historySize should be more than 3 and less than 6.");
        this.historySize = historySize;
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }


    protected abstract String prompt();


    /**
     * 채팅 기록 복원
     * @param pastChatHistory 채팅 기록
     * @return this
     * @throws Exception 해당 인스턴스에 chatHistoryList 가 비어있지않을떄
     */
    public T restoreHistory(List<ChatHistory> pastChatHistory) throws Exception {
        if(this.chatHistoryList.size()>0) {
            throw new Exception("chatHistoryList should be empty.");
        }
        this.chatHistoryList.addAll(pastChatHistory);
        return (T) this;
    }



    @Override
    public void sendRequest(OutputStream outputStream) {
        chatHistoryList.add(new ChatHistory(ChatHistory.Role.USER, prompt()));
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            dataOutputStream.write(toJsonString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void addAssistantMessage(String message) {
        chatHistoryList.add(new ChatHistory(ChatHistory.Role.ASSISTANT, message));
        if(chatHistoryList.size()/2 >= historySize) {
            chatHistoryList.remove(0);
            chatHistoryList.remove(0);
        }
    }

    protected List<ChatHistory> chatHistoryList() {
        return chatHistoryList;
    }

    protected abstract String getSystemPrompt();

    private String toJsonString() throws JsonProcessingException {
        Map<String, Object> requestMap = new HashMap<>();
        List<ChatHistory> messages = new ArrayList<>();
        messages.add(new ChatHistory(ChatHistory.Role.SYSTEM, getSystemPrompt()));
        messages.add(new ChatHistory(ChatHistory.Role.USER, ""));
        messages.addAll(chatHistoryList);
        requestMap.put("messages", messages);
        requestMap.put("model", model == null ? DEFAULT_MODEL : model);
        if (null != option) {
            requestMap.put("option", option);
        }
        requestMap.put("stream", stream);
        return objectMapper.writeValueAsString(requestMap);
    }

    /**
     * request 에 model 값 변경 (default llama3.1:70b)
     *
     * @param model 모델명
     * @return request
     */
    public T model(String model) {
        this.model = model;
        return (T) this;
    }


    /**
     * request 에 option 값 추가
     *
     * @param option option
     * @return request
     */
    public T option(OllamaGenerateOption option) {
        this.option = option;
        return (T) this;
    }

    /**
     * 사용자 쿼리 최신화
     * @param query 사용자 쿼리
     * @return request
     */
    public T pushQuery(String query) {
        this.query = query;
        return (T) this;
    }


    @Override
    public String requestMethod() {
        return REQUEST_METHOD;
    }

    @Override
    public String url() {
        return URL;
    }

    public String query() {
        return query;
    }

    public String model() {
        return model;
    }
}

