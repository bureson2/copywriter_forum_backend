package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.MessageDao;
import cz.cvut.kbss.ear.copyto.dao.OrderContainerDao;
import cz.cvut.kbss.ear.copyto.dao.OrderDao;
import cz.cvut.kbss.ear.copyto.dao.UserDao;
import cz.cvut.kbss.ear.copyto.enums.OrderState;
import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CopywriterService extends UserService{

    private OrderDao orderDao;
    private OrderContainerDao containerDao;
    private MessageDao messageDao;

    @Autowired
    public CopywriterService(UserDao userDao, OrderDao orderDao, OrderContainerDao containerDao, MessageDao messageDao, PasswordEncoder passwordEncoder){
        super(userDao, passwordEncoder);
        this.orderDao = orderDao;
        this.containerDao = containerDao;
        this.messageDao = messageDao;
    }

    @Transactional
    public void sendFinishMessage(OrderContainer order){
        String text = order.getOrder().getId() + " was finished. Please check if everything is OK.";
        Message message = new Message(order.getAssignee(), order.getClient(), text);
        messageDao.persist(message);
    };

    @Transactional
    public void finishJob(OrderContainer container){
        container.getOrder().setState(OrderState.DONE);
        sendFinishMessage(container);
        orderDao.update(container.getOrder());
    }

    @Transactional
    public void signUpForOrder(User candidate, Order order){
        if(candidate.getRole() == Role.COPYWRITER){
            OrderContainer container = containerDao.findByOrder(order);
            container.addCandidate(candidate);
            containerDao.update(container);
        }
    }


}
