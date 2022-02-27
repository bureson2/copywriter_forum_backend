package cz.cvut.kbss.ear.copyto.dto;

import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDTO {

    private int id;

    @OrderBy("name")
    private List<String> categories = new ArrayList<>();

    @NotNull
    private double price = 0;

    @NotNull
    private Date insertionDate;

    @NotNull
    private Date deadline;

    @NotNull
    private String link;

    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(Date insertionDate) {
        this.insertionDate = insertionDate;
    }

    public Date getDeadline() {
        return deadline;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
