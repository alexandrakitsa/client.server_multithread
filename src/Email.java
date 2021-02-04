public class Email {
    private boolean isNew;
    private String sender, receiver, subject, mainbody;
    //email constructors
    public Email(String sender, String receiver,String subject, String mainbody) {
        this.sender=sender;
        this.receiver=receiver;
        this.subject=subject;
        this.mainbody=mainbody;
        this.isNew=true;
    }
    //this one is for when were writing a new email, the other variables we're adding them with setters
    public Email(String sender) {
        this.sender=sender;
        this.isNew=true;
    }
    //returns main body of an email
    public String getMainbody() {
        return mainbody;
    }
    //returns the email's receiver
    public String getReceiver() {
        return receiver;
    }
    //returns the email's sender
    public String getSender() {
        return sender;
    }
    //returns the email's subject
    public String getSubject() {
        return subject;
    }
    //set the sender of an email
    public void setSender(String sender) {this.sender=sender;}
    //sets the receiver of an email
    public void setReceiver(String receiver) {this.receiver=receiver;}
    //sets the subject of an email
    public void setSubject(String subject) {this.subject=subject;}
    //sets the mainbody of an email
    public void setMainbody(String mainbody) {this.mainbody=mainbody;}
    //returns true if the email is new otherwise returns false
    public boolean isNew() {
        return isNew;
    }
    //sets an email as new or read
    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
