package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class MessageServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MessageService sut;

    @Test
    public void createMessageCreatesNewMessage(){
        Message message = Generator.generateMessage();
        em.persist(message);

        User userFrom = message.getAuthor();
        em.persist(userFrom);
        User userTo = message.getReceiver();
        em.persist(userTo);

        sut.sendMessage(message);

        assertEquals(sut.findMessagesByAuthor(userFrom).get(0).getText(),
                sut.findMessagesByReceiver(userTo).get(0).getText());

    }

    @Test
    public void sendMessageAddMessageToReceiversMessage(){
        User sender = Generator.generateClient();
        User receiver = Generator.generateCopywriter();
        em.persist(sender);
        em.persist(receiver);
        Message message = new Message();
        message.setText(RandomStringUtils.randomAlphabetic(20));
        message.setAuthor(sender);
        message.setReceiver(receiver);
        em.persist(message);
        sut.sendMessage(message);

        Message resultReceiver = sut.findMessagesByReceiver(receiver).get(0);
        assertEquals(resultReceiver.getText(), message.getText());
    }
}
