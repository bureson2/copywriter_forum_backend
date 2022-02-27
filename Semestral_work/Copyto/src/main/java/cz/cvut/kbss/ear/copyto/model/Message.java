package cz.cvut.kbss.ear.copyto.model;

import cz.cvut.kbss.ear.copyto.model.users.User;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "C_Message")
@NamedQueries({
        @NamedQuery(name = "Message.findAllMessagesByAuthor", query = "SELECT m from Message m WHERE :user = m.author"),
        @NamedQuery(name = "Message.findAllMessagesByReceiver", query = "SELECT m from Message m WHERE :user = m.receiver"),
        @NamedQuery(name = "Message.findAllMessagesByText", query = "SELECT m from Message m WHERE :text = m.text"),
        @NamedQuery(name = "Message.findAllMessagesByDate", query = "SELECT m from Message m WHERE :date = m.date")
})

public class Message extends AbstractEntity {

    @OneToOne(cascade = CascadeType.MERGE)
    private User author;

    @OneToOne(cascade = CascadeType.MERGE)
    private User receiver;

    @Basic(optional = false)
    @Column(nullable = false)
    private String text = "";

    @Basic(optional = false)
    @Column(nullable = false)
    private Date date = new Date();

    public Message(User author, User receiver, String text) {
        this.author = author;
        this.receiver = receiver;
        this.text = text;
    }

    public Message() {
    }

    public User getAuthor() {
        return author;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
