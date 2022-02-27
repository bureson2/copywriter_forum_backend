package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class ConversationDao extends BaseDao<Conversation> {

    protected ConversationDao() {
        super(Conversation.class);
    }

    public List<Conversation> findAllConversationsByUser(User user){
        Objects.requireNonNull(user);
        return em.createNamedQuery("Conversation.findAllConversationsByUser", Conversation.class).setParameter("user", user)
                .getResultList();
    }

    public List<Message> findAllMessagesInContainer(Conversation conversation){
        return em.createNamedQuery("Conversation.findAllMessages", Message.class).setParameter("c_id", conversation.getId())
                .getResultList();
    }
}
