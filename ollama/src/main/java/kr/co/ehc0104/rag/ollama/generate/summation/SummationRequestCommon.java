package kr.co.ehc0104.rag.ollama.generate.summation;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.List;

public class SummationRequestCommon<T extends SummationRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private final String paragraph;
    private final String fileName;

    protected SummationRequestCommon(boolean stream, String fileName, List<String> sentences) {
        this(stream, fileName, String.join("\n", sentences));
    }

    protected SummationRequestCommon(boolean stream, String fileName, String paragraph) {
        super(stream);
        this.fileName = fileName;
        this.paragraph = paragraph;
    }

    @Override
    protected String prompt() {
        return "Here is a subset of the text from the document " + fileName + ".\n"+
                "Document:" + paragraph;
    }

    @Override
    protected String systemPrompt() {
        return "Your summary must be 3-4 sentences long, precise, and thorough." +
                " Provide a detailed summary of the document." +
                " You must respond in the language of the document." +
                " Provide only the summary without any explanations or additional comments. If you don't get the document," +
                " you must answer 'I don't have enough information to provide a summary.' in the language of request.";
    }
}
