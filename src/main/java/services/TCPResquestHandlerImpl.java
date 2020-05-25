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

            final Payload responsePayload = new Payload(responseContent);

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

            output.write(ex.getMessage().getBytes());

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
            payload.setContent(requestString.get());
        }

        return payload;
    }

    /**
     * @param
     * @return message sent by client in String format or null
     */
    private Optional<String> clientInputStreamToString() throws IOException {

        final InputStream inputStream = this.clientSocket.getInputStream();

        final StringBuilder queryContentSb = new StringBuilder();
        final StringBuilder queryLengthSb = new StringBuilder();

        long queryLength = -1;

        int incoming = inputStream.read();

        while (incoming != -1) {
            char c = (char) incoming;

                if (queryLength > -1) {
                    queryContentSb.append(c);
                } else if (queryLength == -1) {

                    if (c == ':') {
                        queryLength = safeParseToLong(queryLengthSb.toString());
                    }

                    queryLengthSb.append(c);
                }

            if (queryContentSb.length() == queryLength)
                break;

            incoming = inputStream.read();

        }

        if(!queryContentSb.toString().matches("^[a-zA-Z0-9/:]+$")){
            throw new MessageFormatException("The <query> can only have alphanumeric chars and '/:', please rewrite your message...");
        }

        return Optional.of(queryContentSb.toString());
    }

    private Long safeParseToLong(String queryLength) throws MessageFormatException {
        try {

            final Long longValue = Long.parseLong(queryLength);

            if(longValue.equals(0L)){
                throw new MessageFormatException("The <query> should not be empty, please rewrite your message...");
            }

            return Long.parseLong(queryLength);
        } catch (NumberFormatException e) {
            throw new MessageFormatException("The <query length> is out of 'int' range and is not valid, please rewrite your message...");
        }
    }

    @Override
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}