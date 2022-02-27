package cz.cvut.kbss.ear.copyto;

import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.environment.Environment;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.security.SecurityUtils;
import cz.cvut.kbss.ear.copyto.security.model.UserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilsTest {

    private User user;

    @BeforeEach
    public void setUp() {
        this.user = Generator.generateClient();
        user.setId(Generator.randomInt());
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getCurrentUserReturnsCurrentlyLoggedInUser() {
        Environment.setCurrentUser(user);
        final User result = SecurityUtils.getCurrentUser();
        assertEquals(user, result);
    }

    @Test
    public void getCurrentUserDetailsReturnsUserDetailsOfCurrentlyLoggedInUser() {
        Environment.setCurrentUser(user);
        final UserDetails result = SecurityUtils.getCurrentUserDetails();
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertEquals(user, result.getUser());
    }

    @Test
    public void getCurrentUserDetailsReturnsNullIfNoUserIsLoggedIn() {
        assertNull(SecurityUtils.getCurrentUserDetails());
    }
}
