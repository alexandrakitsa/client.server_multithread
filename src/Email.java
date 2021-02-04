public class Email {
    private boolean isNew;
    private String sender, receiver, subject, mainbody;
    public Email(String sender, String receiver,String subject, String mainbody) {
        this.sender=sender;
        this.receiver=receiver;
        this.subject=subject;
        this.mainbody=mainbody;
        this.isNew=true;
    }
    public Email(String sender) {
        this.sender=sender;
        this.isNew=true;
    }
    public String getMainbody() {
        return mainbody;
    }
    public String getReceiver() {
        return receiver;
    }
    public String getSender() {
        return sender;
    }
    public String getSubject() {
        return subject;
    }
    public void setSender(String sender) {this.sender=sender;}
    public void setReceiver(String receiver) {this.receiver=receiver;}
    public void setSubject(String subject) {this.subject=subject;}
    public void setMainbody(String mainbody) {this.mainbody=mainbody;}
    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
