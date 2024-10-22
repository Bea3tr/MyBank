package bank;

import java.net.*;
import java.util.concurrent.*;
import java.io.*;

public class Server {

    public static void main(String[] args) throws IOException {
        
        ExecutorService thrPool = Executors.newFixedThreadPool(3);
        // Set default port;
        int port = 3000;

        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket server = new ServerSocket(port);
        System.out.println("Waiting for connection...");

        while(true) {
            Socket sock = server.accept();
            System.out.println("Connection established!");
            ClientHandler handler = new ClientHandler(sock);
            thrPool.submit(handler);
        }
    }
}
