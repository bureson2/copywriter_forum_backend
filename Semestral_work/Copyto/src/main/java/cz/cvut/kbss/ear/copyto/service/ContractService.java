package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.ContractDao;
import cz.cvut.kbss.ear.copyto.dao.UserDao;
import cz.cvut.kbss.ear.copyto.model.Contract;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ContractService {

    private final UserDao userDao;
    private final ContractDao contractDao;

    @Autowired
    public ContractService(UserDao userDao, ContractDao contractDao){
        this.userDao = userDao;
        this.contractDao = contractDao;
    }

    @Transactional(readOnly = true)
    public Contract findContract(int id) {
        return contractDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Contract> findContract(Order order) {
        return contractDao.findByOrder(order);
    }

    @Transactional(readOnly = true)
    public List<Contract> findContracts() {
        return contractDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Contract> findContractsByClient(User user) {
        return contractDao.findAllByClient(user);
    }

    @Transactional(readOnly = true)
    public List<Contract> findContractsByCopywriter(User user) {
        return contractDao.findAllByCopywriter(user);
    }

    @Transactional
    public void createContract(Contract contract){
        contractDao.persist(contract);
    }

    @Transactional
    public void negotiateAgreement(User client, User copywriter, Order order, Date date, double price){
        Contract contract = new Contract(client, copywriter, order, date, price);
        contractDao.persist(contract);
    }

    @Transactional
    public void negotiateAgreement(User client, User copywriter, Order order, Date date, double price, double penalty){
        Contract contract = new Contract(client, copywriter, order, date, price, penalty);
        contractDao.persist(contract);
    }

    // TODO Confirm agreement



}
