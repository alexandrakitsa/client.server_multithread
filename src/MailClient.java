import java.io.*;
import java.net.Socket;


public class MailClient {
    private static final String SERVER_IP = "127.0.0.1"; //local ip
    private static final int SERVER_PORT = 9090; //port
    public MailClient() {
        //constructor
    }
    //prints the client's menu after he logged in
    private void clientMenu() {
        System.out.println("=============");
        System.out.println("My Account \nMenu\na.New Email\nb.Show Emails\nc.Delete Email\nd.Read Email\ne.Log Out");
        System.out.println("=============");
    }
    //prints the main menu
    private void mainmenu()
    {
        System.out.println("=============");
        System.out.println("Welcome to MailServer \nMenu\n1.Register\n2.LogIn\n3.Exit");
        System.out.println("=============");
    }
    public static void main(String[] args) throws IOException {
        MailClient client = new MailClient(); //creating a client object so we can use its functions
        Socket socket = new Socket(SERVER_IP, SERVER_PORT); //creating a new socket given the fixed ip and port we created in the client object
        DataInputStream input = new DataInputStream(socket.getInputStream());//variable to read the input from server
        System.out.println("------------");
        String serverResponse = input.readUTF();//reading input from server and storing it in a string variable so we can use it later on
        System.out.println(serverResponse); //prints the first server's response
        System.out.println("------------");
        serverResponse = input.readUTF(); //reads the second server's response
        System.out.println(serverResponse);//prints the second server's response
        client.mainmenu();//prints the main menu to get started

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); //variable to store input from client's keyboard
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());//variable to store the information we want to send to the server
        System.out.println(">");
        String command = keyboard.readLine();//reading from keyboard the client's answer
        out.writeUTF(command);//sending that answer to the server

        try {
            /***loop for continues interaction between client and server.
             * as we have already printed the main menu to the clients screen the server need to take action depending on user's answer
            */
            while(true) {
                serverResponse = input.readUTF(); //reads server's answer, usually this variable will contain something that user need to do, like give a username etc.
                System.out.println(serverResponse);//prints that answer
                if(serverResponse.contains("exit")) //if server's answer had the word exit on it we exit the loop and end the programme
                    break;
                //if the answer had the words logged out (that happens in case the user asked to be logged out
                //we print to the clients menu the main menu so he can decide if he want to log in/ register or exit the programme
                else if(serverResponse.contains("logged out")) {
                    client.mainmenu();
                    System.out.println(">");
                    command = keyboard.readLine();//reading from keyboard the client's answer
                    out.writeUTF(command);//sending that answer to the server
                }
                //in case the server's answer contains "mail server" or "welcome" (a way to recognise that a user is logged in)
                //we print the client's menu, read his choice and send it back to the server so it can take further actions.
                else if(serverResponse.contains("mail server")|| serverResponse.contains("welcome")) {
                    client.clientMenu();
                    System.out.println("--------------");
                    System.out.println(">");
                    command = keyboard.readLine();
                    out.writeUTF(command);
                //in case the server's answer contains   (a way to recognise that the server is asking for an information such as a username etc)
                // we read his choice and send it back to the server so it can take further actions.
                }else if(serverResponse.contains(":") ){
                    System.out.println(">");
                    command = keyboard.readLine();
                    out.writeUTF(command);
                }
            }
        }finally {
            //we free the resources and close the programme
            socket.close();
            out.close();
            input.close();
            keyboard.close();
            System.exit(0);}
    }
}
