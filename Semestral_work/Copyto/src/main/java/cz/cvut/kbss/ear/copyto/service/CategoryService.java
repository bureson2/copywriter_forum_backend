package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.CategoryDao;
import cz.cvut.kbss.ear.copyto.dao.OrderDao;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;
    private final OrderDao orderDao;

    @Autowired
    public CategoryService(CategoryDao categoryDao, OrderDao orderDao){
        this.categoryDao = categoryDao;
        this.orderDao = orderDao;
    }

    @Transactional(readOnly = true)
    public List<Category> findCategories(){
        return categoryDao.findAll();
    }

    @Transactional(readOnly = true)
    public Category findCategory(Integer id){
        return categoryDao.find(id);
    }

    @Transactional(readOnly = true)
    public Category findCategory(String name){
        return categoryDao.findByName(name);
    }

    @Transactional
    public void createCategory(Category category){
        Objects.requireNonNull(category);
        categoryDao.persist(category);
    }

    @Transactional
    public void addOrder(Category category, Order order){
        Objects.requireNonNull(category);
        Objects.requireNonNull(order);
        category.addOrder(order);
        order.setCategory(category);
        orderDao.update(order);
    }

    @Transactional
    public void removeOrder(Category category, Order order){
        Objects.requireNonNull(category);
        Objects.requireNonNull(order);
        order.removeCategory(category);
        category.removeOrder(order);
        orderDao.update(order);
        categoryDao.update(category);
    }

    @Transactional
    public void updateCategory(Category category, String name) {
        Objects.requireNonNull(category);
        Objects.requireNonNull(name);
        category.setName(name);
        categoryDao.update(category);
    }

    @Transactional
    public void deleteCategory(Category category) {
        Objects.requireNonNull(category);
        categoryDao.remove(category);
    }
}
