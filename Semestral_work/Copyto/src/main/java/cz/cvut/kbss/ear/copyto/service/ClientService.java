package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.OrderContainerDao;
import cz.cvut.kbss.ear.copyto.dao.OrderDao;
import cz.cvut.kbss.ear.copyto.dao.UserDao;
import cz.cvut.kbss.ear.copyto.enums.OrderState;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService extends UserService{

    private OrderDao orderDao;
    private OrderContainerDao containerDao;

    @Autowired
    public ClientService(UserDao userDao, PasswordEncoder passwordEncoder, OrderContainerDao containerDao, OrderDao orderDao){
        super(userDao, passwordEncoder);
        this.orderDao = orderDao;
        this.containerDao = containerDao;
    }

    @Transactional
    public void authorizeAssignee(OrderContainer container, User assignee){
        container.setAssignee(assignee);
        container.getOrder().setState(OrderState.IN_PROCESS);
        containerDao.update(container);
    }

    @Transactional
    public void changeAssignee(OrderContainer order, User assignee){
        order.setAssignee(assignee);
        containerDao.update(order);
    }

    @Transactional
    public void changeVisibility(OrderContainer container){
        container.setOpen(!container.isOpen());
        containerDao.update(container);
    }
}
