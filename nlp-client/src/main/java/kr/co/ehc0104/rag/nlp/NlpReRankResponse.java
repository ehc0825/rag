package kr.co.ehc0104.rag.nlp;

import java.util.List;

public record NlpReRankResponse(List<Context> result) {

    public record Context(String context,double score) {
    }
}
