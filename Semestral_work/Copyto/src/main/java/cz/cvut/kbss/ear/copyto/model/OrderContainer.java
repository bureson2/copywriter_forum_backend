package cz.cvut.kbss.ear.copyto.model;

import cz.cvut.kbss.ear.copyto.model.users.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "C_OrderContainer")
@NamedQueries({
        @NamedQuery(name = "OrderContainer.findByAssignee", query = "SELECT o from OrderContainer o WHERE :assignee = o.assignee"),
        @NamedQuery(name = "OrderContainer.findByClient", query = "SELECT o from OrderContainer o WHERE :client = o.client"),
        @NamedQuery(name = "OrderContainer.findByOrder", query = "SELECT o from OrderContainer o WHERE :order = o.order"),
        @NamedQuery(name = "OrderContainer.findByWorkplace", query = "SELECT o from OrderContainer o WHERE :workplace = o.workplace"),
        @NamedQuery(name = "OrderContainer.findAllCandidates", query = "SELECT o.candidates from OrderContainer o"),
        @NamedQuery(name = "OrderContainer.findAvailableOrders", query = "SELECT o.order FROM OrderContainer o WHERE o.assignee is null" ),
})
public class OrderContainer extends AbstractEntity{

    @OneToOne(cascade = CascadeType.MERGE)
    private User assignee = null;

    @OneToOne(cascade = CascadeType.MERGE)
    private User client;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Order order = new Order();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Workplace workplace = new Workplace();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true) // TODO ZKONTROLOVAT, ZE TOHLE DB NEPROMAZE
    @OrderBy("email")
    private List<User> candidates = new ArrayList<>();

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean open = true;

    public Order getOrder() {
        return order;
    }

    public User getAssignee() {
        return assignee;
    }

    public User getClient() {
        return client;
    }

    public Workplace getWorkplace() {
        return workplace;
    }

    public List<User> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<User> candidates) {
        this.candidates = candidates;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void addCandidate(User user){
        candidates.add(user);
    }

    public boolean isOpen(){
        return open;
    }

    public void setOpen(boolean val){
        open = val;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }
}


