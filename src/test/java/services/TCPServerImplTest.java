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

        final String query = "teste/*-a'";

        final String message = String.format("%d:%s", query.getBytes().length, query);

        sendMessageToServer(message);

        final StringBuilder sb = getServerResponse();

        assertTrue(sb.toString().matches("(\\d+:.*)"));
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
