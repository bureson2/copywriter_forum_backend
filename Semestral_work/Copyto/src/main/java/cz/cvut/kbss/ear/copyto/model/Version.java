package cz.cvut.kbss.ear.copyto.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "C_Version")
@NamedQueries({
        @NamedQuery(name = "Version.findByDate", query = "SELECT v FROM Version v WHERE :date = v.date"),
        @NamedQuery(name = "Version.findByTitle", query = "SELECT v FROM Version v WHERE :title = v.title")
})
public class Version extends AbstractEntity{

    public Version() {
    }

    public Version(String title, String text) {
        this.title = title;
        this.text = text;
    }

    @Basic(optional = false)
    @Column(nullable = false)
    private Date date = new Date();

    @Basic(optional = false)
    @Column(nullable = false)
    private String title;

    @Basic(optional = false)
    @Column(nullable = false)
    private String text;

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
