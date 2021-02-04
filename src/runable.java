import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;

public class runable implements Runnable {
    private MailServer server;
    private HashMap<String,Account> accounts;
    private MailClient newClient;

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;


    //when creating a runnable object we also give a client socket for the client communication and a server object so we can use its functions
    public runable(Socket client, MailServer server) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
        this.server=server;
    }
    @Override
    public void run()  {

        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("Mail Server:");
            out.writeUTF("Hello, you are connected as a guest!");

            DataInputStream in = new DataInputStream(client.getInputStream());
            while(true) {
                String request = in.readUTF(); //reads client's  input
                if (request.contains("1")) { //numbers have to do with the main menu
                    server.register(client);//calls the proper function depending the number the user chose
                } else if (request.contains("2"))
                    server.logIn(client);
                else if (request.contains("a"))//characters have to do with client's menu
                    server.newEmail(client);//calls the proper function depending the character the user chose
                else if (request.contains("b"))
                    server.showEmails(client);
                else if (request.contains("c"))
                    server.deleteEmail(client);
                else if (request.contains("d"))
                    server.readEmails(client);
                else if (request.contains("e"))
                    server.logOut(client);
                else
                {
                    out.writeUTF("exit"); //if he didn't choose any of the above we exit the programme by sending to the user the word exit
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {//free resources
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
