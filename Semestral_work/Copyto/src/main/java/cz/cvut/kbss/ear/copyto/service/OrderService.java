package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.OrderContainerDao;
import cz.cvut.kbss.ear.copyto.dao.OrderDao;
import cz.cvut.kbss.ear.copyto.enums.OrderState;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.Workplace;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private final OrderContainerDao containerDao;
    private final OrderDao orderDao;

    @Autowired
    public OrderService(OrderContainerDao containerDao, OrderDao orderDao){
        this.containerDao = containerDao;
        this.orderDao = orderDao;
    }

    @Transactional(readOnly = true)
    public Order findOrder(Integer id) {
        return orderDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders() {
        return orderDao.findAll();
    }

    @Transactional(readOnly = true)
    public Order findOrder(int id) {
        return orderDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findContainersByClient() {
        return containerDao.findAvailableOrders();
    }

    @Transactional(readOnly = true)
    public List<Order> findOrders(Category category) {
        return orderDao.findAllByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Order> findOldestOrders() {
        List<Order> toReturn = new ArrayList<>();
        for(Order o : orderDao.findAll()){
            long diffInMillies = Math.abs(new Date().getTime() - o.getInsertionDate().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(diff > 60 && o.getState() == OrderState.ADDED){
                toReturn.add(o);
            }
        }
        return toReturn;
    }

    @Transactional(readOnly = true)
    public List<Order> findNewestOrders() {
        List<Order> toReturn = new ArrayList<>();
        for(Order o : orderDao.findAll()){
            long diffInMillies = Math.abs(new Date().getTime() - o.getInsertionDate().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(diff < 7 && o.getState() == OrderState.ADDED){
                toReturn.add(o);
            }
        }
        return toReturn;
    }

    @Transactional(readOnly = true)
    public List<Order> findOrdersWithComingDeadline() {
        List<Order> toReturn = new ArrayList<>();
        for(Order o : orderDao.findAll()){
            long diffInMillies = Math.abs(o.getDeadline().getTime() - new Date().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(diff < 7 && o.getState() == OrderState.ADDED){
                toReturn.add(o);
            }
        }
        return toReturn;
    }

    @Transactional(readOnly = true)
    public List<Order> findLongTermOrders() {
        List<Order> toReturn = new ArrayList<>();
        for(Order o : orderDao.findAll()){
            long diffInMillies = Math.abs(o.getDeadline().getTime() - new Date().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if(diff > 60 && o.getState() == OrderState.ADDED){
                toReturn.add(o);
            }
        }
        return toReturn;
    }

    @Transactional(readOnly = true)
    public List<OrderContainer> findContainers() {
        return containerDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrderContainer> findContainersByAssignee(User user) {
        return containerDao.findByAssignee(user);
    }

    @Transactional(readOnly = true)
    public List<OrderContainer> findContainersByClient(User user) {
        return containerDao.findByClient(user);
    }

    @Transactional(readOnly = true)
    public List<Order> findAvailableOrders(){
        List<OrderContainer> containers = findContainers();
        ArrayList<Order> availableOrders = new ArrayList<>();
        for(OrderContainer container : containers){
            if(container.getAssignee() == null){
                availableOrders.add(container.getOrder());
            }
        }
        return availableOrders;
    }

    @Transactional(readOnly = true)
    public List<User> findCandidates(OrderContainer container){
        return container.getCandidates();
    }

    @Transactional(readOnly = true)
    public OrderContainer findContainer(int id) {
        return containerDao.find(id);
    }

    @Transactional(readOnly = true)
    public OrderContainer findContainer(Order order) {
        return containerDao.findByOrder(order);
    }

    @Transactional(readOnly = true)
    public OrderContainer findContainer(Workplace workplace) {
        return containerDao.findByWorkplace(workplace);
    }

    @Transactional(readOnly = true)
    public List<User> findCandidates() {
        return containerDao.findAllCandidates();
    }

    @Transactional
    public void createContainer(OrderContainer container) {
        containerDao.persist(container);
    }

    @Transactional
    public void addOrder(Order order) {
        orderDao.persist(order);
    }

    @Transactional
    public void update(OrderContainer container) {
        containerDao.update(container);
    }

    @Transactional
    public void update(Order order) {
        orderDao.update(order);
    }

    @Transactional
    public void remove(OrderContainer container) {
        Objects.requireNonNull(container);
        containerDao.remove(container);
    }

    @Transactional
    public void remove(Order order) {
        Objects.requireNonNull(order);
        orderDao.remove(order);
    }
}
