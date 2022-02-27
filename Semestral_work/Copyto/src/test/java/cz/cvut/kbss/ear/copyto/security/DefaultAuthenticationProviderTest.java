package cz.cvut.kbss.ear.copyto.security;


import cz.cvut.kbss.ear.copyto.dao.Generator;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.security.model.UserDetails;
import cz.cvut.kbss.ear.copyto.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// SpringBootTest starts the whole Spring application context in test mode
@SpringBootTest
// Transactional on class -> each test is run in a transaction which is rolled back after test finishes
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class DefaultAuthenticationProviderTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultAuthenticationProvider provider;

    private final User user = Generator.generateClient();
    private final String rawPassword = user.getPassword();

    @BeforeEach
    public void setUp() {
        userService.createAccount(user);
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    public void successfulAuthenticationSetsSecurityContext() {
        final Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword);
        final SecurityContext context = SecurityContextHolder.getContext();
        assertNull(context.getAuthentication());
        final Authentication result = provider.authenticate(auth);
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertNotNull(SecurityContextHolder.getContext());
        final UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        assertEquals(user.getEmail(), details.getUsername());
        assertTrue(result.isAuthenticated());
    }

    @Test
    public void authenticateThrowsUserNotFoundExceptionForUnknownUsername() {
        final Authentication auth = new UsernamePasswordAuthenticationToken("unknownUsername", rawPassword);
        try {
            assertThrows(UsernameNotFoundException.class, () -> provider.authenticate(auth));
        } finally {
            final SecurityContext context = SecurityContextHolder.getContext();
            assertNull(context.getAuthentication());
        }
    }

    @Test
    public void authenticateThrowsBadCredentialsForInvalidPassword() {
        final Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), "unknownPassword");
        try {
            assertThrows(BadCredentialsException.class, () -> provider.authenticate(auth));
        } finally {
            final SecurityContext context = SecurityContextHolder.getContext();
            assertNull(context.getAuthentication());
        }
    }

    @Test
    public void supportsUsernameAndPasswordAuthentication() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }
}
