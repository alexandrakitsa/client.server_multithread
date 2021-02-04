import java.io.*;
import java.net.Socket;


public class MailClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    public MailClient() {    }

    private void clientMenu() {
        System.out.println("=============");
        System.out.println("My Account \nMenu\na.New Email\nb.Show Emails\nc.Delete Email\nd.Read Email\ne.Log Out");
        System.out.println("=============");
    }
    private void mainmenu()
    {
        System.out.println("=============");
        System.out.println("Welcome to MailServer \nMenu\n1.Register\n2.LogIn\n3.Exit");
        System.out.println("=============");
    }
    public static void main(String[] args) throws IOException {
        MailClient client = new MailClient();
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        DataInputStream input = new DataInputStream(socket.getInputStream());
       // BufferedReader input = new BufferedReader(new InputStreamReader((socket.getInputStream())));
        System.out.println("------------");
        String serverResponse = input.readUTF();
        System.out.println(serverResponse);
        System.out.println("------------");
        serverResponse = input.readUTF();
        System.out.println(serverResponse);
        client.mainmenu();

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println(">");
        String command = keyboard.readLine();
        out.writeUTF(command);

        try {
            while(true) {
                serverResponse = input.readUTF();
                System.out.println(serverResponse);
                if(serverResponse.contains("exit"))
                    break;
                else if(serverResponse.contains("logged out")) {
                    client.mainmenu();
                    System.out.println(">");
                    command = keyboard.readLine();
                    out.writeUTF(command);
                }
                else if(serverResponse.contains("mail server")|| serverResponse.contains("welcome")) {
                    client.clientMenu();
                    System.out.println("--------------");
                    System.out.println(">");
                    command = keyboard.readLine();
                    out.writeUTF(command);
                }else if(serverResponse.contains(":") ){
                    System.out.println(">");
                    command = keyboard.readLine();
                    out.writeUTF(command);
                }
            }
        }finally {
            socket.close();
            out.close();
            input.close();
            keyboard.close();
            System.exit(0);}
    }
}
