package services.interfaces;

import java.net.Socket;

public interface TCPResquestHandler extends Runnable {
    void setClientSocket(Socket clientSocket);
}