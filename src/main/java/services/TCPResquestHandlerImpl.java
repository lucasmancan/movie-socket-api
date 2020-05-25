package services;

import exceptions.MessageFormatException;
import models.MovieOption;
import models.Payload;
import services.interfaces.MovieService;
import services.interfaces.TCPResquestHandler;

import javax.inject.Inject;
import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TCPResquestHandlerImpl implements TCPResquestHandler {

    static Logger logger = Logger.getLogger(TCPResquestHandlerImpl.class.getName());

    private Socket clientSocket;

    private final MovieService movieService;

    @Inject
    public TCPResquestHandlerImpl(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Deals with client resquest and query MovieService
     */
    public void run() {
        try {

            Payload requestPayload = getRequestPayload();

            final String responseContent = movieService.findAllByTitle(requestPayload.getContent())
                    .stream()
                    .map(MovieOption::getName)
                    .collect(Collectors.joining("\n"));

            final Payload responsePayload = new Payload((long) responseContent.length(), responseContent);

            sendMessage(responsePayload.toString());

        } catch (IOException e) {
            handleError(e);
        } finally {
            close();
        }
    }

    /**
     * Closes the client socket connection
     * After query movies list server will stop client connection
     */
    private void close() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            logger.severe("Error occurred while trying to close socket connection..");
        }
    }

    /**
     * Send message to socket client
     *
     * @param message
     * @return
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(message);
        out.println('\n');
    }

    /**
     * Handle a friendly error to socket client
     * For now a default message is sent
     *
     * @param ex
     */
    public void handleError(IOException ex) {
        try {
            OutputStream output = clientSocket.getOutputStream();

            final String errorMessage = "%d:An error occurred while processing your request, please check if you are following the protocol";

            output.write(String.format(errorMessage, errorMessage.getBytes().length).getBytes());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An Error occurred while processind the client request", e);
        }
    }

    /**
     * Cast InputStream to a common format 'models.Payload'
     *
     * @return
     */
    private Payload getRequestPayload() throws IOException {

        final Optional<String> requestString = clientInputStreamToString();

        final Payload payload = new Payload();

        if (requestString.isPresent()) {
            payload.setContentLength((long) requestString.get().getBytes().length);
            payload.setContent(requestString.get());
        }

        return payload;
    }

    /**
     * @param
     * @return message sent by client in String format or null
     */
    private Optional<String> clientInputStreamToString() throws IOException {
        String requestString = null;

        int incoming = clientSocket.getInputStream().read();

        StringBuilder queryContentSb = new StringBuilder();
        StringBuilder queryLengthSb = new StringBuilder();

        long queryLength = -1;

        while (incoming != -1) {
            char c = (char) incoming;

            try {
                if (c == ':') {
                    queryLength = parseToLong(queryLengthSb.toString());
                } else if (queryLength > -1) {
                    queryContentSb.append(c);
                } else if (queryLength == -1) {

                    if (!Character.isDigit(c)) {
                        throw new MessageFormatException("The server language is <query length>:query, please rewrite your message...");
                    }

                    queryLengthSb.append(c);
                }
            } catch (MessageFormatException e) {

                queryContentSb.setLength(0);
                queryLengthSb.setLength(0);

                sendMessage(e.getMessage());
            }

            if (queryContentSb.length() == queryLength)
                break;

            incoming = clientSocket.getInputStream().read();

        }

        requestString = queryContentSb.toString();


        return Optional.of(requestString);
    }

    private Long parseToLong(String queryLength) throws MessageFormatException {
        try {
            return Long.parseLong(queryLength);
        } catch (NumberFormatException e) {
            throw new MessageFormatException("The <query length> is out of 'Long' range, please rewrite your message...");
        }
    }

    @Override
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}