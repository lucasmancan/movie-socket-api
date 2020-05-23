package exceptions;

import java.io.IOException;

public class MessageFormatException extends IOException {
    public MessageFormatException(String message) {
        super(message);
    }
}
