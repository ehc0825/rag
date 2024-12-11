package kr.co.ehc0104.rag.ollama.generate.hypothentical;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.regex.Pattern;

public class HypotheticalDocRequestCommon<T extends HypotheticalDocRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private static final String START_OF_DOCUMENT_DELIMITER = "<SOFD>";
    private static final String END_OF_DOCUMENT_DELIMITER = "<EOFD>";
    private static final String DOCUMENT_REGEX_PATTERN_STRING = START_OF_DOCUMENT_DELIMITER + "(.*?)" + END_OF_DOCUMENT_DELIMITER;

    public static final Pattern DOCUMENT_REGEX_PATTERN = Pattern.compile(DOCUMENT_REGEX_PATTERN_STRING, Pattern.DOTALL);
    public static final String HYPOTHETICAL_DOCUMENT_FIELD_NAME = "generated_document";


    private final String query;
    private final int chunkSize;

    protected HypotheticalDocRequestCommon(boolean stream, int chunkSize, String query) {
        super(stream);
        this.query = query;
        this.chunkSize = chunkSize;
    }


    @Override
    protected String prompt() {
        return query;
    }

    @Override
    protected String systemPrompt() {
        return "Please provide answers to each topic within "+chunkSize + " characters, " +
                "and when you return each answer the answer must be start with " +START_OF_DOCUMENT_DELIMITER
                +" and end with"+ END_OF_DOCUMENT_DELIMITER+
                ". \r Please respond in the requested language.";
    }
}
