package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class MessageDao extends BaseDao<Message> {

    protected MessageDao() {
        super(Message.class);
    }

    public List<Message> findAllMessagesByAuthor(User user){
        Objects.requireNonNull(user);
        return em.createNamedQuery("Message.findAllMessagesByAuthor", Message.class).setParameter("user", user)
                .getResultList();
    }

    public List<Message> findAllMessagesByReceiver(User user){
        Objects.requireNonNull(user);
        return em.createNamedQuery("Message.findAllMessagesByReceiver", Message.class).setParameter("user", user)
                .getResultList();
    }

    public List<Message> findAllMessagesByText(String text){
        return em.createNamedQuery("Message.findAllMessagesByText", Message.class).setParameter("text", text)
                .getResultList();
    }

    public List<Message> findAllMessagesByDate(Date date){
        return em.createNamedQuery("Message.findAllMessagesByDate", Message.class).setParameter("date", date).getResultList();
    }

}
