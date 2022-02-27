package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.Version;
import cz.cvut.kbss.ear.copyto.model.Workplace;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class WorkplaceServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WorkplaceService sut;

    @Test
    public void changeWorkplaceStatusChangesStatus() {
        Workplace workplace = Generator.generateWorkplace();

        em.persist(workplace);
        workplace.setEditable(false);
        sut.changeWorkplaceStatus(workplace);

        assertTrue(workplace.isEditable());
    }
}
