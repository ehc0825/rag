package kr.co.ehc0104.rag.ollama;

import kr.co.ehc0104.rag.ollama.chat.common.ChatRequest;
import kr.co.ehc0104.rag.ollama.chat.common.ChatStreamRequest;
import kr.co.ehc0104.rag.ollama.chat.rag.ChatRagRequest;
import kr.co.ehc0104.rag.ollama.chat.rag.ChatRagStreamRequest;
import kr.co.ehc0104.rag.ollama.embedding.OllamaEmbeddingResponse;
import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateOption;
import kr.co.ehc0104.rag.ollama.generate.doc.*;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocRequest;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocRequestCommon;
import kr.co.ehc0104.rag.ollama.generate.hypothentical.HypotheticalDocStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.query.GenerateSubQueryRequest;
import kr.co.ehc0104.rag.ollama.generate.query.GenerateSubQueryRequestCommon;
import kr.co.ehc0104.rag.ollama.generate.query.GenerateSubQueryStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.Context;
import kr.co.ehc0104.rag.ollama.generate.rag.RagRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.RagStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionRequest;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionRequestCommon;
import kr.co.ehc0104.rag.ollama.generate.recommend.RecommendQuestionStreamRequest;
import kr.co.ehc0104.rag.ollama.generate.summation.SummationRequest;
import kr.co.ehc0104.rag.ollama.generate.summation.SummationStreamRequest;
import kr.co.ehc0104.rag.ollama.tag.ModelTag;
import kr.co.ehc0104.rag.ollama.tag.OllamaModelTagResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class OllamaClientTest {


    private final static OllamaClient ollamaClient = new OllamaClient("127.0.0.1", 11434);


    @Test
    @DisplayName("json Response 방식의 문서 생성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamGenerateDocRequest() {
        boolean isSuccess;
        OllamaGenerateOption option = OllamaGenerateOption.builder().temperature(0.1f).build();
        try {
            Map<String, Object> firstResponse = ollamaClient.generateDoc(new GenerateDocRequest("재택근무", Arrays.asList("서문", "본론", "결론"),
                    new GenerateDocRequestCommon.Hint(0, "재택근무란 무엇인지에 대한 한국어 설명문을 만들어줘")).option(option));
            System.out.println("llama first response = " + firstResponse.get("response"));
            Map<String, Object> secondResponse = ollamaClient.generateDoc
                    (new GenerateDocRequest("재택근무", Arrays.asList("서문", "본론", "결론"),
                            new GenerateDocRequestCommon.Hint(1,
                                    "재택근무가 왜 필요한지 설명하고 왜 효율적인지에 대한 한국어 설명문을 만들어줘"))
                            .option(option));
            System.out.println("llama second response = " + secondResponse.get("response"));
            Map<String, Object> thirdResponse = ollamaClient.generateDoc(new GenerateDocRequest("재택근무", Arrays.asList("서문", "본론", "결론"),
                    new GenerateDocRequestCommon.Hint(2, "재택근무를 해달라고 성토하는 한국어 설명문을 만들어줘")).option(option));
            System.out.println("llama third response = " + thirdResponse.get("response"));
            isSuccess = true;
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("stream response 방식의 문서 작성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamGenerateDocRequest() {
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        AtomicBoolean isDone = new AtomicBoolean(false);
        OllamaGenerateOption option = OllamaGenerateOption.builder().temperature(0.1f).build();
        try {
            Stream<Map<String, Object>> streamResponse = ollamaClient.generateDocAsStream(new GenerateDocStreamRequest("재택근무", Arrays.asList("서문", "본론", "결론"),
                    new GenerateDocRequestCommon.Hint(0, "재택근무란 무엇인지에 대한 한국어 설명문을 만들어줘")).option(option));
            streamResponse.forEach(map -> {
                if (isSuccess.get()) {
                    if (!map.containsKey("response")) {
                        isSuccess.set(false);
                    } else {
                        isSuccess.set(true);
                        System.out.println("response = " + map.get("response"));
                    }
                    isDone.set((Boolean) map.get("done"));
                }
            });
        } catch (IOException e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
        assertTrue(isSuccess.get() && isDone.get());
    }


    @Test
    @DisplayName("json Response 방식의 목차 분리 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamSegmentationIndexRequest() {
        boolean isSuccess = false;
        try {
            Map<String, Object> response =
                    ollamaClient.indexSegmentation(new IndexSegmentationRequest("재택근무",
                            "서론 본론 결론 에다가 추가로 중간에 출처 항목도 있었으면 좋겠어"));
            List<String> indexList = (List<String>) response.get(IndexSegmentationRequestCommon.INDEX_LIST_FIELD_NAME);
            for (String index : indexList) {
                if (isOneOfRequestIndex(index)) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    break;
                }
            }
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("stream response 방식의 목차 분리 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamSegmentationIndexRequest() {
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        AtomicBoolean isDone = new AtomicBoolean(false);
        OllamaGenerateOption option = OllamaGenerateOption.builder().temperature(0.1f).build();
        try {
            Stream<Map<String, Object>> streamResponse =
                    ollamaClient.indexSegmentationAsStream(new IndexSegmentationStreamRequest("재택근무",
                            "서론 본론 결론 에다가 추가로 중간에 출처 항목도 있었으면 좋겠어").option(option));
            streamResponse.forEach(map -> {
                if (isSuccess.get()) {
                    if (!map.containsKey("response")) {
                        isSuccess.set(false);
                    } else {
                        isSuccess.set(true);
                    }
                    isDone.set((Boolean) map.get("done"));
                    if (isDone.get()) {
                        List<String> indexList =
                                (List<String>) map.get(IndexSegmentationRequestCommon.INDEX_LIST_FIELD_NAME);
                        for (String index : indexList) {
                            System.out.println("index = " + index);
                            if (isOneOfRequestIndex(index)) {
                                isSuccess.set(true);
                            } else {
                                isSuccess.set(false);
                                break;
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
        assertTrue(isSuccess.get() && isDone.get());
    }


    @Test
    @DisplayName("json Response 방식의 rag 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamRagRequest() {
        boolean isSuccess = false;
        try {
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));

            Map<String, Object> response =
                    ollamaClient.rag(new RagRequest(contextList, "재택근무가 왜 시행되고 있는거야?"));

            if (response.containsKey("response")) {
                if(response.get("response").toString().contains("코로나")) {
                    System.out.println("response = " + response.get("response"));
                    isSuccess = true;
                }
            }
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("stream response 방식의 rag 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamRagRequest() {
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        AtomicBoolean isDone = new AtomicBoolean(false);
        try {
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));

            Stream<Map<String, Object>> streamResponse =
                    ollamaClient.ragAsStream(new RagStreamRequest(contextList, "재택근무가 왜 시행되고 있는거야?"));

            streamResponse.forEach(map -> {
                if (isSuccess.get()) {
                    if (!map.containsKey("response")) {
                        isSuccess.set(false);
                    } else {
                        isSuccess.set(true);
                        System.out.println("response = " + map.get("response"));
                    }
                    isDone.set((Boolean) map.get("done"));
                }
            });
        } catch (IOException e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
        assertTrue(isSuccess.get() && isDone.get());
    }


    @Test
    @DisplayName("json Response 방식의 가정 문서 생성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamHypotheticalDocRequest() {
        boolean isSuccess = false;
        try {
            Map<String, Object> response =
                    ollamaClient.generateHypotheticalDocument(new HypotheticalDocRequest(300,
                            "재택 근무에 대해 설명해주고 전세사기에 대해서 알려줘봐."));
            List<String> documentList =
                    (List<String>) response.get(HypotheticalDocRequestCommon.HYPOTHETICAL_DOCUMENT_FIELD_NAME);
            for(String doc: documentList) {
                System.out.println("doc = " + doc);
            }
            if(!documentList.isEmpty()) {
                isSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("stream Response 방식의 가정 문서 생성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamHypotheticalDocRequest() {
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        AtomicBoolean isDone = new AtomicBoolean(false);
        try {
            Stream<Map<String, Object>> streamResponse =
                    ollamaClient.generateHypotheticalDocumentAsStream(new HypotheticalDocStreamRequest(300,
                            "재택 근무에 대해 설명해줘"));
            streamResponse.forEach(map -> {
                if (isSuccess.get()) {
                    if (!map.containsKey("response")) {
                        isSuccess.set(false);
                    } else {
                        isSuccess.set(true);
                    }
                    isDone.set((Boolean) map.get("done"));
                    if (isDone.get()) {
                        List<String> documentList =
                                (List<String>) map
                                        .get(HypotheticalDocRequestCommon.HYPOTHETICAL_DOCUMENT_FIELD_NAME);
                        if(!documentList.isEmpty()) {
                            System.out.println("documentList = " + documentList);
                            isSuccess.set(true);
                        }
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("e = " + e);
            e.printStackTrace();
        }
        assertTrue(isSuccess.get() && isDone.get());
    }


    @Test
    @DisplayName("json Response 방식의 채팅 rag 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamRagChat() {
        boolean isSuccess = false;
        try {
//            테스트 데이터
            String firstQuery = "재택근무가 왜 시행되고 있는거야? 장점이 뭐길래?";
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무를 도입하려면 고려할 점이 많습니다."));


            String secondQuery = "재택근무의 장점이 뭐냐니까?";
            List<Context> additionalContextList = new ArrayList<>();
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 효율성을 증가시킵니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 생산성을 높여줍니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 집중도를 높여줍니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 편의성을 높여줍니다."));

            String thirdQuery = "재택근무의 단점은 뭐길래?";
            List<Context> additionalContextList2 = new ArrayList<>();
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 효율성을 감소시킵니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 생산성을 낮춰줍니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 집중도를 낮춰줍니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 편의성을 낮춰줍니다."));

            String fourthQuery = "재택근무를 도입하려면 어떤 점을 고려해야 하니?";
            String fifthQuery = "대한민국 대통령이 누구니?";
            String sixthQuery = "대한민국 대통령은 누구야?";


            ChatRagRequest chatRagRequest = new ChatRagRequest(contextList, firstQuery);

            Map<String, Object> firstResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("firstResponse = " + firstResponse.get("message"));
            System.out.println("***************************************");
            chatRagRequest.pushContext(additionalContextList);
            chatRagRequest.pushQuery(secondQuery);

            Map<String, Object> secondResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("secondResponse = " + secondResponse.get("message"));
            System.out.println("***************************************");

            chatRagRequest.pushContext(additionalContextList2);
            chatRagRequest.pushQuery(thirdQuery);
            Map<String, Object> thirdResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("thirdResponse = " + thirdResponse.get("message"));
            System.out.println("***************************************");

            chatRagRequest.pushQuery(fourthQuery);
            Map<String, Object> fourthResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("fourthResponse = " + fourthResponse.get("message"));
            System.out.println("***************************************");

            chatRagRequest.pushQuery(fifthQuery);
            Map<String, Object> fifthResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("fifthResponse = " + fifthResponse.get("message"));
            System.out.println("***************************************");

            chatRagRequest.pushQuery(sixthQuery);
            Map<String, Object> sixthResponse =
                    ollamaClient.ragChat(chatRagRequest);
            System.out.println("***************************************");
            System.out.println("sixthResponse = " + sixthResponse.get("message"));
            System.out.println("***************************************");

            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }


    @Test
    @DisplayName("stream Response 방식의 채팅 rag 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamRagChat() {
        boolean isSuccess = false;
        try {
//            테스트 데이터
            String firstQuery = "재택근무가 왜 시행되고 있는거야? 장점이 뭐길래?";
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무를 도입하려면 고려할 점이 많습니다."));


            String secondQuery = "재택근무의 장점이 뭐냐니까?";
            List<Context> additionalContextList = new ArrayList<>();
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 효율성을 증가시킵니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 생산성을 높여줍니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 집중도를 높여줍니다."));
            additionalContextList.add(new Context("재택근무.txt", "재택근무는 일의 편의성을 높여줍니다."));

            String thirdQuery = "재택근무의 단점은 뭐길래?";
            List<Context> additionalContextList2 = new ArrayList<>();
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 효율성을 감소시킵니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 생산성을 낮춰줍니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 집중도를 낮춰줍니다."));
            additionalContextList2.add(new Context("재택근무.txt", "재택근무는 일의 편의성을 낮춰줍니다."));

            String fourthQuery = "재택근무를 도입하려면 어떤 점을 고려해야 하니?";
            String fifthQuery = "대한민국 대통령이 누구니?";
            String sixthQuery = "대한민국 대통령은 누구야?";


            ChatRagStreamRequest chatRagRequest = new ChatRagStreamRequest(contextList, firstQuery);

            Stream<Map<String, Object>> firstResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);
            System.out.println("=======================================");
            printStreamChatResponse(firstResponse);
            System.out.println("=======================================");

            chatRagRequest.pushContext(additionalContextList);
            chatRagRequest.pushQuery(secondQuery);


            Stream<Map<String, Object>>  secondResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);
            System.out.println("=======================================");
            printStreamChatResponse(secondResponse);
            System.out.println("=======================================");

            chatRagRequest.pushContext(additionalContextList2);
            chatRagRequest.pushQuery(thirdQuery);
            Stream<Map<String, Object>>  thirdResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);
            System.out.println("=======================================");
            printStreamChatResponse(thirdResponse);
            System.out.println("=======================================");

            chatRagRequest.pushQuery(fourthQuery);
            Stream<Map<String, Object>>  fourthResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);

            System.out.println("=======================================");
            printStreamChatResponse(fourthResponse);
            System.out.println("=======================================");

            chatRagRequest.pushQuery(fifthQuery);
            Stream<Map<String, Object>>  fifthResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);
            System.out.println("=======================================");
            printStreamChatResponse(fifthResponse);
            System.out.println("=======================================");


            chatRagRequest.pushQuery(sixthQuery);
            Stream<Map<String, Object>> sixthResponse =
                    ollamaClient.ragChatAsStream(chatRagRequest);
            System.out.println("=======================================");
            printStreamChatResponse(sixthResponse);
            System.out.println("=======================================");


            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("SubQuery 생성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testGenerateSubQuery(){
        boolean isSuccess = false;
        String query = "재택근무의 장단점에 대해서 설명해줘봐";
        GenerateSubQueryRequest generateSubQueryRequest = new GenerateSubQueryRequest(query);
        try {
            Map<String,Object> response = ollamaClient.decompositionQuery(generateSubQueryRequest);
            if(response.containsKey(GenerateSubQueryRequestCommon.SUB_QUERY_FIELD_NAME)) {
                List<String> subQueries = (List<String>) response.get(GenerateSubQueryRequestCommon.SUB_QUERY_FIELD_NAME);
                if(subQueries.size()>0) {
                    isSuccess = true;
                    for(String subQuery : subQueries) {
                        System.out.println(subQuery);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("SubQuery stream 생성 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamGenerateSubQuery(){
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        AtomicBoolean isDone = new AtomicBoolean(false);
        String query = "재택근무의 장단점에 대해서 설명해줘봐";
        GenerateSubQueryStreamRequest generateSubQueryRequest = new GenerateSubQueryStreamRequest(query);
        try {
            Stream<Map<String,Object>> streamResponse = ollamaClient.decompositionQueryAsStream(generateSubQueryRequest);
            streamResponse.forEach(map -> {
                if (!map.containsKey("response")) {
                    isSuccess.set(false);
                } else {
                    isSuccess.set(true);
                    System.out.println("response = " + map.get("response"));
                }
                if (isSuccess.get()) {
                    isDone.set((Boolean) map.get("done"));
                    if (isDone.get()) {
                        List<String> subQueryList =
                                (List<String>) map
                                        .get(GenerateSubQueryRequestCommon.SUB_QUERY_FIELD_NAME);
                        if(!subQueryList.isEmpty()) {
                            for(String subQuery : subQueryList) {
                                System.out.println(subQuery);
                            }
                            isSuccess.set(true);
                        }
                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess.get());
    }

    @Test
    @DisplayName("subQuery 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testSubQuery(){
        boolean isSuccess = false;
        String query = "재택근무의 장단점에 대해서 설명해줘봐";
        try {
            List<String> subQueries = ollamaClient.getSubQuery(query);
            if(subQueries.size()>0) {
                isSuccess = true;
                for(String subQuery : subQueries) {
                    System.out.println(subQuery);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("요약 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testSummarize(){
        boolean isSuccess = false;
        String query = """
                이순신의 가문은 4대 때에 조선 왕조로 넘어오면서 두각을 나타낸다. 5대조인 이변(李邊)은 영중추부사(領中樞府事)와 홍문관 대제학을 지냈고, 증조부 이거(李琚)는 병조참의에 이르렀다.
                                
                그러나 할아버지 이백록(李百祿)이 조광조(趙光祖) 등 지치주의(至治主義)를 주장하던 소장파 사림(少壯派士林)들과 뜻을 같이하다가 기묘사화의 참화를 당한 뒤로, 아버지 이정도 관직에 뜻을 두지 않았던 만큼 이순신이 태어날 즈음에 가세는 이미 기울어 있었다.
                                
                그러하였음에도 이순신이 뒤에 명장으로 나라에 큰 공을 남길 수 있었던 것은 유년시절에 어머니 변씨로부터 큰 영향을 받았던 때문이었다. 변씨는 현모로서 아들들을 끔찍이 사랑하면서도 가정교육을 엄격히 하였다.
                                
                이순신은 위로 이희신(李羲臣) · 이요신(李堯臣)의 두 형과 아우 이우신(李禹臣)이 있어 모두 4형제였다. 형제들의 이름은 돌림자인 신(臣)자 위에 삼황오제(三皇五帝) 중에서 복희씨(伏羲氏) · 요(堯) · 순(舜) · 우(禹) 임금을 시대순으로 따서 붙인 것이다.
                                
                이순신은 사대부가의 전통인 충효와 문학에 있어서 뛰어났을 뿐 아니라 시재(詩才)에도 특출하였으며, 정의감과 용감성을 겸비하였으면서도 인자한 성품을 지니고 있었다.
                                
                강한 정의감은 뒤에 상관과 충돌하여 모함을 받기도 하였으며, 용감성은 적을 두려워하지 않고 전투에서 매양 선두에 나서서 장졸들을 지휘함으로써 예하장병의 사기를 북돋워 여러 전투에서 전승의 기록을 남길 수 있었다. 또, 이순신의 인자한 성품은 홀로 계신 노모를 극진히 받들 수 있었고, 어버이를 일찍 여읜 조카들을 친아들같이 사랑할 수 있었다.
                                
                이순신의 시골 본가는 충청남도 아산시 염치면 백암리이나, 어린 시절의 대부분은 생가인 서울 건천동에서 자란 듯하다. 같은 마을에 살았던 유성룡(柳成龍)은 『징비록(懲毖錄)』에서 이순신이 어린 시절부터 큰 인물로 성장할 수 있는 자질을 갖추고 있었음을 다음과 같이 묘사하고 있다.
                                
                “이순신은 어린 시절 얼굴 모양이 뛰어나고 기풍이 있었으며 남에게 구속을 받으려 하지 않았다. 다른 아이들과 모여 놀라치면 나무를 깎아 화살을 만들고 그것을 가지고 동리에서 전쟁놀이를 하였으며, 자기 뜻에 맞지 않는 자가 있으면 그 눈을 쏘려고 하여 어른들도 꺼려 감히 이순신의 문앞을 지나려 하지 않았다. 또 자라면서 활을 잘 쏘았으며 무과에 급제하여 발신(發身)하려 하였다. 또 자라면서 말타고 활쏘기를 좋아하였으며 더욱이 글씨를 잘 썼다.”
                                
                28세 되던 해에 비로소 무인 선발시험의 일종인 훈련원별과(訓鍊院別科)에 응시하였으나 불운하게도 시험장에서 달리던 말이 거꾸러지는 바람에 말에서 떨어져서 왼발을 다치고 실격하였다.
                                
                그 뒤에도 계속 무예를 닦아, 4년 뒤인 1576년(선조 9) 식년무과에 병과로 급제하여 권지훈련원봉사(權知訓鍊院奉事)로 처음 관직에 나갔다.
                                
                이어 함경도의 동구비보권관(董仇非堡權管)에 보직되고, 이듬해에 발포수군만호(鉢浦水軍萬戶)를 거쳐, 1583년 건원보권관(乾原堡權管) · 훈련원참군(訓鍊院參軍)을 역임하고, 1586년에는 사복시주부가 되었다. 그러나 무관으로 발을 들여놓은 진로는 순탄하지만은 않았다.
                """;
        try {
            Map<String,Object> summationResponse
                    = ollamaClient.summation(new SummationRequest("이순신의 업적.txt",
                    query));

            String summary = (String) summationResponse.get(OllamaClient.RESPONSE_KEY);
            if(summary != null) {
                isSuccess = true;
                System.out.println(summary);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("요약 테스트 stream")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testSummarizeAsStream(){
        boolean isSuccess = false;
        String query = """
                이순신의 가문은 4대 때에 조선 왕조로 넘어오면서 두각을 나타낸다. 5대조인 이변(李邊)은 영중추부사(領中樞府事)와 홍문관 대제학을 지냈고, 증조부 이거(李琚)는 병조참의에 이르렀다.
                                
                그러나 할아버지 이백록(李百祿)이 조광조(趙光祖) 등 지치주의(至治主義)를 주장하던 소장파 사림(少壯派士林)들과 뜻을 같이하다가 기묘사화의 참화를 당한 뒤로, 아버지 이정도 관직에 뜻을 두지 않았던 만큼 이순신이 태어날 즈음에 가세는 이미 기울어 있었다.
                                
                그러하였음에도 이순신이 뒤에 명장으로 나라에 큰 공을 남길 수 있었던 것은 유년시절에 어머니 변씨로부터 큰 영향을 받았던 때문이었다. 변씨는 현모로서 아들들을 끔찍이 사랑하면서도 가정교육을 엄격히 하였다.
                                
                이순신은 위로 이희신(李羲臣) · 이요신(李堯臣)의 두 형과 아우 이우신(李禹臣)이 있어 모두 4형제였다. 형제들의 이름은 돌림자인 신(臣)자 위에 삼황오제(三皇五帝) 중에서 복희씨(伏羲氏) · 요(堯) · 순(舜) · 우(禹) 임금을 시대순으로 따서 붙인 것이다.
                                
                이순신은 사대부가의 전통인 충효와 문학에 있어서 뛰어났을 뿐 아니라 시재(詩才)에도 특출하였으며, 정의감과 용감성을 겸비하였으면서도 인자한 성품을 지니고 있었다.
                                
                강한 정의감은 뒤에 상관과 충돌하여 모함을 받기도 하였으며, 용감성은 적을 두려워하지 않고 전투에서 매양 선두에 나서서 장졸들을 지휘함으로써 예하장병의 사기를 북돋워 여러 전투에서 전승의 기록을 남길 수 있었다. 또, 이순신의 인자한 성품은 홀로 계신 노모를 극진히 받들 수 있었고, 어버이를 일찍 여읜 조카들을 친아들같이 사랑할 수 있었다.
                                
                이순신의 시골 본가는 충청남도 아산시 염치면 백암리이나, 어린 시절의 대부분은 생가인 서울 건천동에서 자란 듯하다. 같은 마을에 살았던 유성룡(柳成龍)은 『징비록(懲毖錄)』에서 이순신이 어린 시절부터 큰 인물로 성장할 수 있는 자질을 갖추고 있었음을 다음과 같이 묘사하고 있다.
                                
                “이순신은 어린 시절 얼굴 모양이 뛰어나고 기풍이 있었으며 남에게 구속을 받으려 하지 않았다. 다른 아이들과 모여 놀라치면 나무를 깎아 화살을 만들고 그것을 가지고 동리에서 전쟁놀이를 하였으며, 자기 뜻에 맞지 않는 자가 있으면 그 눈을 쏘려고 하여 어른들도 꺼려 감히 이순신의 문앞을 지나려 하지 않았다. 또 자라면서 활을 잘 쏘았으며 무과에 급제하여 발신(發身)하려 하였다. 또 자라면서 말타고 활쏘기를 좋아하였으며 더욱이 글씨를 잘 썼다.”
                                
                28세 되던 해에 비로소 무인 선발시험의 일종인 훈련원별과(訓鍊院別科)에 응시하였으나 불운하게도 시험장에서 달리던 말이 거꾸러지는 바람에 말에서 떨어져서 왼발을 다치고 실격하였다.
                                
                그 뒤에도 계속 무예를 닦아, 4년 뒤인 1576년(선조 9) 식년무과에 병과로 급제하여 권지훈련원봉사(權知訓鍊院奉事)로 처음 관직에 나갔다.
                                
                이어 함경도의 동구비보권관(董仇非堡權管)에 보직되고, 이듬해에 발포수군만호(鉢浦水軍萬戶)를 거쳐, 1583년 건원보권관(乾原堡權管) · 훈련원참군(訓鍊院參軍)을 역임하고, 1586년에는 사복시주부가 되었다. 그러나 무관으로 발을 들여놓은 진로는 순탄하지만은 않았다.
                """;
        try {
            Stream<Map<String,Object>> summationResponse
                    = ollamaClient.summationAsStream(new SummationStreamRequest("이순신의 업적.txt",
                    query));

            StringBuilder responseBuilder = new StringBuilder();
            summationResponse.forEach(map -> {
                responseBuilder.append(map.get(OllamaClient.RESPONSE_KEY));
            });

            if(!responseBuilder.toString().equals("")){
                System.out.println(responseBuilder);
                isSuccess = true;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("ollama 임베딩 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testEmbedding(){
        boolean isSuccess = false;
        String query = "재택근무의 장단점에 대해서 설명해줘봐";
        OllamaEmbeddingRequest ollamaEmbeddingRequest = new OllamaEmbeddingRequest(query);
        try {
            OllamaEmbeddingResponse ollamaEmbeddingResponse = ollamaClient.embedding(ollamaEmbeddingRequest);
            System.out.println(ollamaEmbeddingResponse.embedding());
            if(ollamaEmbeddingResponse.embedding().size()>0) {
                isSuccess = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("ollama 챗봇 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testChat(){
        boolean isSuccess = false;
        ChatRequest ollamaChatRequest = new ChatRequest("안녕");
        try {
            Map<String,Object> chatResponse = ollamaClient.chat(ollamaChatRequest);
            System.out.println("chatResponse = " + chatResponse);
            ollamaChatRequest.pushQuery("다시 한번 잘 인사해봐");
            chatResponse = ollamaClient.chat(ollamaChatRequest);
            System.out.println("chatResponse = " + chatResponse);
            ollamaChatRequest.pushQuery("영어로 아까너가 했던말 번역해봐");
            chatResponse = ollamaClient.chat(ollamaChatRequest);
            System.out.println("chatResponse = " + chatResponse);
            isSuccess = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("ollama 챗봇 스트림 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testChatStream(){
        boolean isSuccess = false;
        ChatStreamRequest ollamaChatStreamRequest = new ChatStreamRequest("안녕");
        try {
            Stream<Map<String,Object>> chatResponse = ollamaClient.chatAsStream(ollamaChatStreamRequest);
            printStreamChatResponse(chatResponse);
            ollamaChatStreamRequest.pushQuery("다시 한번 잘 인사해봐");
            chatResponse = ollamaClient.chatAsStream(ollamaChatStreamRequest);
            printStreamChatResponse(chatResponse);
            ollamaChatStreamRequest.pushQuery("영어로 아까너가 했던말 번역해봐");
            chatResponse = ollamaClient.chatAsStream(ollamaChatStreamRequest);
            printStreamChatResponse(chatResponse);
            isSuccess = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }

    @Test
    @DisplayName("모델 목록조회")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testModelList(){
        boolean isSuccess = false;
        try {
            OllamaModelTagRequest ollamaModelTagRequest = new OllamaModelTagRequest();
            OllamaModelTagResponse ollamaModelTagResponse = ollamaClient.modelTags(ollamaModelTagRequest);
            List<ModelTag> modelList = ollamaModelTagResponse.models();

            for(ModelTag modelTag : modelList) {

                System.out.println("modelTag = " + modelTag.model());
                System.out.println("modelTag.name() = " + modelTag.name());
                System.out.println("modelTag.detail() = " + modelTag.details());
            }
            if(modelList.size()>0) {
                isSuccess = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }


    @Test
    @DisplayName("json Response 방식의 질문 추천 테스트 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testNonStreamRecommendQuestionRequest() {
        boolean isSuccess = false;
        try {
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));

            Map<String, Object> response =
                    ollamaClient.recommendQuestion(new RecommendQuestionRequest(contextList, "재택근무가 왜 시행되고 있는거야?",3));

            if(response.containsKey(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME)) {
                isSuccess = true;
            }
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }


    @Test
    @DisplayName("stream Response 방식의 질문 추천 테스트 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testStreamRecommendQuestionRequest() {
        boolean isSuccess;
        try {
            List<Context> contextList = new ArrayList<>();
            contextList.add(new Context("재택근무.txt", "재택근무란 집에서 근무하는 것을 의미합니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 코로나19로 인해 많은 회사에서 시행하고 있습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 장점이 많습니다."));
            contextList.add(new Context("재택근무.txt", "재택근무는 단점이 많습니다."));

            Stream<Map<String, Object>> response =
                    ollamaClient.recommendQuestionAsStream(new RecommendQuestionStreamRequest(contextList, "재택근무가 왜 시행되고 있는거야?",3));

            AtomicBoolean isDone = new AtomicBoolean(false);
            response.forEach(map -> {
                isDone.set((Boolean) map.get("done"));
                if (isDone.get()) {
                    if(map.containsKey(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME)) {
                        System.out.println("map.get(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME) = "
                                + map.get(RecommendQuestionRequestCommon.QUESTION_LIST_FIELD_NAME));
                    }
                }
            });

            isSuccess = true;
        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }
        assertTrue(isSuccess);
    }


    private void printStreamChatResponse(Stream<Map<String, Object>> response) {
        AtomicBoolean isDone = new AtomicBoolean(false);
        response.forEach(map -> {
            isDone.set((Boolean) map.get("done"));
            if (isDone.get()) {
                System.out.println("");
            }else {
                Map<String,String> message = (Map<String, String>) map.get("message");
                System.out.print(message.get("content"));
            }
        });
    }

    private boolean isOneOfRequestIndex(String index) {
        return index.contains("서론") || index.contains("본론") || index.contains("출처") || index.contains("결론");
    }

}