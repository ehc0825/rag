package kr.co.ehc0104.rag.ollama.tag;

public record ModelTag(String name, String model, String modifiedAt, long size, String digest, ModelTagDetail details) {
}

