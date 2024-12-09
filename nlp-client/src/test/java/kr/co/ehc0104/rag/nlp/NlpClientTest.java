package kr.co.ehc0104.rag.nlp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NlpClientTest {

    private final static NlpClient nlpClient = new NlpClient("127.0.0.1", 8090);

    @Test
    @DisplayName("reRank 요청 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testReRankRequest() throws IOException {
        List<String> contexts = List.of("이순신은 조선 중기의 무신이다.", "이순신은 조선 후기의 무신이다.");
        String question = "이순신은 무엇을 했나요?";
        NlpReRankRequest nlpReRankRequest = new NlpReRankRequest(contexts, question, NlpReRankRequest.ReRankerType.GEMMA);
        NlpReRankResponse nlpReRankResponse = nlpClient.reRank(nlpReRankRequest);
        assertTrue(nlpReRankResponse.result().size() > 0);
    }

    @Test
    @DisplayName("embedding 요청 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testEmbeddingRequest() throws IOException {
        String text = "이순신은 무엇을 했나요?";
        NlpEmbeddingRequest nlpEmbeddingRequest = new NlpEmbeddingRequest(text, NlpEmbeddingRequest.EmbeddingModel.ROBERTA);
        NlpEmbeddingResponse nlpEmbeddingResponse = nlpClient.embedding(nlpEmbeddingRequest);
        assertTrue(nlpEmbeddingResponse.embedding().size() > 0);
    }

    @Test
    @DisplayName("문장 청킹 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testChunkingRequest() throws IOException {
        String text = "이순신은 무엇을 했나요? 이순신은 무신이다 이순신은 조선 중기의 무신이다 이순신은 조선 후기의 무신이다 이순신은 조선 후기의 무신이다";

        TaskId taskIdResponse = nlpClient.chunking(text);
        String taskId = taskIdResponse.taskId();
        ChunkingResponse chunkingResponse = null;

        while (true){
            TaskStatus taskStatus = nlpClient.taskStatus(taskId);
            try {
                if(taskStatus.isDone()) {
                    chunkingResponse = nlpClient.chunkTaskResult(taskId);
                    nlpClient.deleteTask(taskId);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                break;
            }
        }
        for(String chunk : chunkingResponse.chunks()){
            System.out.println(chunk);
        }

        assertTrue(chunkingResponse.chunks().size() > 0);
    }

    @Test
    @DisplayName("문장 클러스터링 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testClusteringRequest() throws IOException {
        boolean isSuccess = false;
        List<String> texts = List.of("이순신은 무신이다.", "이순신은 일본이 벌벌 떤다.","이순신은 명장이다.",
                "삼성전자는 대한민국의 기업이다.","갤럭시 시리즈등의 제품군을 가지고 있다");
        TaskId taskIdResponse = nlpClient.clustering(texts, NlpEmbeddingRequest.EmbeddingModel.ROBERTA);


        ClusteringResponse clusteringResponse =null;

        String taskId = taskIdResponse.taskId();

        while (true){
            TaskStatus taskStatus = nlpClient.taskStatus(taskId);
            try {
                if(taskStatus.isDone()) {
                    clusteringResponse = nlpClient.clusterTaskResult(taskId);
                    nlpClient.deleteTask(taskId);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                break;
            }
        }

        if(clusteringResponse.clustersSize()>0) {
            for(List<String> cluster : clusteringResponse.clusters()) {
                if(cluster.size()==3 &&
                        cluster.get(0).contains("이순신")
                        && cluster.get(1).contains("이순신")
                        && cluster.get(2).contains("이순신")) {
                    isSuccess = true;
                    break;
                }
            }

        }
        assertTrue(isSuccess);

    }

    @Test
    @DisplayName("summary 요청 테스트")
    @Disabled("환경에 종속적인 테스트라 비활성화 처리함")
    public void testSummaryRequest() throws IOException {

        String content = """
                1. 개요[편집]
                戰方急전 방 급 愼勿言我死신 물 언 아 사
                싸움이 급하다. 내가 죽었다는 말을 하지 마라.
                16세기 말 조선의 명장이자 구국영웅. 임진왜란 및 정유재란 당시 조선 수군을 지휘했던 제독이었다. 시호는 충무공이다.
                2. 행적과 위상[편집]
                우리는 영예로운 충무공의 후예이다.
                대한민국 해군의 다짐
                한국 민족의 역사는 이웃 민족의 침략에 항쟁한 고난의 역사다. 그러나 매양 그 고난을 헤치고 이겨낸 극복의 역사이기도 하다. 이 극복의 역사를 통하여 하나의 힘이 움직였음을 볼 수 있으니 그 힘이 바로 민족을 죽음 속에서도 건져낼 수 있는 민족정기요 이 정기의 가장 대표적인 발양자가 충무공 이순신 장군이시다.
                               
                서기 1545년 4월 28일 음력 3월초 8일 서울에서 탄생,
                1598년 12월 16일 음력 11월 19일 노량에서 순직,
                54년 동안의 일생을 통해 오직 정의에 살고 정의에 죽은 이다.
                               
                특히 1592년으로부터 7년 동안 싸운 저 유명한 임진란 때 왜적의 침략으로 종사는 위태롭고 민생은 도탄에 빠졌을 적에 쓰러지는 국가, 민족의 운명을 한 손으로 바로잡아 일으켰으니 창생의 생명을 살리고 역사의 명맥을 잇게 한 크신 공로야말로 천추에 사라지지 않을 것이요 만대에 겨레의 제사를 받으리라.
                               
                비록 육신의 몸은 마지막 해전에서 최후의 피를 흘렸을지라도 꽃다운 혼은 저 태양이 되어 조국과 함께 길이 살아계실 것이니 과연 우리 역사의 면류관이요 또 빛과 힘과 자랑이 아니겠느냐.
                               
                아! 님이 함께 계시는 이 나라여 복이 있으라.
                광화문 광장에 설치된 이순신 장군 동상의 건립기
                오늘날 대한민국에서 수많은 국민들이 존경과 흠모의 대상으로 삼으며, 그 존재만으로 애국심과 자부심을 갖게 해주는 한국사의 대표적인 구국영웅이다. 세종대왕과 함께 한국사 최고의 위인으로 높은 위상과 명성을 자랑하는 인물로,[17] 대한민국 수도 서울의 중심 광화문 광장에 세워져 있는 대형 동상의 주인공이다.[18] 또한 이순신의 이름을 딴 대교, 학교, 함선과 거리 등이 조성되어 있어서 한국인에게 이름이 가장 많이 언급되는 역사적 인물 중 하나이기도 하다.
                """;
        NlpSummarizeResponse summarizeResponse = nlpClient.summarize(content);
        System.out.println("summarizeResponse.result() = " + summarizeResponse.result());
    }


}