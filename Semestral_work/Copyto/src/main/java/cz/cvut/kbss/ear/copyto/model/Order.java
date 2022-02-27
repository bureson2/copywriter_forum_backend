package cz.cvut.kbss.ear.copyto.model;

import cz.cvut.kbss.ear.copyto.enums.OrderState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "C_Order")
@NamedQueries({
        @NamedQuery(name = "Order.findAllByCategory", query = "SELECT o from Order o WHERE :category MEMBER OF o.categories"),
        @NamedQuery(name = "Order.findAllByDeadline", query = "SELECT o from Order o WHERE :deadline > o.deadline"),
        @NamedQuery(name = "Order.findAllByInsertionDate", query = "SELECT o from Order o WHERE :insertionDate = o.insertionDate"),
        @NamedQuery(name = "Order.findByLink", query = "SELECT o from Order o WHERE :link = o.link"),
        @NamedQuery(name = "Order.findByPrice", query = "SELECT o from Order o WHERE :price = o.price")
})
public class Order extends AbstractEntity{

    @ManyToMany
    @OrderBy("name")
    private List<Category> categories = new ArrayList<>();

    @Basic(optional = false)
    @Column(nullable = true)
    private double price;

    @Basic(optional = false)
    @Column(nullable = false)
    private Date insertionDate = new Date();

    @Basic(optional = false)
    @Column(nullable = true)
    private Date deadline;

    @Basic(optional = false)
    @Column(nullable = true)
    private String link;

    public Order(){}
    public Order(double price, Date deadlineDate) {
        this.price = price;
        this.deadline = deadlineDate;
        this.insertionDate = new Date();
    }

    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.ADDED;

    public void setCategory(Category category){
        categories.add(category);
    }

    public void removeCategory(Category category){
        categories.remove(category);
    }

    public double getPrice() {
        return price;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public OrderState getState() {
        return state;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setInsertionDate(Date insertionDate) {
        this.insertionDate = insertionDate;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
