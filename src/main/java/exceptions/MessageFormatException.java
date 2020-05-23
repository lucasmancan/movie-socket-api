package exceptions;

import java.io.IOException;

public class MessageFormatException extends IOException {
    public MessageFormatException(String message) {
        super(String.format("%d:%s", message.length(), message));
    }
}
