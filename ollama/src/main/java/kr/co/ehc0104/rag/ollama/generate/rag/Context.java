package kr.co.ehc0104.rag.ollama.generate.rag;

//equals and hashcode 사용중, jdk 버전 변경하여 record 사용 불가시 수정 필요
public record Context(String filePath, String context) {}
