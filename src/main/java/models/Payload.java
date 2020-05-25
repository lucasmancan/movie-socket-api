package models;

public class Payload {
    private Long contentLength = 0L;
    private String content;

    @Override
    public String toString() {
        return String.format("%d:%s", this.contentLength, this.content);
    }

    public Payload(Long contentLength, String content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public Payload() {

    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
