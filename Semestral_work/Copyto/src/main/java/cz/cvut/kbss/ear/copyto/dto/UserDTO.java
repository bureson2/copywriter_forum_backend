package cz.cvut.kbss.ear.copyto.dto;

import cz.cvut.kbss.ear.copyto.rest.util.PasswordMatches;
import cz.cvut.kbss.ear.copyto.rest.util.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@PasswordMatches
public class UserDTO {

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String surname;

    @NotNull
    @NotEmpty
    private Date date;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @ValidEmail
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String mobile;

    @NotNull
    @NotEmpty
    private String role;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
