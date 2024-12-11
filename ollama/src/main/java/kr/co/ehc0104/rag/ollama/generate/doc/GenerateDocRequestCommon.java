package kr.co.ehc0104.rag.ollama.generate.doc;


import kr.co.ehc0104.rag.ollama.generate.OllamaGenerateRequest;

import java.util.List;

public class GenerateDocRequestCommon<T extends GenerateDocRequestCommon<T>> extends OllamaGenerateRequest<T> {

    private final List<String> indexList;
    private final String subject;
    private final Hint hint;
    private String preGeneratedDocument;

    protected GenerateDocRequestCommon(boolean stream, String subject, List<String> indexList, Hint hint) {
        super(stream);
        this.indexList = indexList;
        this.subject = subject;
        this.hint = hint;
    }


    @Override
    protected String prompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hint.indexNum)
                .append(". ").append(indexList.get(hint.indexNum)).append(" regarding \r");
        stringBuilder.append("Create a document based on the following hint\r");
        stringBuilder.append("Hint: ").append(hint.indexHint);
        return stringBuilder.toString();
    }

    @Override
    protected String systemPrompt() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are an assistant that generates a document according to the provided table of" +
                " contents based on the given subject.\r");
        stringBuilder.append("The subject is as follows:\r");
        stringBuilder.append(subject).append("\r");
        stringBuilder.append("The table of contents is as follows:\r");
        int indexNum = 0;
        for (String index : indexList) {
            stringBuilder.append("Index ").append(indexNum).append(". ").append(index).append("\r");
            indexNum++;
        }
        stringBuilder.append("The user will provide hints for each section in the table of contents," +
                " and you should create an example document based on those hints.\r");
        if(preGeneratedDocument != null){
            stringBuilder.append("The user has provided a pre-generated document. Please use this as a reference.\r");
            stringBuilder.append(preGeneratedDocument);
        }
        stringBuilder.append("It would be good if you could answer in the language the user asked the question. ");
        stringBuilder.append("\r");
        return stringBuilder.toString();
    }

    /**
     * 문서 생성 hint (사전생성 문서 추가) max last 3 개
     * @param preGeneratedDocument 사전생성 문서
     * @return this
     */
    public T withPreGeneratedDocument(List<String> preGeneratedDocument) {
        int size = preGeneratedDocument.size();
        int startIndex = Math.max(0, size - 3);

        List<String> usePreGeneratedDocument = preGeneratedDocument.subList(startIndex, size);
        List<String> useIndexList = indexList.subList(startIndex, indexList.size()); // indexList도 동일하게 처리

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < usePreGeneratedDocument.size(); i++) {
            stringBuilder.append(i).append(".").append(useIndexList.get(i)).append("\r");
            stringBuilder.append(usePreGeneratedDocument.get(i)).append("\r");
        }

        this.preGeneratedDocument = stringBuilder.toString();
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }




    public static class Hint{
        private final int indexNum;
        private final String indexHint;

        /**
         * 문서 생성 hint
         * @param indexNum 목차 인덱스 번호
         * @param indexHint 생성 문서에 대한 힌트
         */
        public Hint(int indexNum, String indexHint) {
            this.indexNum = indexNum;
            this.indexHint = indexHint;
        }
    }
}