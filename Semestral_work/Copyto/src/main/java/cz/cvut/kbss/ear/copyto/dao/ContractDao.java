package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Contract;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class ContractDao extends BaseDao<Contract> {

    protected ContractDao() {
        super(Contract.class);
    }

    public List<Contract> findAllByClient(User client){
        Objects.requireNonNull(client);
        return em.createNamedQuery("Contract.findAllByClient", Contract.class).setParameter("client", client)
                .getResultList();
    }

    public List<Contract> findAllByCopywriter(User copywriter){
        Objects.requireNonNull(copywriter);
        return em.createNamedQuery("Contract.findAllByCopywriter", Contract.class).setParameter("copywriter", copywriter)
                .getResultList();
    }

    public List<Contract> findByOrder(Order order){
        Objects.requireNonNull(order);
        return em.createNamedQuery("Contract.findByOrder", Contract.class).setParameter("order", order)
                .getResultList();
    }

    public List<Contract> findAllByDateOfAgreement(Date dateOfAgreement){
        return em.createNamedQuery("Contract.findAllByDateOfAgreement", Contract.class).setParameter("dateOfAgreement", dateOfAgreement)
                .getResultList();
    }

    public List<Contract> findAllByDate(Date date){
        return em.createNamedQuery("Contract.findAllByDate", Contract.class).setParameter("date", date)
                .getResultList();
    }
}
