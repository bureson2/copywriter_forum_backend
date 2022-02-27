package cz.cvut.kbss.ear.copyto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.ear.copyto.security.model.LoginStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AuthenticationFailure implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFailure.class);

    private final ObjectMapper mapper;

    @Autowired
    public AuthenticationFailure(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException {
        LOG.debug("Login failed for user {}.", httpServletRequest.getParameter(SecurityConstants.USERNAME_PARAM));
        final LoginStatus status = new LoginStatus(false, false, null, e.getMessage());
        mapper.writeValue(httpServletResponse.getOutputStream(), status);
    }
}
