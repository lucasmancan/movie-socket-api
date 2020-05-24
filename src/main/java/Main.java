import com.google.inject.Guice;
import com.google.inject.Injector;
import modules.DIModule;
import services.TCPResquestHandlerImpl;
import services.TCPServerImpl;
import services.interfaces.TCPServer;

import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        Injector injector = Guice.createInjector(new DIModule());

        TCPServer tcpServer = injector.getInstance(TCPServer.class);
        tcpServer.setPort(8085);

        new Thread(tcpServer).start();

        System.out.println("Press any key to stop de TCP server...");

        Scanner scanner = new Scanner(System.in);

        scanner.next();

        tcpServer.stop();
    }
}
