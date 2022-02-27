package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.Copyto;
import cz.cvut.kbss.ear.copyto.environment.TestConfiguration;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.service.SystemInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

// DataJpaTest does not load all the application beans, it starts only persistence-related stuff
@DataJpaTest
// Exclude SystemInitializer from the startup, we don't want the admin account here
@ComponentScan(basePackageClasses = Copyto.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class CategoryDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderDao orderDao;

    private static final Random RAND = new Random();

    @Test
    public void findAllByCategoryReturnsOrdersInSpecifiedCategory() {

        final Category cat = Generator.generateCategory("testCategory");
        em.persist(cat);

        final List<Order> orders = Generator.generateOrders(cat, em);
        final List<Order> result = orderDao.findAllByCategory(cat);

        orders.sort(Comparator.comparing(Order::getDeadline));
        result.sort(Comparator.comparing(Order::getDeadline));

        assertEquals(orders.size(), result.size());
        for (int i = 0; i < orders.size(); i++) {
            assertEquals(orders.get(i).getId(), result.get(i).getId());
        }
    }
}
