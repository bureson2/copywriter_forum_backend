package cz.cvut.kbss.ear.copyto.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "C_Category")
@NamedQueries({
        @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE :name = c.name")
})
public class Category extends AbstractEntity {

    @ManyToMany(mappedBy = "categories")
    @OrderBy("insertionDate")
    private List<Order> orders = new ArrayList<>();

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void removeOrder(Order order){
        orders.remove(order);
    }

    public void addOrder(Order order){
        orders.add(order);
    }
}
