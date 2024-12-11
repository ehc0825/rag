package kr.co.ehc0104.rag.ollama.generate.recommend;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.List;
import java.util.regex.Pattern;

public class RecommendQuestionRequestCommon <T extends RecommendQuestionRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private static final String CONTEXT_DELIMITER = "$$$";
    private static final String QUESTION_DELIMITER = "@@@";

    public static final String QUESTION_LIST_FIELD_NAME = "question_list";

    public static final Pattern QUESTION_REGEX_PATTERN = Pattern.compile("\\d+\\.\\s+(.*)");


    protected final List<Context> contexts;
    private final String query;
    private final int maxQuestions;

    protected RecommendQuestionRequestCommon(boolean stream, List<Context> contexts, String query, int maxQuestions) {
        super(stream);
        this.contexts = contexts;
        this.query = query;
        this.maxQuestions = maxQuestions;
    }


    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Do not answering this question directly. You are an assistant to generate questions.");
        stringBuilder.append(QUESTION_DELIMITER).append(query).append(QUESTION_DELIMITER);
        stringBuilder.append("The table of contents must follow the format: 1., 2., 3., with each item separated by a line break.\r" +
                "The questions must match the language enclosed by " + QUESTION_DELIMITER + "(priority language is korean.), and they should align closely " +
                "with the provided contexts to help the user find specific answers.\r" +
                "Avoid generating vague or generic questions. Ensure each question is actionable and directly addresses the context.\r" +
                "The total number of generated questions should not exceed " + maxQuestions + ".");
        return stringBuilder.toString();
    }

    @Override
    protected String systemPrompt() {
        StringBuilder prompt = new StringBuilder("You are an assistant to generate questions " +
                " based on the provided contexts.\r" +
                "The contexts will be provided enclosed with " + CONTEXT_DELIMITER + ".\r" +
                "The user's question will be provided enclosed with " + QUESTION_DELIMITER + ".\r" +
                "Here are the contexts:\r");
        for (Context context : contexts) {
            prompt.append(CONTEXT_DELIMITER).append(context.context()).append(CONTEXT_DELIMITER).append("\r");
        }
        prompt.append("This is Important: You are not Allowed to answering question you task is just to generate questions based on the context provided." +
                " The questions must match the language enclosed by " + QUESTION_DELIMITER);
        return prompt.toString();
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

}