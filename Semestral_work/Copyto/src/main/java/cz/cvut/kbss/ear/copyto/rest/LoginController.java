package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/";
    }


    @PostMapping(value = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public String loginUser(@RequestBody HashMap<String, String> request) throws Exception {
        loginService.loginUser(request.get("email"), request.get("password"));
        LOG.trace("User {} successfully logged in", request.get("email"));

        return "redirect:/login";
    }
}

