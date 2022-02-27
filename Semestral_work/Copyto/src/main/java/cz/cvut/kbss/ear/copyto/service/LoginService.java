package cz.cvut.kbss.ear.copyto.service;


import cz.cvut.kbss.ear.copyto.security.DefaultAuthenticationProvider;
import cz.cvut.kbss.ear.copyto.security.SecurityUtils;
import cz.cvut.kbss.ear.copyto.security.model.UserDetails;
import cz.cvut.kbss.ear.copyto.service.security.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private DefaultAuthenticationProvider provider;
    private final UserDetailsService userDetailsService;

    @Autowired
    public LoginService(DefaultAuthenticationProvider provider, UserDetailsService userDetailsService) {
        this.provider = provider;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public void loginUser (String email, String password) throws Exception {
        if (SecurityUtils.getCurrentUserDetails() != null) {
            throw new Exception("You are already login.");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        provider.authenticate(authentication);

        final UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(email);
        SecurityUtils.setCurrentUser(userDetails);
        System.out.println(SecurityUtils.getCurrentUser().getRole());
    }
}
