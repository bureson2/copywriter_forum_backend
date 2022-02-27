package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.users.Client;
import cz.cvut.kbss.ear.copyto.model.users.Copywriter;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    @Qualifier("clientService")
    private UserService sut;

    @Autowired
    private OrderService orderService;

    @Test
    public void createUserCreatesNewOrderDetail() {
        final User copywriter = Generator.generateCopywriter();
        final User client = Generator.generateClient();
        sut.createAccount(copywriter);
        sut.createAccount(client);

        final Client resultClient = (Client) sut.findByEmail(client.getEmail());
        final Copywriter resultCopywriter = (Copywriter) sut.findByEmail(copywriter.getEmail());

        assertEquals(client, resultClient);
        assertEquals(copywriter, resultCopywriter);
    }

    @Test
    public void updateUserUpdatesUser(){
        final User client = Generator.generateClient();
        sut.createAccount(client);
        String newName = "newName";
        String newSurname = "newSurname";
        String password = "password";
        String newEmail = "newEmail";
        String newNumber = "123456789";

        client.setFirstName(newName);
        client.setSurname(newSurname);
        client.setPassword(password);
        client.setEmail(newEmail);
        client.setMobile(newNumber);

        sut.update(client);
        final Client result = (Client)sut.findByEmail(newEmail);

        assertEquals(result.getFirstName(),newName);
        assertEquals(result.getSurname(),newSurname);
        assertEquals(result.getPassword(),password);
        assertEquals(result.getEmail(),newEmail);
        assertEquals(result.getMobile(),newNumber);
        assertEquals(result, client);
    }
}
