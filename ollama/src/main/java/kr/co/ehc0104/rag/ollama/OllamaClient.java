package kr.co.ehc0104.rag.ollama;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ehc0104.rag.common.StreamUtil;
import kr.co.ehc0104.rag.common.StringParseUtil;
import kr.co.ehc0104.rag.ollama.chat.common.ChatRequest;
import kr.co.ehc0104.rag.ollama.chat.common.ChatStreamRequest;
import kr.co.ehc0104.rag.ollama.chat.rag.ChatRagRequest;
import kr.co.ehc0104.rag.ollama.chat.rag.ChatRagStreamRequest;
import kr.co.ehc0104.rag.ollama.embedding.OllamaEmbeddingResponse;
import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateOption;
import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;
import kr.co.ehc0104.rag.ollama.generate.common.GenerateRequest;
import kr.co.ehc0104.rag.ollama.generate.common.GenerateStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.doc.*;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocRequest;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocRequestCommon;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.query.GenerateSubQueryRequest;
import kr.co.ehc0104.rag.ollama.generate.query.GenerateSubQueryStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.RagRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.RagStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionRequest;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionRequestCommon;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.summation.SummationRequest;
import kr.co.ehc0104.rag.ollama.generate.summation.SummationStreamRequest;
import kr.co.ehc0104.rag.ollama.tag.OllamaModelTagResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class OllamaClient {

    public static final String CONTEXTS_KEY = "contexts";
    public static final String MESSAGE_KEY = "message";
    public static final String DONE_KEY = "done";
    public static final String HISTORY_KEY = "history";
    public static final String RESPONSE_KEY = "response";
    public static final String CONTENT_KEY = "content";

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * ollama 연결 client
     *
     * @param host ollama host 주소
     * @param port ollama port
     */
    public OllamaClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 모델 테그 목록조회
     * @return
     * @throws IOException
     */
    public OllamaModelTagResponse modelTags(OllamaModelTagRequest modelTagRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getOllamaInputStream(modelTagRequest);
            return modelTagRequest.response(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    /**
     * ollama 임베딩 요청
     * @param ollamaEmbeddingRequest ollama embedding 요청
     * @return ollama 임베딩 응답
     * @throws IOException  ollama 서버 오류 발생
     */
    public OllamaEmbeddingResponse embedding(OllamaEmbeddingRequest ollamaEmbeddingRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getOllamaInputStream(ollamaEmbeddingRequest);
            return ollamaEmbeddingRequest.response(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * ollama chat 요청
     * @param chatRequest chat 요청
     * @return chat 응답
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String,Object> chat(ChatRequest chatRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getOllamaInputStream(chatRequest);
            Map<String, Object> ragResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            chatRequest.addAssistantMessage(getChatResponseContent(ragResponse));
            ragResponse.put(HISTORY_KEY, chatRequest.chatHistoryList());
            return ragResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * ollama stream chat 요청
     * @param chatStreamRequest chat 요청
     * @return chat 응답
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String,Object>> chatAsStream(ChatStreamRequest chatStreamRequest) throws IOException {
        InputStream inputStream = getOllamaInputStream(chatStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseMessageBuilder = new StringBuilder();
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {});
                        responseMessageBuilder.append(getChatResponseContent(responseMap));
                        if (isFinalResponse(responseMap)) {
                            chatStreamRequest.addAssistantMessage(responseMessageBuilder.toString());
                            responseMap.put(HISTORY_KEY, chatStreamRequest.chatHistoryList());
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * rag 채팅 응답
     *
     * @param chatRagRequest rag 채팅 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> ragChat(ChatRagRequest chatRagRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getOllamaInputStream(chatRagRequest);
            Map<String, Object> ragResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            ragResponse.put(CONTEXTS_KEY, chatRagRequest.contexts());
            chatRagRequest.addAssistantMessage(getChatResponseContent(ragResponse));
            ragResponse.put(HISTORY_KEY, chatRagRequest.chatHistoryList());
            return ragResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * rag 채팅 응답 stream
     *
     * @param chatRagStreamRequest rag stream 채팅 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> ragChatAsStream(ChatRagStreamRequest chatRagStreamRequest) throws IOException {
        InputStream inputStream = getOllamaInputStream(chatRagStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseMessageBuilder = new StringBuilder();
        AtomicBoolean isFirst = new AtomicBoolean(true);
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        responseMessageBuilder.append(getChatResponseContent(responseMap));
                        if (isFirst.get()) {
                            responseMap.put(CONTEXTS_KEY, chatRagStreamRequest.contexts());
                            isFirst.set(false);
                        }
                        if (isFinalResponse(responseMap)) {
                            chatRagStreamRequest.addAssistantMessage(responseMessageBuilder.toString());
                            responseMap.put(HISTORY_KEY, chatRagStreamRequest.chatHistoryList());
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Stream<Map<String, Object>> mockChatResponseAsStream(OllamaChatRequest ollamaChatRequest,
                                                                String mockMessage) {
        return Stream.concat(
                mockMessage.chars()
                        .mapToObj(c -> (char) c)
                        .map(character -> mockChatResponse(
                                ollamaChatRequest,
                                String.valueOf(character), false)),
                Stream.of(mockChatResponse(ollamaChatRequest, "", true))
        );
    }

    private Map<String, Object> mockChatResponse(OllamaChatRequest ollamaChatRequest,
                                                 String mockMessage, boolean isFinalResponse) {
        Map<String, Object> response = new HashMap<>();
        response.put("created_at",
                DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.NANOS)));
        response.put("model", ollamaChatRequest.model());
        response.put(DONE_KEY, isFinalResponse);
        Map<String,Object> message = new HashMap<>();
        message.put("content", mockMessage);
        message.put("role", "assistant");
        response.put("message", message);
        return response;
    }

    public Map<String, Object> mockChatResponse(ChatRagRequest chatRagRequest, String mockMessage) {
        return mockChatResponse(chatRagRequest, mockMessage, true);
    }


    /**
     * 가정 문서 생성
     *
     * @param hypotheticalDocRequest 가정문서 생성 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> generateHypotheticalDocument(HypotheticalDocRequest hypotheticalDocRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(hypotheticalDocRequest);
            Map<String, Object> ollamaResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            List<String> documents = StringParseUtil.segmentationStrToList((String) ollamaResponse.get(RESPONSE_KEY),
                    HypotheticalDocRequestCommon.DOCUMENT_REGEX_PATTERN);
            if (documents.isEmpty()) {
                documents.add(ollamaResponse.get(RESPONSE_KEY).toString());
            }
            ollamaResponse.put(HypotheticalDocRequestCommon.HYPOTHETICAL_DOCUMENT_FIELD_NAME,
                    documents);
            return ollamaResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * stream 가정 문서 생성
     *
     * @param hypotheticalDocStreamRequest 가정문서 생성 요청
     * @return stream response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> generateHypotheticalDocumentAsStream
    (HypotheticalDocStreamRequest hypotheticalDocStreamRequest) throws IOException {
        InputStream inputStream = getGenerateInputStream(hypotheticalDocStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        if (isFinalResponse(responseMap)) {
                            responseMap.put(HypotheticalDocRequestCommon.HYPOTHETICAL_DOCUMENT_FIELD_NAME,
                                    StringParseUtil.segmentationStrToList(stringBuffer.toString(),
                                            HypotheticalDocRequestCommon.DOCUMENT_REGEX_PATTERN));
                        } else {
                            stringBuffer.append(responseMap.get(RESPONSE_KEY));
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * rag 생성 응답
     *
     * @param ragRequest rag 생성 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> rag(RagRequest ragRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(ragRequest);
            Map<String, Object> ragResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            ragResponse.put(CONTEXTS_KEY, ragRequest.contexts());
            return ragResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * rag 생성 응답 stream
     *
     * @param ragStreamRequest rag stream 생성 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> ragAsStream(RagStreamRequest ragStreamRequest) throws IOException {
        InputStream inputStream = getGenerateInputStream(ragStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        AtomicBoolean isFirst = new AtomicBoolean(true);
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        if (isFirst.get()) {
                            responseMap.put(CONTEXTS_KEY, ragStreamRequest.contexts());
                            isFirst.set(false);
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * 생성 응답
     *
     * @param generateRequest 생성 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> generate(GenerateRequest generateRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(generateRequest);
            return objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * stream 으로 생성
     *
     * @param generateStreamRequest 생성 요청
     * @return Stream response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> generateAsStream(GenerateStreamRequest generateStreamRequest) throws IOException {
        return convertInputStreamToStream(getGenerateInputStream(generateStreamRequest));
    }


    /**
     * stream 목차 분리
     *
     * @param indexSegmentationStreamRequest 목차 분리 요청
     * @return stream response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> indexSegmentationAsStream(IndexSegmentationStreamRequest indexSegmentationStreamRequest)
            throws IOException {
        InputStream inputStream = getGenerateInputStream(indexSegmentationStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        if (isFinalResponse(responseMap)) {
                            responseMap.put(IndexSegmentationRequestCommon.INDEX_LIST_FIELD_NAME,
                                    StringParseUtil.segmentationStrToList(stringBuffer.toString(),
                                            IndexSegmentationRequestCommon.INDEX_REGEX_PATTERN));
                        } else {
                            stringBuffer.append(responseMap.get(RESPONSE_KEY));
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 목차 분리
     *
     * @param indexSegmentationRequest 목차 분리 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> indexSegmentation(IndexSegmentationRequest indexSegmentationRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(indexSegmentationRequest);
            Map<String, Object> ollamaResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            ollamaResponse.put(IndexSegmentationRequestCommon.INDEX_LIST_FIELD_NAME,
                    StringParseUtil.segmentationStrToList((String) ollamaResponse.get(RESPONSE_KEY),
                            IndexSegmentationRequestCommon.INDEX_REGEX_PATTERN));
            return ollamaResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * stream 으로 추천 질문
     * @param recommendQuestionStreamRequest 추천 질문 요청
     * @return Stream response (Map)
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String,Object>> recommendQuestionAsStream
    (RecommendQuestionStreamRequest recommendQuestionStreamRequest) throws IOException {
        InputStream inputStream = getGenerateInputStream(recommendQuestionStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        if (isFinalResponse(responseMap)) {
                            List<String> questionList = StringParseUtil.segmentationStrToList(stringBuffer.toString(),
                                    RecommendQuestionRequestCommon.QUESTION_REGEX_PATTERN);
                            int index =0;
                            for(String question : questionList) {
                                question = question.replaceAll("@","");
                                questionList.set(index,question);
                                index++;
                            }
                            if(questionList.size() > recommendQuestionStreamRequest.getMaxQuestions()) {
                                responseMap.put(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME,
                                        questionList.subList(0,recommendQuestionStreamRequest.getMaxQuestions()));
                            }
                            else {
                                responseMap.put(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME,questionList);
                            }
                        } else {
                            stringBuffer.append(responseMap.get(RESPONSE_KEY));
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * 추천 질문
     * @param recommendQuestionRequest 추천 질문 요청
     * @return map response (RecommendQuestionRequest.QUESTION_LIST_FIELD_NAME : List<String>)
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String,Object> recommendQuestion(RecommendQuestionRequest recommendQuestionRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(recommendQuestionRequest);
            Map<String, Object> ollamaResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            List<String> questionList = StringParseUtil.segmentationStrToList((String) ollamaResponse.get(RESPONSE_KEY),
                    RecommendQuestionRequestCommon.QUESTION_REGEX_PATTERN);
            int index =0;
            for(String question : questionList) {
                question = question.replaceAll("@","");
                questionList.set(index,question);
                index++;
            }
            if(questionList.size() > recommendQuestionRequest.getMaxQuestions()) {
                ollamaResponse.put(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME,
                        questionList.subList(0,recommendQuestionRequest.getMaxQuestions()));
            }
            else {
                ollamaResponse.put(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME,questionList);
            }
            return ollamaResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * stream 사용자 질의 분리
     *
     * @param generateSubQueryStreamRequest 목차 분리 요청
     * @return stream response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> decompositionQueryAsStream(GenerateSubQueryStreamRequest generateSubQueryStreamRequest)
            throws IOException {
        InputStream inputStream = getGenerateInputStream(generateSubQueryStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(line, new TypeReference<>() {
                        });
                        if (isFinalResponse(responseMap)) {
                            responseMap.put(GenerateSubQueryStreamRequest.SUB_QUERY_FIELD_NAME,
                                    StringParseUtil.segmentationStrToList(stringBuffer.toString(),
                                            GenerateSubQueryStreamRequest.SUB_QUERY_REGEX_PATTERN));
                        } else {
                            stringBuffer.append(responseMap.get(RESPONSE_KEY));
                        }
                        return responseMap;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * 사용자 질의 분리
     *
     * @param generateSubQueryRequest 질의 분리 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> decompositionQuery(GenerateSubQueryRequest generateSubQueryRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(generateSubQueryRequest);
            Map<String, Object> ollamaResponse = objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
            ollamaResponse.put(GenerateSubQueryRequest.SUB_QUERY_FIELD_NAME,
                    StringParseUtil.segmentationStrToList((String) ollamaResponse.get(RESPONSE_KEY),
                            GenerateSubQueryRequest.SUB_QUERY_REGEX_PATTERN));
            return ollamaResponse;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 사용자 질의 분리
     * @param query 사용자 질의
     * @return 분리된 질의 리스트
     * @throws IOException ollama 서버 오류 발생
     */
    public List<String> getSubQuery(String query) throws IOException {
        return getSubQuery(query, null, null);
    }

    /**
     * 사용자 질의 분리
     * @param query 사용자 질의
     * @param model 모델
     * @return 분리된 질의 리스트
     * @throws IOException ollama 서버 오류 발생
     */
    public List<String> getSubQuery(String query, String model) throws IOException {
        return getSubQuery(query, model, null);
    }

    /**
     * 사용자 질의 분리
     * @param query 사용자 질의
     * @param model 모델
     * @param option 옵션
     * @return 분리된 질의 리스트
     * @throws IOException ollama 서버 오류 발생
     */
    public List<String> getSubQuery(String query, String model, OllamaGenerateOption option) throws IOException {
        GenerateSubQueryRequest generateSubQueryRequest = new GenerateSubQueryRequest(query);
        if(model != null) {
            generateSubQueryRequest.model(model);
        }
        if(option != null) {
            generateSubQueryRequest.option(option);
        }
        Map<String, Object> response = decompositionQuery(generateSubQueryRequest);
        return (List<String>) response.get(GenerateSubQueryRequest.SUB_QUERY_FIELD_NAME);
    }


    //TODO: 테스트 구현필요
    /**
     * 문단 요약
     * @param summationRequest 요약 요청
     * @return 요약 결과
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String,Object> summation(SummationRequest summationRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(summationRequest);
            return objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 문단요약 스트림
     * @param summationStreamRequest 요약 스트림 요청
     * @return 스트림 결과
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> summationAsStream(SummationStreamRequest summationStreamRequest)
            throws IOException {
        InputStream inputStream = getGenerateInputStream(summationStreamRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, new TypeReference<>() {});
                    } catch (IOException e) {
                        throw new RuntimeException("JSON parse error: " + line, e);
                    }
                });
    }

    /**
     * stream 으로 문서 생성
     *
     * @param generateDocStreamRequest 문서 생성 Stream request
     * @return Stream response
     * @throws IOException ollama 서버 오류 발생
     */
    public Stream<Map<String, Object>> generateDocAsStream(GenerateDocStreamRequest generateDocStreamRequest) throws IOException {
        return convertInputStreamToStream(getGenerateInputStream(generateDocStreamRequest));
    }

    /**
     * 문서 생성
     *
     * @param generateDocRequest 문서 생성 request
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> generateDoc(GenerateDocRequest generateDocRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(generateDocRequest);
            return objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 주제 추출
     *
     * @param titleExtractionRequest 주제 추출 요청
     * @return map response
     * @throws IOException ollama 서버 오류 발생
     */
    public Map<String, Object> titleExraction(TitleExtractionRequest titleExtractionRequest) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getGenerateInputStream(titleExtractionRequest);
            return objectMapper.readValue(StreamUtil.inputStreamToString(inputStream), Map.class);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    private InputStream getGenerateInputStream(OllamaGenerateRequest request) throws IOException {
        try {
            HttpURLConnection httpURLConnection = prepareInputStream(request.url(), request.requestMethod());
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(request.toJsonString().getBytes(StandardCharsets.UTF_8));
            dataOutputStream.close();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned non-OK status: " + responseCode);
            }
            return httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private InputStream getOllamaInputStream(CommonOllamaRequest request) throws IOException {
        try {
            HttpURLConnection httpURLConnection = prepareInputStream(request.url(), request.requestMethod());
            if(!request.requestMethod().equals("GET")) {
                request.sendRequest(httpURLConnection.getOutputStream());
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned non-OK status: " + responseCode);
            }
            return httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private HttpURLConnection prepareInputStream(String urlString, String method) throws IOException {
        HttpURLConnection httpURLConnection;
        URL url = new URL("http://" + host + ":" + port + urlString);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setUseCaches(false);
        if ("GET".equalsIgnoreCase(method)) {
            httpURLConnection.setDoOutput(false);
        } else {
            httpURLConnection.setDoOutput(true);
        }
        return httpURLConnection;
    }


    private Stream<Map<String, Object>> convertInputStreamToStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        return reader.lines()
                .filter(line -> line.trim().endsWith("}") && line.startsWith("{"))
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, new TypeReference<>() {
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private boolean isFinalResponse(Map<String, Object> response) {
        if (response.containsKey(DONE_KEY)) {
            return (boolean) response.get(DONE_KEY);
        } else {
            return false;
        }
    }

    private String getChatResponseContent(Map<String, Object> chatResponse) {
        if (chatResponse.containsKey(MESSAGE_KEY)) {
            Map<String, String> responseMessage = (Map<String, String>) chatResponse.get(MESSAGE_KEY);
            return responseMessage.get(CONTENT_KEY);
        }
        return "";
    }


}