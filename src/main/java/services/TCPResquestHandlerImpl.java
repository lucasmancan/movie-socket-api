package services;

import exceptions.MessageFormatException;
import models.MovieOption;
import models.Payload;
import services.interfaces.MovieService;
import services.interfaces.TCPResquestHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TCPResquestHandlerImpl implements TCPResquestHandler {

    static Logger logger = Logger.getLogger(TCPResquestHandlerImpl.class.getName());

    private  Socket clientSocket;

    private final MovieService movieService;

    @Inject
    public TCPResquestHandlerImpl(MovieService movieService) {
//        this.clientSocket = clientSocket;
        this.movieService = movieService;
    }

    /**
     * Deals with client resquest
     */
    public void run() {
        try {

            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            Payload requestPayload = null;

            try{
                requestPayload = getRequestPayload(input);
            }catch (MessageFormatException messageFormatException){

                output.write(messageFormatException.getMessage().getBytes());
                output.close();
                input.close();

                throw messageFormatException;
            }

            final String responseContent = movieService.findAllByTitle(requestPayload.getContent())
                    .stream()
                    .map(MovieOption::getName)
                    .collect(Collectors.joining("\n"));

            final Payload responsePayload = new Payload(responseContent.length(), responseContent);

            output.write(responsePayload.toString().getBytes());
            output.close();
            input.close();
            logger.fine(String.format("Message was successfully processed for the client: %s", clientSocket.getRemoteSocketAddress()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An Error occurred while processind the client request", e);
        }
    }

    /**
     * Cast InputStream to a common format 'models.Payload'
     *
     * @param input
     * @return
     * @throws IOException if the client message does not follow the patter '<query length>:query'
     *                     or the 'query length' is not the real length of 'query' property
     */
    private Payload getRequestPayload(InputStream input) throws MessageFormatException {

        final String requestString = toString(input);

        /* Regex validates the pattern: '<query length>:query'*/
        if (requestString == null || !requestString.matches("(\\d+:.*)"))
            throw new MessageFormatException(String.format("The message provided by client: %s is not valid, it must follow the pattern '<query length>:query'", requestString));

        final String[] splitString = requestString.split(":", 2);

        final Payload payload = new Payload();

        payload.setContentLength(Integer.valueOf(splitString[0]));
        payload.setContent(splitString[1]);

        if (payload.getContent().length() != payload.getContentLength())
            throw new MessageFormatException("The query length provided is not valid");

        return payload;
    }

    /**
     *
     * @param input client connection data
     * @return message sent by client in String format or null
     */
    private String toString(InputStream input)  {

        String requestString = null;

        try {
            InputStreamReader isReader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isReader);

            final StringBuilder sb = new StringBuilder();

            while (br.ready()) {
                sb.append((char) br.read());
            }

            requestString = sb.toString();
        }catch (IOException ex){
            return null;
        }

        return requestString;
    }

    @Override
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}