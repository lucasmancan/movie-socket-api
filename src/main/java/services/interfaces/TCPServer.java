package services.interfaces;

public interface TCPServer extends Runnable{
    void stop();
    void setPort(int serverPort);
}
