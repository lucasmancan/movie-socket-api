package services;

import services.interfaces.TCPResquestHandler;
import services.interfaces.TCPServer;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServerImpl implements TCPServer {
    static Logger logger = Logger.getLogger(TCPServerImpl.class.getName());

    private  TCPResquestHandler resquestHandler;

    @Inject
    public TCPServerImpl(TCPResquestHandler resquestHandler) {
        this.resquestHandler = resquestHandler;
    }

    protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();

        while(!isStopped()){
            try {
                Socket clientSocket = this.serverSocket.accept();

                resquestHandler.setClientSocket(clientSocket);

                this.threadPool.execute(resquestHandler);

            } catch (IOException e) {
                if(isStopped()) {
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
        }
        this.threadPool.shutdown();
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    @Override
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
            logger.info("Server stopped!");
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    @Override
    public void setPort(int serverPort) {
        this.serverPort = serverPort;
    }

    private void openServerSocket() {
        try {

            this.serverSocket = new ServerSocket(this.serverPort);
            logger.fine(String.format("TCP Server is listening on port %d", this.serverPort));
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("Cannot open port %d", this.serverPort), e);
            throw new RuntimeException(String.format("Cannot open port %d", this.serverPort), e);
        }
    }
}