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
    //checks if a password given by the user is matching to the one of the account he is trying to get logged on
    public boolean isPasswordCorrect(String password) {
        return password.equals(this.password);
    }
    //returns username
    public String getUsername() {return username;}
    //returns the mailbox of a user
    public ArrayList<Email> getMailbox(){return mailbox;}
    //adds an email to the user's mailbox
    public void addToMailbox(Email email){mailbox.add(email);}
    //removes mail from user's mailbox
    public void removeFromMailbox(Email email){mailbox.remove(email);}
}
