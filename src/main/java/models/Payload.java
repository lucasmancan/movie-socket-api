package models;

public class Payload {
    private String content;

    @Override
    public String toString() {
        return String.format("%d:%s", this.content.getBytes().length, this.content);
    }

    public Payload(String content) {
        this.content = content;
    }

    public Payload() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
