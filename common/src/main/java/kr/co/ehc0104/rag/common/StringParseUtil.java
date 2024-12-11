package kr.co.ehc0104.rag.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParseUtil {


    public static List<String> segmentationStrToList(String str, Pattern pattern) {
        List<String> stringList = new ArrayList<>();
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            stringList.add(matcher.group(1).trim());
        }
        return stringList;
    }

    private StringParseUtil(){}
}
