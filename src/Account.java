import java.util.*;

public class Account {
    private String username, password;
    private ArrayList<Email> mailbox;
    public Account(String username, String password)
    {
        this.password=password;
        this.username=username;
        mailbox = new ArrayList<>();
    }
    public boolean isPasswordCorrect(String password) {
        return password.equals(this.password);
    }
    public String getUsername() {return username;}
    public ArrayList<Email> getMailbox(){return mailbox;}
    public void addToMailbox(Email email){mailbox.add(email);}
}
