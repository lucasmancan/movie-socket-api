import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer implements Runnable {
    static Logger logger = Logger.getLogger(TCPServer.class.getName());

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public TCPServer(int port){
        this.serverPort = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();
        while(!isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    break;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }


            this.threadPool.execute(new TCPResquestHandler(clientSocket));
        }
        this.threadPool.shutdown();
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
            logger.info("Server stopped!");
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {

            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Cannot open port %d", this.serverPort), e);
            throw new RuntimeException(String.format("Cannot open port %d", this.serverPort), e);
        }
    }
}