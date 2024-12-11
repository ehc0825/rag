package kr.co.ehc0104.rag.ollama.chat.rag;


import kr.co.ehc0104.rag.ollama.OllamaChatRequest;
import kr.co.ehc0104.rag.ollama.generate.rag.Context;

import java.util.ArrayList;
import java.util.List;

public class ChatRagRequestCommon<T extends ChatRagRequestCommon<T>> extends OllamaChatRequest<T> {


    private static final int MAX_CONTEXT_SIZE = 4;

    private static final String CONTEXT_DELIMITER = "Context:";
    private static final String QUESTION_DELIMITER = "#%";
    private static final String PAST_CONTEXT_TAG = "[PastContext]";

    protected final List<Context> contexts;

    protected String additionalSystemPrompt;

    protected ChatRagRequestCommon(boolean stream, List<Context> contexts, String query) {
        super(stream, query);
        if (contexts.size() >= MAX_CONTEXT_SIZE) {
            this.contexts = new ArrayList<>(contexts.subList(0, MAX_CONTEXT_SIZE));
        } else {
            this.contexts = contexts;
        }
    }

    protected ChatRagRequestCommon(boolean stream, List<Context> contexts, String query, int historySize) {
        super(stream, query, historySize);
        if (contexts.size() >= MAX_CONTEXT_SIZE) {
            this.contexts = new ArrayList<>(contexts.subList(0, MAX_CONTEXT_SIZE));
        } else {
            this.contexts = contexts;
        }
    }

    /**
     * 문맥 추가 (기존 문맥 앞에 추가)
     *
     * @param additionalContexts 추가 문맥
     * @return this
     */
    public ChatRagRequestCommon<T> pushContext(List<Context> additionalContexts) {
        addPastContextTag();
        List<Context> updatedContexts = mergeContexts(additionalContexts, contexts);
        contexts.clear();
        contexts.addAll(updatedContexts);
        return this;
    }


    private List<Context> mergeContexts(List<Context> newContexts, List<Context> existingContexts) {
        List<Context> mergedContexts = new ArrayList<>();
        List<Context> removeDuplicate = new ArrayList<>();
        for (Context context : newContexts) {
            if (!existingContexts.contains(context)) {
                removeDuplicate.add(context);
            }
        }
        mergedContexts.addAll(truncateContexts(removeDuplicate, MAX_CONTEXT_SIZE / 2 + 1));
        mergedContexts.addAll(truncateContexts(existingContexts, MAX_CONTEXT_SIZE / 2 - 1));
        return mergedContexts;
    }

    private List<Context> truncateContexts(List<Context> contexts, int maxSize) {
        return contexts.size() > maxSize ? new ArrayList<>(contexts.subList(0, maxSize)) : new ArrayList<>(contexts);
    }


    private void addPastContextTag() {
        for (int i = 0; i < contexts.size(); i++) {
            Context context = contexts.get(i);
            contexts.set(i, new Context(context.filePath(), PAST_CONTEXT_TAG + context.context() + PAST_CONTEXT_TAG));
        }
    }

    /**
     * 문맥 초기화
     *
     * @return this
     */
    public ChatRagRequestCommon<T> clearContext() {
        this.contexts.clear();
        return this;
    }


    /**
     * 추가적인 시스템프롬프트 세팅
     *
     * @param additionalSystemPrompt 추가적인 시스템프롬프트
     * @return this
     */
    public ChatRagRequestCommon<T> additionalSystemPrompt(String additionalSystemPrompt) {
        this.additionalSystemPrompt = additionalSystemPrompt;
        return this;
    }

    private List<Context> truncateContextList(List<Context> contextList, int adjustableSize) {
        if ((MAX_CONTEXT_SIZE / 2) + adjustableSize >= MAX_CONTEXT_SIZE) {
            adjustableSize = 0;
        }
        if (contextList.size() > MAX_CONTEXT_SIZE / 2) {
            return new ArrayList<>(contextList.subList(0, (MAX_CONTEXT_SIZE / 2) + adjustableSize));
        }
        return new ArrayList<>(contextList);
    }

    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(QUESTION_DELIMITER).append(query()).append(QUESTION_DELIMITER);
        return stringBuilder.toString();
    }

    @Override
    protected String getSystemPrompt() {
        StringBuilder prompt = new StringBuilder("You are an assistant designed to find information" +
                " from the provided contexts to answer user questions.\n" +
                "The contexts will be provided using " + CONTEXT_DELIMITER + ".\n" +
                "The user question will be enclosed within " + QUESTION_DELIMITER + ".\n" +
                "Here are the contexts:\n");
        int i = 0;
        for (Context context : contexts) {
            prompt.append("** ").append(i).append(".").append(CONTEXT_DELIMITER)
                    .append("*** ").append("file:").append(context.filePath()).append("\n")
                    .append("*** ").append("content:").append(context.context()).append("\n")
                    .append("\n");
            i++;
        }
        prompt.append("You must strictly adhere to the following rules:\n");
        prompt.append("1. Refine your responses to enhance the user experience," +
                " but you must only use information found within the " + CONTEXT_DELIMITER + ". " +
                "Your answers should be strictly based on the information provided within the " + CONTEXT_DELIMITER +
                " and must be given in the language requested by the user(priority language is korean).\n");
        prompt.append("2. Even if you are asked about information you already know, if it is not included within the "
                + CONTEXT_DELIMITER + ", you must respond with \"don't know\" in the language requested by the user.\r");
        if (additionalSystemPrompt != null) {
            prompt.append("this is additional prompt: ");
            prompt.append(additionalSystemPrompt);
        }
        return prompt.toString();
    }

    public List<Context> contexts() {
        return contexts;
    }

}
