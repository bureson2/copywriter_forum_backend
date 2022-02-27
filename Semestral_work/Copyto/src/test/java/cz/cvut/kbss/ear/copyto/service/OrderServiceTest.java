package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OrderService sut;

    private static final Random RAND = new Random();

    @Test
    public void createOrderDetailCreatesNewOrderDetail() {
        final Order order = Generator.generateOrder();
        sut.addOrder(order);

        final Order result = sut.findOrder(order.getId());
        assertEquals(order, result);
    }

    @Test void updateOrderDetailUpdatesOrdersParams(){

        final Category newCategory = Generator.generateCategory("newCategory");
        em.persist(newCategory);
        final User newClient = Generator.generateClient();
        em.persist(newClient);
        final User newAssignee = Generator.generateCopywriter();
        em.persist(newAssignee);
        final Date newDeadline = new Date();
        final Date newInsertionDate = new Date();
        final int newPrice = 100;
        final Order order = Generator.generateOrder();

        order.setCategory(newCategory);
        order.setDeadline(newDeadline);
        order.setInsertionDate(newInsertionDate);
        order.setPrice(newPrice);
        order.setLink("newLink.cz");

        sut.addOrder(order);

        final Order result = sut.findOrders(newCategory).get(0);
        assertEquals(result.getCategories().get(0), newCategory);
        assertEquals(result.getDeadline(), newDeadline);
        assertEquals(result.getInsertionDate(), newInsertionDate);
        assertEquals(order.getId(), result.getId());
        assertEquals(result.getPrice(), newPrice);
    }

    @Test
    public void updateOrderUpdatesOrder(){
        final Order order = Generator.generateOrder();
        sut.addOrder(order);

        Date insertionDate = new Date();
        Date deadlineDate = new Date();
        String link = RandomStringUtils.randomAlphabetic(10);
        User copywriter = Generator.generateCopywriter();
        em.persist(copywriter);
        int price = RAND.nextInt();

        order.setInsertionDate(insertionDate);
        order.setDeadline(deadlineDate);
        order.setLink(link);
        order.setPrice(price);

        sut.update(order);
        final Order result = sut.findOrder(order.getId());

        assertEquals(result.getInsertionDate(), insertionDate);
        assertEquals(result.getDeadline(), deadlineDate);
        assertEquals(result.getPrice(), price);
        assertEquals(result.getLink(), link);
    }
}
