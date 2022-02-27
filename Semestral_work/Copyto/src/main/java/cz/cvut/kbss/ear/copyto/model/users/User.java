package cz.cvut.kbss.ear.copyto.model.users;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.AbstractEntity;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "C_User")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "USER_TYPE", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
        @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE :role = u.role"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE :email = u.email"),
        @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE :firstName = u.firstName and :surname = u.surname")
})
public class User extends AbstractEntity {

    @ManyToMany
    private List<Conversation> conversations = new ArrayList<>();

    @Basic(optional = false)
    @Column(nullable = false)
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false)
    private String surname;

    @Basic(optional = false)
    @Column(nullable = false)
    private String password;

    @Basic(optional = false)
    @Column(nullable = false)
    private String mobile;

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    private String email;

    @Basic(optional = false)
    @Column(nullable = false)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    protected Role role = Role.USER;

    public User(String firstName, String surname, String password, String mobile, String email, Date dateOfBirth) {
        this.firstName = firstName;
        this.surname = surname;
        this.password = password;
        this.mobile = mobile;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public User() {
    }

    public Role getRole() {
        return role;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void addConversation(Conversation conversation) {
        conversations.add(conversation);
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getMobile() {
        return mobile;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void erasePassword(){
        this.password = "";
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }


}
