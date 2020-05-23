package models;

public class Payload {
    private Integer contentLength;
    private String content;

    @Override
    public String toString() {
        return String.format("%d:%s", this.contentLength, this.content);
    }

    public Payload(Integer contentLength, String content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public Payload() {

    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
