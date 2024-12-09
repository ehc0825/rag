package kr.co.ehc0104.rag.nlp;

import java.util.List;

public record ClusteringResponse(List<List<String>> clusters, int clustersSize){}
