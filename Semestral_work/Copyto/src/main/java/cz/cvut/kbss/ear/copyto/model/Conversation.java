package cz.cvut.kbss.ear.copyto.model;

import cz.cvut.kbss.ear.copyto.model.users.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "C_Conversation")
@NamedQueries({
        @NamedQuery(name = "Conversation.findAllConversationsByUser", query = "SELECT m from Conversation m WHERE :user MEMBER OF m.members"),
        @NamedQuery(name = "Conversation.findAllMessages", query = "SELECT c.messages from Conversation c WHERE :c_id = c.id"),
})
public class Conversation extends AbstractEntity{

    public Conversation(List<User> members) {
        this.members = members;

        for (User member : members) {
            member.addConversation(this);
        }
    }

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "converstaionId")
    @OrderBy("date")
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @OrderBy("surname")
    private List<User> members = new ArrayList<>();

    public Conversation() {

    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<User> getUsers() {
        return members;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void addMember(User user){
        members.add(user);
    }

    public void addMessage(Message message){
        messages.add(message);
    }
}
