package kr.co.ehc0104.rag.nlp;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public record NlpPdfDataExtractionResponse(List<PdfPageData> result) {

    @Setter
    @Getter
    @ToString
    public static class PdfPageData {

        private int page;
        private String text;
        private List<String> images;

    }

    public void savePdfImages(String path) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Base64.Decoder base64Decoder = Base64.getDecoder();

        for (PdfPageData pageData : this.result) {
            List<String> images = pageData.getImages();
            for (int i = 0; i < images.size(); i++) {
                String imageFileName = Path.of(path, String.format("%s_%d_%d.png", uuid, pageData.getPage(), i + 1)).toString();
                try (OutputStream outputStream = new FileOutputStream(imageFileName)) {
                    byte[] imageBytes = base64Decoder.decode(images.get(i).getBytes(StandardCharsets.UTF_8));
                    outputStream.write(imageBytes);
                }

                images.set(i, imageFileName);
            }
        }
    }

}
