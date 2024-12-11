package kr.co.ehc0104.rag.ollama.generate.rag;

import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.List;

public class RagRequestCommon<T extends RagRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private static final String CONTEXT_DELIMITER = "$$$";
    private static final String QUESTION_DELIMITER = "#%";


    protected final List<Context> contexts;
    private final String query;

    protected RagRequestCommon(boolean stream, List<Context> contexts, String query) {
        super(stream);
        this.contexts = contexts;
        this.query = query;
    }

    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QUESTION_DELIMITER).append(query).append(QUESTION_DELIMITER);
        return stringBuilder.toString();
    }

    @Override
    protected String systemPrompt() {
        StringBuilder prompt = new StringBuilder("You are an assistant that generates responses to user " +
                "questions based on the provided contexts.\r" +
                "The given contexts will be provided enclosed with " + CONTEXT_DELIMITER + "\r" +
                "The user question will be provided enclosed with " + QUESTION_DELIMITER + "\r" +
                "Here are the contexts:\r");
        for (Context context : contexts) {
            prompt.append(CONTEXT_DELIMITER).append(context.context()).append(CONTEXT_DELIMITER).append("\r");
        }
        prompt.append("Please refine your answer for a better user experience," +
                " but make sure not to generate responses based on information outside the provided context.\r"
                + "You must exclude the delimiters related to the question, context, and page numbers from your response.\r");
        prompt.append("This is very important!!!! You must Answer in the language");
        prompt.append("enclosed with the question").append(QUESTION_DELIMITER).append("\r");
        prompt.append("Do not create responses based on your imagination; only refer to the provided contexts." +
                " If you don't know the answer, simply say you don't know. in the language the user asked the question.\r");
        return prompt.toString();
    }

    public List<Context> contexts() {
        return contexts;
    }

}