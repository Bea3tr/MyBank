package bank;

import java.security.SecureRandom;
import java.util.*;
import java.io.*;

public class Bank {

    Random rand = new SecureRandom();
    private final String name;
    private final String accNum; 
    private final String pin;
    private File userAcc;
    private float accBal;
    private List<String> transactions;

    public String getName() {return name;}
    public String getAccNum() {return accNum;}
    public String getPin() {return pin;}
    public File getUserAcc() {return userAcc;}
    
    public float getAccBal() {return accBal;}
    public void setAccBal(float accBal) {this.accBal = accBal;}

    public List<String> getTransactions() {return transactions;}

    public Bank() {
        this.name = "";
        this.accNum = "";
        this.pin = "";
    }

    public Bank(String name, String pin) {
        this.name = name;
        this.pin = pin;
        this.accBal = 0f;
        this.userAcc = new File("database/" + name + ".txt");
        this.transactions = new ArrayList<>();
        this.accNum = "%s-%s-%s-%d".formatted(String.format("%03d", rand.nextInt(999)), 
                                                String.format("%03d", rand.nextInt(999)), 
                                                String.format("%03d", rand.nextInt(999)), 
                                                rand.nextInt(10));
    }

    public void deposit(float amt) {
        if(amt < 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        accBal += amt;
        transactions.add("[%tc] Deposited $%.2f\tCurrent balance: %.2f".formatted(new Date(), amt, accBal));
    }

    public void withdraw(float amt) {
        if(amt < 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        } else if(amt > accBal) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        accBal -= amt;
        transactions.add("[%tc] Withdrawn $%.2f\tCurrent balance: %.2f".formatted(new Date(), amt, accBal));
    }

    public String getBal() {
        return "Current balance: %f\n".formatted(getAccBal());
    }

    // Only use when user exists
    public void setBal() throws FileNotFoundException, IOException {
        Reader read = new FileReader(userAcc);
        BufferedReader br = new BufferedReader(read);

        String line = "";
        String readBal = "";
        while(line != null) {
            line = br.readLine();
            if(line == null)
                break;
            if(line.startsWith("[")) {
                String[] details = line.split(" ");
                // The last String in line, continue updating until last entry
                readBal = details[details.length - 1];
            }
        }
        br.close();
        if(!readBal.equals("")) {
            setAccBal(Float.parseFloat(readBal));
        }
    }

    public void update() throws IOException {
        Writer account;
        BufferedWriter bw;

        if(!userAcc.exists()) {
            account = new FileWriter(userAcc);
            bw = new BufferedWriter(account);
            bw.write("Account no.: %s\n".formatted(accNum));
            bw.write("Account holder: %s\n".formatted(name));
            bw.write("Pin: %s\n".formatted(pin));
            bw.write("Account Balance: %f\n".formatted(accBal));

        } else {
            account = new FileWriter(userAcc, true);
            bw = new BufferedWriter(account);
            setBal();
        }
        
        transactions.forEach(trans -> {
            try {
                bw.write(trans + "\n");
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        });
        bw.flush();
        account.flush();
        bw.close();
        account.close();
    }
}