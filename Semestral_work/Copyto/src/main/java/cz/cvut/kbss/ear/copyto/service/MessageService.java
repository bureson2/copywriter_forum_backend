package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.ConversationDao;
import cz.cvut.kbss.ear.copyto.dao.MessageDao;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final ConversationDao conversationDao;
    private final MessageDao messageDao;

    @Autowired
    public MessageService(ConversationDao conversationDao, MessageDao messageDao) {
        this.conversationDao = conversationDao;
        this.messageDao = messageDao;
    }

    @Transactional(readOnly = true)
    public List<Conversation> findConversations() {
        return conversationDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Conversation> findConversations(User user) {
        return conversationDao.findAllConversationsByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Message> findAllMessagesInConversation(Conversation conversation) {
        return conversationDao.findAllMessagesInContainer(conversation);
    }

    @Transactional(readOnly = true)
    public Conversation findConversation(int id) {
        return conversationDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Message> findMessages() {
        return messageDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Message> findMessages(Date date) {
        return messageDao.findAllMessagesByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Message> findMessagesByAuthor(User user) {
        return messageDao.findAllMessagesByAuthor(user);
    }

    @Transactional(readOnly = true)
    public List<Message> findMessagesByReceiver(User user) {
        return messageDao.findAllMessagesByReceiver(user);
    }

    @Transactional(readOnly = true)
    public Message findMessage(int id) {
        return messageDao.find(id);
    }

    @Transactional(readOnly = true)
    public void createConversation(Conversation conversation) {
        conversationDao.persist(conversation);
    }

    @Transactional(readOnly = true)
    public void createMessage(Message message) {
        messageDao.persist(message);
    }

    @Transactional
    public void sendMessage(Message message) {
        messageDao.persist(message);

        List<Conversation> authorsConversation = findConversations(message.getAuthor());
        Conversation toUpdate = null;

        // pokud existuje konverzace mezi temito 2 uzivateli
        for (Conversation c : authorsConversation) {
            if (c.getUsers().contains(message.getReceiver()) && c.getUsers().size() == 2) {
                toUpdate = c;
                break;
            }
        }

        if (toUpdate == null) {
            toUpdate = new Conversation();
            toUpdate.addMember(message.getReceiver());
            toUpdate.addMember(message.getAuthor());
            toUpdate.addMessage(message);
            conversationDao.persist(toUpdate);
        } else {
            toUpdate.addMessage(message);
            conversationDao.update(toUpdate);
        }
    }

    @Transactional
    public void sendGroupMessage(User author, List<User> receivers, String text) {
        for (User receiver : receivers) {
            Message message = new Message(author, receiver, text);
            messageDao.persist(message);
        }

        List<Conversation> authorsConversation = findConversations(author);
        Conversation toUpdate = null;

        for (Conversation forControl : authorsConversation) {
            if (authorsConversation.containsAll(receivers)) {
                toUpdate.addMessage(new Message(author, receivers.get(0), text));
                conversationDao.update(toUpdate);
                return;
            }
        }

        toUpdate = new Conversation();
        for (User receiver : receivers) {
            toUpdate.addMember(receiver);
        }
        toUpdate.addMember(author);
        toUpdate.addMessage(new Message(author, receivers.get(0), text));
        conversationDao.persist(toUpdate);

    }
}



