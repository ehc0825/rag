package kr.co.ehc0104.rag.ollama.tag;

import java.util.List;

public record ModelTagDetail(String parentModel, String format, String family, List<String> families, String parameterSize, String quantizationLevel) {
}