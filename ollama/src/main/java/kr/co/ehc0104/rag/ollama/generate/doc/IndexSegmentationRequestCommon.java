package kr.co.ehc0104.rag.ollama.generate.doc;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.regex.Pattern;

public class IndexSegmentationRequestCommon<T extends IndexSegmentationRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private final String query;
    private final String subject;

    public static final String INDEX_LIST_FIELD_NAME = "index_list";

    public static final Pattern INDEX_REGEX_PATTERN = Pattern.compile("\\d+\\.\\s+(.*)");

    protected IndexSegmentationRequestCommon(boolean stream, String subject, String query) {
        super(stream);
        this.subject = subject;
        this.query = query;
    }


    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Please extract the table of contents from the following request").append("\r");
        stringBuilder.append("$$$").append("\r");
        stringBuilder.append(query);
        stringBuilder.append("$$$").append("\r");
        return stringBuilder.toString();
    }

    @Override
    protected String systemPrompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are an assistant that extracts a table of contents from the user " +
                "request enclosed in $$$ $$$ based on the given subject.\r");
        stringBuilder.append("The subject is as follows:\r");
        stringBuilder.append(subject).append("\r");
        stringBuilder.append("The table of contents must be separated in the format of 1., 2., 3.," +
                " and each item should be returned with a line break.");
        return stringBuilder.toString();
    }
}