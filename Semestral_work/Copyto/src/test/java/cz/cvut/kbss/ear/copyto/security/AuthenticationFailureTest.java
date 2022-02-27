package cz.cvut.kbss.ear.copyto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.ear.copyto.config.AppConfig;
import cz.cvut.kbss.ear.copyto.security.model.LoginStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Standalone unit test without any Spring
 */
public class AuthenticationFailureTest {

    private final ObjectMapper mapper = new AppConfig().objectMapper();

    private AuthenticationFailure sut;

    @BeforeEach
    public void setUp() {
        this.sut = new AuthenticationFailure(mapper);
    }

    @Test
    public void authenticationFailureReturnsLoginStatusWithErrorInfo() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final String msg = "Username not found";
        final AuthenticationException e = new UsernameNotFoundException(msg);
        sut.onAuthenticationFailure(request, response, e);
        final LoginStatus status = mapper.readValue(response.getContentAsString(), LoginStatus.class);
        assertFalse(status.isSuccess());
        assertFalse(status.isLoggedIn());
        assertNull(status.getUsername());
        assertEquals(msg, status.getErrorMessage());
    }
}
