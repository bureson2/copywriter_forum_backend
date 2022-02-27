package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.Copyto;
import cz.cvut.kbss.ear.copyto.environment.TestConfiguration;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.service.SystemInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.persistence.PersistenceException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
// Exclude SystemInitializer from the startup, we don't want the admin account here
@ComponentScan(basePackageClasses = Copyto.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class BaseDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CategoryDao sut;

    @Test
    public void persistSavesSpecifiedInstance() {
        final Category category = Generator.generateCategory("TestCategory");
        sut.persist(category);
        assertNotNull(category.getId());

        final Category result = em.find(Category.class, category.getId());
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final Category category = Generator.generateCategory("TestCategory");
        em.persistAndFlush(category);
        assertNotNull(category.getId());

        final Category result = sut.find(category.getId());
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final Category catOne = Generator.generateCategory("TestCategory");
        em.persistAndFlush(catOne);
        final Category catTwo = Generator.generateCategory("TestCategoryTwo");
        em.persistAndFlush(catTwo);

        final List<Category> result = sut.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(catOne.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(catTwo.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final Category cat = Generator.generateCategory("TestCategory");
        em.persistAndFlush(cat);

        final Category update = new Category();
        update.setId(cat.getId());
        final String newName = "New category name";
        update.setName(newName);
        sut.update(update);

        final Category result = sut.find(cat.getId());
        assertNotNull(result);
        assertEquals(cat.getName(), result.getName());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final Category cat = Generator.generateCategory("TestCategory");
        em.persistAndFlush(cat);
        assertNotNull(em.find(Category.class, cat.getId()));
        em.detach(cat);

        sut.remove(cat);
        assertNull(em.find(Category.class, cat.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final Category cat = Generator.generateCategory("TestCategory");
        cat.setId(123);
        assertNull(em.find(Category.class, cat.getId()));

        sut.remove(cat);
        assertNull(em.find(Category.class, cat.getId()));
    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        final Category cat = Generator.generateCategory("TestCategory");
        em.persistAndFlush(cat);
        em.remove(cat);
        assertThrows(PersistenceException.class, () -> sut.update(cat));
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final Category cat = Generator.generateCategory("TestCategory");
        em.persistAndFlush(cat);
        assertTrue(sut.exists(cat.getId()));
        assertFalse(sut.exists(-1));
    }
}
