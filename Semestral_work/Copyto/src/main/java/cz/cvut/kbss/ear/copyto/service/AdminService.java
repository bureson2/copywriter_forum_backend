package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.CategoryDao;
import cz.cvut.kbss.ear.copyto.dao.UserDao;
import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminService extends UserService {

    CategoryDao categoryDao;

    @Autowired
    public AdminService(UserDao userDao, PasswordEncoder passwordEncoder, CategoryDao categoryDao){
        super(userDao, passwordEncoder);
        this.categoryDao = categoryDao;
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryDao.find(id);
        Objects.requireNonNull(category);
        categoryDao.remove(category);
    }

    @Transactional
    public void createCategory(Category category){
        Objects.requireNonNull(category);
        categoryDao.persist(category);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User toRemove = dao.find(id);
        dao.remove(toRemove);
    }

    @Transactional
    public void blockUser(Integer id){
        User toBlock = dao.find(id);
        toBlock.setRole(Role.USER);
        dao.update(toBlock);
    }

    @Transactional
    public void unblockClient(Integer id){
        User toUnblock = dao.find(id);
        toUnblock.setRole(Role.CLIENT);
        dao.update(toUnblock);
    }

    @Transactional
    public void unblockCopywriter(Integer id){
        User toUnblock = dao.find(id);
        toUnblock.setRole(Role.COPYWRITER);
        dao.update(toUnblock);
    }


}
