import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TCPServerTest {

    static TCPServer server;
    static Socket client;

    @BeforeClass
    public static void init() throws IOException {
        server = new TCPServer(8085);
        new Thread(server).start();
    }

    @Before
    public void beforeEach() throws IOException {
        client = new Socket("127.0.0.1", 8085);
    }

    @After
    public void finalize() throws IOException {
        client.close();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
    }

    @Test
    public void shoudMatchToResponsePattern() throws IOException {

        final String query = "teste";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertTrue(sb.toString().matches("(\\d+:.*)"));
    }

    @Test
    public void shoudMatchToResponseinvalidMessagePatternErro() throws IOException {

        final String query = "teste";


        /*Invalid Message format*/
        final String message = String.format("8asdas%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("The message provided by client: 8asdas5 is not valid, it must follow the pattern '<query length>:query'", sb.toString());
    }

    @Test
    public void shoudMatchToResponseinvalidQueryLengthErro() throws IOException {

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

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb;
    }
}
