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

            final Payload responsePayload = new Payload(responseContent.length(), responseContent);

            sendMessage(responsePayload.toString());

        } catch (IOException e) {
            handleError(e);
        }finally {
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
    private Payload getRequestPayload() {

        final Optional<String> requestString = clientInputStreamToString();

        final Payload payload = new Payload();

        if (requestString.isPresent()) {
            payload.setContentLength(requestString.get().getBytes().length);
            payload.setContent(requestString.get());
        }

        return payload;
    }

    /**
     * @param
     * @return message sent by client in String format or null
     */
    private Optional<String> clientInputStreamToString() {
        String requestString = null;

        try {

            int incoming = clientSocket.getInputStream().read();

            StringBuilder queryContentSb = new StringBuilder();
            StringBuilder queryLengthSb = new StringBuilder();

            int queryLength = -1;

            while (incoming != -1) {
                char c = (char) incoming;

                if (c == ':') {
                    queryLength = Integer.parseInt(queryLengthSb.toString());
                } else if (queryLength > -1) {
                    queryContentSb.append(c);
                } else if (queryLength == -1 && Character.isDigit(c)) {
                    queryLengthSb.append(c);
                }

                if (queryContentSb.length() == queryLength)
                    break;

                incoming = clientSocket.getInputStream().read();

            }

            requestString = queryContentSb.toString();
        } catch (IOException ex) {
            return Optional.empty();
        }

        return Optional.of(requestString);
    }

    @Override
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}