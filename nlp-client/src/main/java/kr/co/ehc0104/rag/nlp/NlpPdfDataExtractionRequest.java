package kr.co.ehc0104.rag.nlp;

import java.io.*;
import java.nio.file.Files;

public class NlpPdfDataExtractionRequest extends NlpRequestCommon<String, NlpPdfDataExtractionResponse> {

    private final File file;

    public NlpPdfDataExtractionRequest(File file) {
        super(NlpPdfDataExtractionResponse.class);
        this.file = file;
    }

    @Override
    String requestBody() {
        return null;
    }

    @Override
    String url() {
        return "/pdf/extract";
    }

    public void writeFileToOutputStream(OutputStream outputStream, String dataBoundary) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
            writer.append("--").append(dataBoundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(this.file.getName()).append("\"\r\n")
                    .append("Content-Type: ").append(Files.probeContentType(this.file.toPath())).append("\r\n")
                    .append("\r\n")
                    .flush();

            try (FileInputStream fileInputStream = new FileInputStream(this.file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            outputStream.flush();
            writer.append("\r\n").flush();

            writer.append("--").append(dataBoundary).append("--\r\n")
                    .flush();
        }
    }

}