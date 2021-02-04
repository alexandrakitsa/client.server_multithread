import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;

public class runable implements Runnable {
  //  private MailServer server;
    private HashMap<String,Account> accounts;
    private MailClient newClient;

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;



    public runable(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
       // this.server=server;
    }
    @Override
    public void run()  {

        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeUTF("Mail Server:");
            out.writeUTF("Hello, you are connected as a guest!");

            DataInputStream in = new DataInputStream(client.getInputStream());
            while(true) {
                String request = in.readUTF();
                if (request.contains("1")) {
                    server.register(client);
                } else if (request.contains("2"))
                    server.logIn(client);
                else if (request.contains("a"))
                    server.newEmail(client);
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
                    out.writeUTF("exit");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
