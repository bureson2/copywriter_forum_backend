package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderDao extends BaseDao<Order> {

    protected OrderDao() {
        super(Order.class);
    }

    public List<Order> findAllByCategory(Category category){
        Objects.requireNonNull(category);
        return em.createNamedQuery("Order.findAllByCategory", Order.class).setParameter("category", category)
                .getResultList();
    }

    public List<Order> findAllByDeadline(Date deadline) {
        return em.createNamedQuery("Order.findAllByDeadline", Order.class).setParameter("deadline", deadline)
                .getResultList();
    }

    public List<Order> findAllByInsertionDate(Date insertionDate) {
        return em.createNamedQuery("Order.findAllByInsertionDate", Order.class).setParameter("insertionDate", insertionDate)
                .getResultList();
    }

    public Order findByLink(String link) {
        return em.createNamedQuery("Order.findByLink", Order.class).setParameter("link", link).getSingleResult();
    }

    public Order findByPrice(Double price) {
        return em.createNamedQuery("Order.findByPrice", Order.class).setParameter("price", price).getSingleResult();
    }
}
