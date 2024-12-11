package kr.co.ehc0104.rag.ollama.generate.doc;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

public class TitleExtractionRequestCommon<T extends TitleExtractionRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private final String query;


    protected TitleExtractionRequestCommon(boolean stream, String query) {
        super(stream);
        this.query = query;
    }

    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Please extract the key contents from the following request").append("\r");
        stringBuilder.append("$$$").append("\r");
        stringBuilder.append(query);
        stringBuilder.append("$$$").append("\r");
        return stringBuilder.toString();
    }

    @Override
    protected String systemPrompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are an assistant that extracts only one sentence of key contents from the user " +
                "request\r");
        stringBuilder.append("The answer must be only a term of key contents");
        return stringBuilder.toString();
    }
}