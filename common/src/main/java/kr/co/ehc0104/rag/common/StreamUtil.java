package kr.co.ehc0104.rag.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtil {

    /**
     * InputStream을 읽어서 String으로 변환
     * @param inputStream 변환할 InputStream
     * @return 변환된 String
     * @throws IOException 변환 중 발생한 예외
     */
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\r");
        }
        return stringBuilder.toString();
    }


    private StreamUtil() {
    }
}
