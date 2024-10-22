package bank;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        // Set default port & host
        int port = 3000;
        String host = "";

        if (args.length <= 0) {
            System.err.println("Invalid number of arguments expexted");
        } else {
            String[] outputs = args[0].split(":");
            host = outputs[0];
            port = Integer.parseInt(outputs[1]);
        }

        Socket sock = new Socket(host, port);

        Console cons = System.console();

        // Get output & output streams
        InputStream is = sock.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);

        OutputStream os = sock.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);

        // Read output from Server first to start interaction
        String output = "";
        while(output != null) {
            output = dis.readUTF();
            if(output == null || output.startsWith("Thank") || output.contains("Terminating"))
                break;
            System.out.println(output);
            if(output.endsWith(": ")) {
                String input = cons.readLine(">>> ");
                dos.writeUTF(input);
                dos.flush();
            }
        }
        dos.close();
        bos.close();
        os.close();

        dis.close();
        bis.close();
        is.close();
        sock.close();
    }
    
}
