package kr.co.ehc0104.rag.ollama.chat;


import lombok.ToString;

@ToString
public class ChatHistory{

    private final String role;
    private final String content;

    public String getRole() {
        return role;
    }
    public String getContent() {
        return content;
    }

    public ChatHistory(Role role, String content) {
        this.role = role.value;
        this.content = content;
    }
    public ChatHistory(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public enum Role {
        USER(),
        SYSTEM(),
        ASSISTANT();
        private final String value;
        Role() {
            this.value = name().toLowerCase();
        }

    }
}
