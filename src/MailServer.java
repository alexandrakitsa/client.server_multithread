import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer {
    static MailServer  server = new MailServer();
    private static final int port=9090;
    private HashMap<String,Account> accounts;
    private ArrayList<Email> emails;
    private Account currentAccount;
    private static ArrayList<runable> clients= new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);


    public MailServer() {
            accounts= new HashMap<>();
            emails = new ArrayList<>();
    }

    public void register(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF("Enter your username: ");
        String username = in.readUTF();
        while (accounts.containsKey(username)) {
            out.writeUTF("The user already exists, please enter a different username : ");
            username = in.readUTF();
        }
        out.writeUTF("Enter your password: ");
        String password = in.readUTF();
        out.writeUTF("Re-enter your password: ");
        String p;
        p = in.readUTF();
        while (!password.equals(p)) {
            out.writeUTF("Password doesn't match, please enter your password again: ");
            p = in.readUTF();
        }
        out.writeUTF("welcome "+username);
        Account newAccount = new Account(username, p);
        accounts.put(username, newAccount);
        currentAccount = newAccount;
    }
    public void logIn(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF("Enter your username: ");
        String username = in.readUTF();
        while (!accounts.containsKey(username)) {
            out.writeUTF("The user doesn't exist, please enter a valid username : ");
            username = in.readUTF();
        }
        out.writeUTF("Enter your password: ");
        String password = in.readUTF();
        while (!accounts.get(username).isPasswordCorrect(password)) {
            out.writeUTF("Incorrect password, please enter your password again: ");
            password = in.readUTF();
        }
        out.writeUTF("welcome back "+username);
        currentAccount = accounts.get(username);
    }
    public void newEmail(Socket client) throws IOException {
        Email newemail= new Email(currentAccount.getUsername());
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        String temp;
        //out.println("New Email");
        out.writeUTF("Send email to: ");
       // out.print("To: ");
        //System.out.println("wait....");
        String to= in.readUTF();
        while(!accounts.containsKey(to)) {
            out.writeUTF("Type a valid user: ");
            to=in.readUTF();
        }
        //out.println(to);
        newemail.setReceiver(to);
        out.writeUTF("Subject: ");
        temp= in.readUTF();
        newemail.setSubject(temp);
      //  out.println(temp);
        out.writeUTF("Mainbody: ");
        temp= in.readUTF();
        newemail.setMainbody(temp);
     //   out.println(temp);

        accounts.get(currentAccount.getUsername()).addToMailbox(newemail);
        accounts.get(to).addToMailbox(newemail);
        out.writeUTF("---------------");
        out.writeUTF("mail server");
    }
    public void showEmails(Socket client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF("Id    From               Subject");
        int i=0;
        emails = currentAccount.getMailbox();
        for(Email e : emails) {
            i++;
            if(e.isNew() && !e.getSender().equals(currentAccount.getUsername()))
                out.writeUTF(i+"."+"[new]"+e.getSender()+"                   "+e.getSubject());
            else
                out.writeUTF(i+"."+"    "+e.getSender()+"                   "+e.getSubject());
        }
        out.writeUTF("---------------");
        out.writeUTF("mail server");
    }
    public void readEmails(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        out.writeUTF("Please enter user ID: ");
        int id= Integer.parseInt(in.readUTF());
        if(emails.size()<id-1)
        {
            out.writeUTF("The email Id you have entered is incorrect.");
            return;
        }
        Email mail = emails.get(id-1);
        emails.get(id-1).setNew(false);
        out.writeUTF("Send From: "+mail.getSender()+"\nTo: "+mail.getReceiver()+"\nSubject: "+mail.getSubject()+"\nMain Body: \n"+mail.getMainbody()+"\n---------------\nmail server");
    }
    public void deleteEmail(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        out.writeUTF("Give me the Id of the email you want to delete: ");
        int id = Integer.parseInt(in.readUTF());
        if(emails.size()<id-1)
        {
            out.writeUTF("The email Id you have entered is incorrect.");
            return;
        }
        emails.remove(id-1);
        out.writeUTF("Email removed successfully from mailbox. \n---------------\nmail server");
    }
    public void logOut(Socket client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());        currentAccount=null;
        out.writeUTF("You have been logged out. See you again soon!");
    }
    //public void exit() {}

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(port);
        while(true) {
            System.out.println("server waiting for client connection...");
            Socket client = listener.accept();
            System.out.println("client connected to the server!");
            runable clientThread = new runable(client);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }

}
