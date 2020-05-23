import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TCPServerTest {

    TCPServer server;
    Socket client;

    @Before
    public void init() throws IOException {
        this.server = new TCPServer(9000);

        new Thread(server).start();

        client = new Socket("127.0.0.1", 9000);

    }

    @Test
    public void shoudMatchtoResponsePattern() throws IOException {

        final String query = "test";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertTrue(sb.toString().matches("(\\d+:.*)"));
    }

    @Test
    public void shoudMatchtoResponseinvalidMessagePatternErro() throws IOException {

        final String query = "teste";


        /*Invalid Message format*/
        final String message = String.format("8asdas%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("The message provided by client: 8asdas5 is not valid, it must follow the pattern '<query length>:query'", sb.toString());
    }

    @Test
    public void shoudMatchtoResponseinvalidQueryLengthErro() throws IOException {

        final String query = "teste";


        /*Invalid query length*/
        final String message = String.format("56:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("The query length provided is not valid", sb.toString());
    }


    private void sendMessageToServer(String message) throws IOException {
        OutputStream output = client.getOutputStream();
        output.write(message.getBytes());
    }

    private StringBuilder getServerResponse() throws IOException {
        InputStreamReader isReader = new InputStreamReader(client.getInputStream());
        BufferedReader br = new BufferedReader(isReader);

        final StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine())!=null){
            sb.append(line);
        }
        return sb;
    }
}
