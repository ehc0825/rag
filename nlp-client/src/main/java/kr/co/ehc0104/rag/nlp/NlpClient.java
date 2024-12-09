package kr.co.ehc0104.rag.nlp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NlpClient {

    private final String host;
    private final int port;
    private final Map<String, String> headers = new HashMap<>();


    /**
     * NlpClient 생성자
     * @param host host
     * @param port port
     */
    public NlpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * NlpClient 생성자
     * @param host host
     * @param port port
     * @param id id
     * @param password password
     */
    public NlpClient(String host, int port, String id, String password) {
        this.host = host;
        this.port = port;
        String idPassword = String.format("%s:%s", id, password);
        headers.put("Authorization", String.format("Basic %s", Base64.getEncoder().encodeToString(idPassword.getBytes())));
    }


    /**
     * context reRank
     * @param request reRank request
     * @return reRank response
     * @throws IOException reRank 중 발생한 예외
     */
    public NlpReRankResponse reRank(NlpReRankRequest request) throws IOException {
        return request.response(getInputStream(request));
    }

    /**
     * embedding
     * @param request embedding request
     * @return embedding response
     * @throws IOException embedding 중 발생한 예외
     */
    public NlpEmbeddingResponse embedding(NlpEmbeddingRequest request) throws IOException {
        return request.response(getInputStream(request));
    }

    /**
     * task 상태조회
     * @param taskId taskId
     * @return task response
     * @throws IOException task 상태 조회 중 발생한 예외
     */
    public TaskStatus taskStatus(String taskId) throws IOException {
        NlpTaskGetRequest nlpTaskGetRequest = new NlpTaskGetRequest(taskId);
        return nlpTaskGetRequest.response(getInputStream(nlpTaskGetRequest));
    }

    /**
     * 문장 청킹
     * @param content content
     * @return task id response
     * @throws IOException 청크 태스크 생성중 예외 발생
     */
    public TaskId chunking(String content) throws IOException{
        NlpChunkRequest chunkContent = new NlpChunkRequest(content);
        return chunkContent.response(getInputStream(chunkContent));
    }

    /**
     * 문장 클러스터링
     * @param contents contents
     * @param embeddingModel embeddingModel
     * @return task id response
     * @throws IOException 클러스터링 태스크 생성중 예외 발생
     */
    public TaskId clustering(List<String> contents, NlpEmbeddingRequest.EmbeddingModel embeddingModel) throws IOException{
        ClusteringRequest clusteringContent = new ClusteringRequest(contents, embeddingModel);
        return clusteringContent.response(getInputStream(clusteringContent));
    }

    /**
     * 문장 청킹 task 결과
     * @param taskId taskId
     * @return 청킹 결과
     * @throws IOException 클러스터링 태스크 결과 조회중 예외 발생
     */
    public ChunkingResponse chunkTaskResult(String taskId) throws IOException {
        TaskResultRequest<ChunkingResponse> taskResultRequest = new TaskResultRequest<>(taskId, ChunkingResponse.class);
        return taskResultRequest.response(getInputStream(taskResultRequest));
    }

    /**
     * 문장 클러스터링 task 결과
     * @param taskId taskId
     * @return 클러스터링 결과
     * @throws IOException
     */
    public ClusteringResponse clusterTaskResult(String taskId) throws IOException {
        TaskResultRequest<ClusteringResponse> taskResultRequest = new TaskResultRequest<>(taskId, ClusteringResponse.class);
        return taskResultRequest.response(getInputStream(taskResultRequest));
    }

    /**
     * task 삭제
     * @param taskId taskId
     * @return task response
     * @throws IOException task 삭제 중 발생한 예외
     */
    public TaskStatus deleteTask(String taskId) throws IOException {
        NlpTaskDeleteRequest nlpTaskDeleteRequest = new NlpTaskDeleteRequest(taskId);
        return nlpTaskDeleteRequest.response(getInputStream(nlpTaskDeleteRequest));
    }

    /**
     * PDF 데이터 추출
     * @param file PDF 파일
     * @param saveImagePath 이미지 저장 경로
     * @return PDF 데이터 추출 결과
     * @throws IOException PDF 데이터 추출 중 발생한 예외
     */
    public NlpPdfDataExtractionResponse extractPdfData(File file, String saveImagePath) throws IOException {
        NlpPdfDataExtractionRequest request = new NlpPdfDataExtractionRequest(file);
        Consumer<HttpURLConnection> customizeHttpURLConnection = connection -> {
            String dataBoundary = String.format("===%d===", System.currentTimeMillis());
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", String.format("multipart/form-data; boundary=%s", dataBoundary));
            try {
                request.writeFileToOutputStream(connection.getOutputStream(), dataBoundary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        NlpPdfDataExtractionResponse response = request.response(this.getInputStream(request, customizeHttpURLConnection));
        response.savePdfImages(saveImagePath);

        return response;
    }

    /**
     * contnet 요약
     * @param content 요약 request
     * @return 요약 response
     * @throws IOException 요약 중 발생한 예외
     */
    public NlpSummarizeResponse summarize(String content) throws IOException {
        NlpSummarizeRequest request = new NlpSummarizeRequest(content);
        return request.response(getInputStream(request));
    }

    private InputStream getInputStream(
            NlpRequestCommon<?, ?> request,
            Consumer<HttpURLConnection> customizeHttpURLConnection
    ) throws IOException {
        try {
            URL url = new URL(String.format("http://%s:%d%s", host, port, request.url()));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            if (!this.headers.isEmpty()) {
                for (Map.Entry<String, String> header : this.headers.entrySet()) {
                    httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod(request.requestMethod());

            // GET 요청이면 setDoOutput(false) 설정
            httpURLConnection.setDoOutput(!"GET".equalsIgnoreCase(request.requestMethod()));
            if (customizeHttpURLConnection == null) {
                if (!"GET".equalsIgnoreCase(request.requestMethod())) {
                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(request.toJsonString().getBytes(StandardCharsets.UTF_8));
                    dataOutputStream.close();
                }
            } else {
                customizeHttpURLConnection.accept(httpURLConnection);
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

    private InputStream getInputStream(NlpRequestCommon<?, ?> request) throws IOException {
        return this.getInputStream(request, null);
    }

}