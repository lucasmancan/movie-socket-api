package services;

import com.google.inject.Guice;
import com.google.inject.Injector;
import modules.DIModule;
import org.junit.*;
import services.interfaces.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TCPServerImplTest {

    static TCPServer server;
    Socket client;

    @BeforeClass
    public static void init() {

        Injector injector = Guice.createInjector(new DIModule());

        server = injector.getInstance(TCPServer.class);

        server.setPort(9000);

        new Thread(server).start();

    }


    @AfterClass
    public static void afterClass() {
        server.stop();
    }


    @Before
    public void beforeEach() throws IOException {
        client = new Socket("127.0.0.1", 9000);
    }

    @After
    public void finalize() throws IOException {
        client.close();
    }

    @Test
    public void shoudMatchToResponsePattern() throws IOException {

        final String query = "ac/dc";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertTrue(sb.toString().matches("(\\d+:.*)"));
    }

    @Test
    public void shoudMatchToResponsePatternEmptyQuery() throws IOException {

        final String query = "";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("63:The <query> should not be empty, please rewrite your message...", sb.toString());
    }


    @Test
    public void shoudMatchToResponsePatternInvalidQueryLength() throws IOException {

        final String query = "1231231212312312312312312312312:12asdasasdasdas";

        final String message = String.format("%da:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("89:The <query length> is out of 'int' range and is not valid, please rewrite your message...", sb.toString());
    }

    @Test
    public void shoudMatchToResponsePatternInvalidQueryContent() throws IOException {

        final String query = "ac/*-*asdas:as";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertEquals("85:The <query> can only have alphanumeric chars and '/:', please rewrite your message...", sb.toString());
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
