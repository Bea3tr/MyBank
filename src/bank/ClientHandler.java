package bank;

import java.net.Socket;
import java.io.*;
import java.util.*;

public class ClientHandler implements Runnable {

    private final Socket sock;

    public ClientHandler(Socket s) {
        this.sock = s;
    }

    @Override 
    public void run() {

        try {
            // Get input & output streams 
            InputStream is = sock.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

            OutputStream os = sock.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeUTF("Welcome to MyBank!\nPlease enter your name: ");
            dos.flush();

            String name = dis.readUTF();
            String pin = "";

            // Check database for user
            File db = new File("database");
            List<File> userList = Arrays.asList(db.listFiles());
            // userList.forEach(System.out::println);

            Bank userBank = new Bank();
            
            if(!userList.contains(new File("database/" + name + ".txt"))) {
                // Register user
                dos.writeUTF("You are not a registered user. Would you like to create an account (y/n)?: ");
                dos.flush();
                if(dis.readUTF().equals("y")) {
                    dos.writeUTF("Please create a 6 digit pin number: ");
                    dos.flush();

                    pin = dis.readUTF();
                    boolean isValid = true;

                    int num = 0;
                    // Check if pin is valid
                    for (char c : pin.toCharArray()) {
                        if(!Character.isDigit(c)) {
                            isValid = false;
                            break;
                        }
                        num++;
                    }
                    if(num != 6)
                        isValid = false;

                    while(!isValid) {
                        dos.writeUTF("Invalid pin (Please enter digits only): ");
                        dos.flush();

                        pin = dis.readUTF();

                        int count = 0;
                        for (char c : pin.toCharArray()) {
                            if(!Character.isDigit(c)) {
                                isValid = false;
                                break;
                            } else {
                                count++;
                            }
                        }
                        if(count == 6)
                            isValid = true;
                    }

                    // Create bank account
                    userBank = new Bank(name, pin);
                    // Create file for user details
                    userBank.update();
                } else {
                    dos.writeUTF("Terminating operation...");
                    dos.flush();

                    dos.close();
                    bos.close();
                    os.close();

                    dis.close();
                    bis.close();
                    is.close();

                    sock.close();
                    System.exit(0);
                }
            } else {
                userBank = new Bank(name);
                dos.writeUTF("Please enter your pin number: ");
                dos.flush();

                pin = dis.readUTF();
                // Check if pin is correct
                String correctPin = userBank.getPin();

                int attempts = 3;
                while(!pin.equals(correctPin)) {
                    attempts--;
                    dos.writeUTF("Incorrect pin, please try again (attempts: %d): ".formatted(attempts));
                    dos.flush();

                    pin = dis.readUTF();
                    if(attempts == 0) {
                        dos.writeUTF("Maximum number of attempts reached. Terminating operation...");
                        dos.flush();

                        dos.close();
                        bos.close();
                        os.close();

                        dis.close();
                        bis.close();
                        is.close();

                        sock.close();
                        System.exit(0);
                    }
                }
                dos.writeUTF("Login successful!");
                dos.flush();
            }
            String op = "";

            while(true) {
                dos.writeUTF("How can we assist you today?\n[Deposit]\t[Withdraw]\t[Balance]\t[Update]\t[Quit]: ");
                dos.flush();
                op = dis.readUTF().toLowerCase();
                switch(op){
                    case "deposit":
                        dos.writeUTF("Deposit: ");
                        dos.flush();
                        userBank.deposit(Float.parseFloat(dis.readUTF()));
                        break;
                        
                    case "withdraw":
                        dos.writeUTF("Withdraw: ");
                        dos.flush();
                        float amt = Float.parseFloat(dis.readUTF());
                        if(amt > userBank.getAccBal()) {
                            dos.writeUTF("Insufficient balance, redirecting back to menu...");
                            dos.flush();
                            break;
                        } else {
                            userBank.withdraw(amt);
                        }
                        break;
    
                    case "balance":
                        dos.writeUTF(userBank.getBal());
                        dos.flush();
                        break;
                        
                    case "update":
                        userBank.update();
                        break;

                    case "quit":
                        System.out.printf("Terminating operation for user %s\n", name);
                        dos.writeUTF("Thank you for banking with us! See you again!");
                        dos.flush();

                        dos.close();
                        bos.close();
                        os.close();

                        dis.close();
                        bis.close();
                        is.close();
                        sock.close();
                        System.exit(0);
                        break;
                        
                    default:
                        dos.writeUTF("Invalid input, please try again");
                }                 
            }

        } catch(IllegalArgumentException ex) {
            System.err.println(ex.toString());
        } catch(IOException ex) {
            System.err.println("Error processing I/O operations");
            ex.printStackTrace();
        } 
    }

}
