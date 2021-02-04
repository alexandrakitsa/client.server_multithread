import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MailServer {
    private static final int port=9090;//port
    private HashMap<String,Account> accounts;
    private ArrayList<Email> emails;//to store user's mailbox
    private Account currentAccount; //stores current user for easier use
    private static ArrayList<Thread> clients= new ArrayList<>(); // stores threads
    private static ExecutorService pool = Executors.newFixedThreadPool(4); //for thread execution

    //constructor
    public MailServer() {
            accounts= new HashMap<>();
            emails = new ArrayList<>();
    }

    //user registration
    public void register(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());//variable to store the input from the user
        DataOutputStream out = new DataOutputStream(client.getOutputStream());//variable to send the information to the user
        out.writeUTF("Enter your username: ");//sends information to the user asking him to enter his username
        String username = in.readUTF();//reads that information
        while (accounts.containsKey(username)) { //in case of entering an existing username asks from user to enter a different username
            out.writeUTF("The user already exists, please enter a different username : ");
            username = in.readUTF();
        }
        out.writeUTF("Enter your password: ");//same for password
        String password = in.readUTF();
        out.writeUTF("Re-enter your password: ");//asks to re enter the password
        String p;
        p = in.readUTF();
        while (!password.equals(p)) {//checks if the passwords are matching
            out.writeUTF("Password doesn't match, please enter your password again: ");
            p = in.readUTF();
        }
        out.writeUTF("welcome "+username); //welcomes user (this way the client object will know to print the clients menu)
        Account newAccount = new Account(username, p); //creates new account
        accounts.put(username, newAccount);//adds this account to list
        currentAccount = newAccount;//sets current account as the new account created
    }
    public void logIn(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());//variable to store the input from the user
        DataOutputStream out = new DataOutputStream(client.getOutputStream());//variable to send the information to the user
        out.writeUTF("Enter your username: ");//sends information to the user asking him to enter his username
        String username = in.readUTF();//reads that information
        while (!accounts.containsKey(username)) {//if the username does not exist, asks the user to enter a valid username
            out.writeUTF("The user doesn't exist, please enter a valid username : ");
            username = in.readUTF();
        }
        out.writeUTF("Enter your password: ");//same with password
        String password = in.readUTF();
        while (!accounts.get(username).isPasswordCorrect(password)) {//checks if the password is the correct one depending to the given username
            out.writeUTF("Incorrect password, please enter your password again: ");
            password = in.readUTF();
        }
        out.writeUTF("welcome back "+username);
        currentAccount = accounts.get(username);
    }
    public void newEmail(Socket client) throws IOException {
        Email newemail= new Email(currentAccount.getUsername()); //creates a new email that is going to be send by the current user that is logged in
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        //asks from user all needed information for the email to be send
        String temp;
        out.writeUTF("Send email to: ");
        String to= in.readUTF();
        while(!accounts.containsKey(to)) {
            out.writeUTF("Type a valid user: "); //the receiver the user gives must be in accounts list
            to=in.readUTF();
        }
        newemail.setReceiver(to);
        out.writeUTF("Subject: ");
        temp= in.readUTF();
        newemail.setSubject(temp);
      //  out.println(temp);
        out.writeUTF("Mainbody: ");
        temp= in.readUTF();
        newemail.setMainbody(temp);
     //   out.println(temp);

        accounts.get(currentAccount.getUsername()).addToMailbox(newemail);//adds to sender mail box the email
        accounts.get(to).addToMailbox(newemail); //adds to the receivers mailbox the email
        out.writeUTF("---------------");
        out.writeUTF("mail server");
    }
    public void showEmails(Socket client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF("Id    From               Subject");
        int i=0;
        emails = currentAccount.getMailbox();//gets mailbox from current logged in user
        for(Email e : emails) { //shows to client all emails.
            i++;
            if(e.isNew() && !e.getSender().equals(currentAccount.getUsername()))//if the email is new and the sender it's not the current user we print the new next to id.
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
        if(emails.size()<id-1)//if the id provided by user abstracted by one is greater than the mailbox size
        {
            out.writeUTF("The email Id you have entered is incorrect."); //means that the email doesn't exist
            return;
        }
        //otherwise is sending to the client the requested email so he can print it
        Email mail = emails.get(id-1);
        emails.get(id-1).setNew(false);
        out.writeUTF("Send From: "+mail.getSender()+"\nTo: "+mail.getReceiver()+"\nSubject: "+mail.getSubject()+"\nMain Body: \n"+mail.getMainbody()+"\n---------------\nmail server");
    }
    public void deleteEmail(Socket client) throws IOException {
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        out.writeUTF("Give me the Id of the email you want to delete: ");
        int id = Integer.parseInt(in.readUTF());
        if(emails.size()<id-1)//if the id provided by user abstracted by one is greater than the mailbox size
        {
            out.writeUTF("The email Id you have entered is incorrect.");//means that the email doesnt exist to his mailbox
            return;
        }
        accounts.get(currentAccount.getUsername()).removeFromMailbox(emails.get(id-1));//removes email from user's mailbox
        currentAccount.removeFromMailbox(emails.get(id-1));//removes the email from the current account we created for easier use
        emails.remove(id-1); //removes email from current's users list of mails we created  for easier use
        out.writeUTF("Email removed successfully from mailbox. \n---------------\nmail server");
    }
    public void logOut(Socket client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        currentAccount=null; //sets current user as null
        out.writeUTF("You have been logged out. See you again soon!"); //sends message to client containing the words logged out so he can print
        //the main menu again.
    }
    public static void main(String[] args) throws IOException {
        MailServer  server = new MailServer();//creates a MailServer object so we can use its functions
        ServerSocket listener = new ServerSocket(port); //creates a server socket to listen to clients
        while(!pool.isTerminated()) {

            System.out.println("server waiting for client connection...");
            Socket client = listener.accept(); //accepts clients
            System.out.println("client connected to the server!");
            int n = 8; // Number of threads
            for (int i=0; i<n; i++)
            {
                Thread object = new Thread(new runable(client,server)); //creates thread for each client trying to connect to the server
                object.start(); //starts the thread created so the server can communicate continuously with multiple clients
                clients.add(object);//for later use if we wanna keep a track on clients connected
            }
           // runable clientThread = new runable(client,server);
           // pool.execute(clientThread);
        }
    }

}
