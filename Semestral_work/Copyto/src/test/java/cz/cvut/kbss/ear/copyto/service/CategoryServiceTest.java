package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryService sut;

    @Test
    public void addOrderToCategoryAddsOrderToTargetCategory() {
        final Category category = Generator.generateCategory(RandomStringUtils.randomAlphabetic(10));
        final Order order = Generator.generateOrder();
        em.persist(order);
        sut.createCategory(category);
        sut.addOrder(category, order);

        final Order result = em.find(Order.class, order.getId());
        assertTrue(result.getCategories().stream().anyMatch(
                o -> o.getId().equals(category.getId())));
    }

    @Test
    public void removeOrderRemovesOrderFromCategory(){
        final Category category = Generator.generateCategory(RandomStringUtils.randomAlphabetic(10));
        final Order order = Generator.generateOrder();
        em.persist(order);
        sut.createCategory(category);
        sut.addOrder(category, order);
        sut.removeOrder(category, order);

        final Order result = em.find(Order.class, order.getId());
        assertFalse(result.getCategories().stream().anyMatch(
                o -> o.getId().equals(category.getId())));
    }

    @Test
    public void createCategoryCreatesNewCategory(){
        final Category category = Generator.generateCategory(RandomStringUtils.randomAlphabetic(10));
        sut.createCategory(category);

        final Category result = sut.findCategory(category.getId());
        assertEquals(category, result);
    }

    @Test
    public void deleteCategoryDeletesCategory() {
        final Category category = Generator.generateCategory(RandomStringUtils.randomAlphabetic(10));
        sut.createCategory(category);
        sut.deleteCategory(category);

        final Category result = sut.findCategory(category.getId());
        assertNull(result);
    }

    @Test
    public void updateCategoryUpdatesCategoryParams() {
        final Category category = Generator.generateCategory(RandomStringUtils.randomAlphabetic(10));
        sut.createCategory(category);
        sut.updateCategory(category, "update");

        final Category result = sut.findCategory(category.getId());
        assertEquals("update", result.getName() );
    }
}
