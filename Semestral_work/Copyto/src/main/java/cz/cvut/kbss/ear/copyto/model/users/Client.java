package cz.cvut.kbss.ear.copyto.model.users;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENT")
@NamedQueries({
        @NamedQuery(name = "Client.findInterestedCopywriters", query = "SELECT o.candidates FROM OrderContainer o WHERE :client = o.client and :order = o" )
})
public class Client extends User {

    @ElementCollection
    private List<String> webs = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "id")
    @OrderBy("id")
    private List<OrderContainer> orders = new ArrayList<>();

    @Basic(optional = false)
    @Column(nullable = true)
    private String residence;

    public Client() {
        this.role = Role.CLIENT;
    }

    public void addOrder(OrderContainer orderContainer) {
        orders.add(orderContainer);
    }

    public List<String> getWebs() {
        return webs;
    }

    public List<OrderContainer> getOrders() {
        return orders;
    }

    public String getResidence() {
        return residence;
    }

    public void setWebs(List<String> webs) {
        this.webs = webs;
    }

    public void setOrders(List<OrderContainer> orders) {
        this.orders = orders;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }
}
