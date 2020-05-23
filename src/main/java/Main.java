import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        TCPServer server = new TCPServer(8085);
        new Thread(server).start();

        System.out.println("Press any key to stop de TCP server...");

        Scanner scanner = new Scanner(System.in);

        scanner.next();

        server.stop();
    }
}
