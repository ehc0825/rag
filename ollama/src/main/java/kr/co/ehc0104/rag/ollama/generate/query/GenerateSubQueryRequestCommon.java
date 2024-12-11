package kr.co.ehc0104.rag.ollama.generate.query;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.regex.Pattern;

public class GenerateSubQueryRequestCommon<T extends GenerateSubQueryRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private static final String QUERY_DELIMITER = "<QD>";
    private static final String START_OF_SUB_QUERY_DELIMITER = "<SOSQ>";
    private static final String END_OF_SUB_QUERY_DELIMITER = "<EOSQ>";
    private static final String SUB_QUERY_REGEX_PATTERN_STRING = START_OF_SUB_QUERY_DELIMITER + "(.*?)" + END_OF_SUB_QUERY_DELIMITER;
    public static final Pattern SUB_QUERY_REGEX_PATTERN = Pattern.compile(SUB_QUERY_REGEX_PATTERN_STRING, Pattern.DOTALL);
    public static final String SUB_QUERY_FIELD_NAME = "generated_sub_query";
    public final String query;

    protected GenerateSubQueryRequestCommon(boolean stream, String query) {
        super(stream);
        this.query = query;
    }


    @Override
    protected String prompt() {
        return QUERY_DELIMITER+query+QUERY_DELIMITER;
    }

    protected String systemPrompt() {
        return "You are an assistant that generates sub-queries from the user request.\r" +
                "You must follow these guidelines:\r" +
                "1. The user request will be enclosed within query delimiters (" + QUERY_DELIMITER + ").\r" +
                "2. Ensure that the sub-queries do not overlap in content and cover different parts of the user's request.\r" +
                "3. Each sub-query must be generated in the language of the enclosed user request.\r" +
                "4. Each sub-query must start with " + START_OF_SUB_QUERY_DELIMITER +
                " and end with " + END_OF_SUB_QUERY_DELIMITER + ".\r" +
                "5. Do not provide any explanation about the delimiters.\r" +
                "6. Do not include the answers to the sub-queries in your response.\r" +
                "7. Generate only the sub-queries based on the distinct topics within the user's request;" +
                " do not create any additional sub-queries.";
    }
}
