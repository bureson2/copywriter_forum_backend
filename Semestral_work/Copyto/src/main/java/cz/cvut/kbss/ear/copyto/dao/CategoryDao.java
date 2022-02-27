package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao extends BaseDao<Category>{

    protected CategoryDao() {
        super(Category.class);
    }

    public Category findByName(String name) {
        return em.createNamedQuery("Category.findByName", Category.class).setParameter("name", name)
                .getSingleResult();
    }
}
