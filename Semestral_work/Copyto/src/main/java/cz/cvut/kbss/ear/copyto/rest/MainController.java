package cz.cvut.kbss.ear.copyto.rest;


import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @GetMapping
    public String viewMainPage(Principal principal, Model model) {

        if (principal != null) {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            model.addAttribute("name", auth.getPrincipal().getUser().getFirstName());
        }

        return "index";
    }

}
