package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.Copyto;
import cz.cvut.kbss.ear.copyto.environment.TestConfiguration;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.service.SystemInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ComponentScan(basePackageClasses = Copyto.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class MessageDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private ConversationDao conversationDao;

    @Test
    public void findAllMessagesByUser() {
        Message ExpecteMessage = Generator.generateMessage();
        em.persist(ExpecteMessage);

        User userFrom = ExpecteMessage.getAuthor();
        em.persist(userFrom);
        User userTo = ExpecteMessage.getReceiver();
        em.persist(userTo);

        Message messageFromAuthor = messageDao.findAllMessagesByAuthor(userFrom).get(0);
        Message messageFromReceiver = messageDao.findAllMessagesByReceiver(userTo).get(0);

        assertEquals(ExpecteMessage, messageFromAuthor);
        assertEquals(ExpecteMessage, messageFromReceiver);
    }

    @Test
    public void findAllMessageConversationsByUser() {
        
        User author = Generator.generateClient();
        em.persist(author);
        User receiver1 = Generator.generateCopywriter();
        User receiver2 = Generator.generateCopywriter();
        User receiver3 = Generator.generateCopywriter();
        em.persist(receiver1);
        em.persist(receiver2);
        em.persist(receiver3);
        
        Conversation conversation1 = new Conversation(new ArrayList<>(Arrays.asList(author, receiver1)));
        Conversation conversation2 = new Conversation(new ArrayList<>(Arrays.asList(author, receiver2)));
        Conversation conversation3 = new Conversation(new ArrayList<>(Arrays.asList(author, receiver3)));
        em.persist(conversation1);
        em.persist(conversation2);
        em.persist(conversation3);

        List<Conversation> allConversations = author.getConversations();
        List<Conversation> foundConversations = conversationDao.findAllConversationsByUser(author);

        allConversations.sort(Comparator.comparing(Conversation::getId));
        foundConversations.sort(Comparator.comparing(Conversation::getId));

        assertEquals(allConversations, foundConversations);
    }

    @Test
    public void findAllMessagesInConversation() {

        User author = Generator.generateClient();
        em.persist(author);
        User receiver1 = Generator.generateCopywriter();
        em.persist(receiver1);

        Conversation conversation = new Conversation(new ArrayList<>(Arrays.asList(author, receiver1)));
        em.persist(conversation);

        Message message1 = new Message(author, receiver1, "Hello, author");
        Message message2 = new Message(receiver1, author, "Hello, receiver");
        em.persist(message1);
        em.persist(message2);

       conversation.addMessage(message1);
       conversation.addMessage(message2);

       List<Message> foundMessages = conversationDao.findAllMessagesInContainer(conversation);

        assertEquals(foundMessages, conversation.getMessages());
        assertEquals(2, conversation.getMessages().size());
    }
}

