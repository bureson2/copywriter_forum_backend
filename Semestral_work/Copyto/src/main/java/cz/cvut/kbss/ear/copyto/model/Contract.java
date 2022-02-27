package cz.cvut.kbss.ear.copyto.model;

import cz.cvut.kbss.ear.copyto.model.users.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "C_Contract")
@NamedQueries({
        @NamedQuery(name = "Contract.findAllByClient", query = "SELECT c FROM Contract c WHERE :client = c.client"),
        @NamedQuery(name = "Contract.findAllByCopywriter", query = "SELECT c FROM Contract c WHERE :copywriter = c.copywriter"),
        @NamedQuery(name = "Contract.findByOrder", query = "SELECT c FROM Contract c WHERE :order = c.order"),
        @NamedQuery(name = "Contract.findAllByDateOfAgreement", query = "SELECT c FROM Contract c WHERE :dateOfAgreement = c.dateOfAgreement"),
        @NamedQuery(name = "Contract.findAllByDate", query = "SELECT c FROM Contract c WHERE :date = c.date")
})
public class Contract extends AbstractEntity {

    @OneToOne(cascade = CascadeType.MERGE)
    private User client;

    @OneToOne(cascade = CascadeType.MERGE)
    private User copywriter;

    @OneToOne(cascade = CascadeType.MERGE)
    private Order order;

    @Basic(optional = false)
    @Column(nullable = false)
    private Date dateOfAgreement = new Date();

    // aby pri zmene deadlinu u objednavky zustalo zachovano puvodni domluvene datum
    @Basic(optional = false)
    @Column(nullable = true)
    private Date date;

    // aby pri zmene castky u objednavky zustalo zachovano puvodni domluvena castka
    @Basic(optional = false)
    @Column(nullable = false)
    private double price;

    @Basic(optional = false)
    @Column(nullable = true)
    private double penalty = 0;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean approvedByClient = false;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean approvedByCopywriter = false;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean finishedWork = false;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean paid = false;

    public Contract(User client, User copywriter, Order order, Date date, double price) {
        this.client = client;
        this.copywriter = copywriter;
        this.order = order;
        this.date = date;
        this.price = price;
    }

    public Contract(User client, User copywriter, Order order, Date date, double price, double penalty) {
        this.client = client;
        this.copywriter = copywriter;
        this.order = order;
        this.date = date;
        this.price = price;
        this.penalty = penalty;
    }

    public Contract() {
    }


    public User getClient() {
        return client;
    }

    public User getCopywriter() {
        return copywriter;
    }

    public Order getOrder() {
        return order;
    }

    public Date getDateOfAgreement() {
        return dateOfAgreement;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public void setCopywriter(User copywriter) {
        this.copywriter = copywriter;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setDateOfAgreement(Date dateOfAgreement) {
        this.dateOfAgreement = dateOfAgreement;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }
}
