package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param user User data
     */
    @PreAuthorize("(!#user.isAdmin() && anonymous) || hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody User user) {
        userService.createAccount(user);
        LOG.debug("User {} successfully registered.", user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT,', 'ROLE_COPYWRITER', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        return auth.getPrincipal().getUser();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUsers() {
        return userService.find();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getById(@PathVariable Integer id) {
        final User user = userService.find(id);
        if (user == null) {
            throw NotFoundException.create("User", id);
        } return user;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT,', 'ROLE_COPYWRITER', 'ROLE_USER')")
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getByEmail(@PathVariable String email) {
        final User user = userService.findByEmail(email);
        if (user == null) {
            throw NotFoundException.create("User", email);
        } return user;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT,', 'ROLE_COPYWRITER', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/name/{name}/surname/{surname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getByName(@PathVariable String name, @PathVariable String surname) {
        final User user = userService.findByName(name, surname);
        if (user == null) {
            throw NotFoundException.create("User", name + surname);
        } return user;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT,', 'ROLE_COPYWRITER', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/role/{role}/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getByRole(@PathVariable Role role) {
        final List<User> user = userService.findByRole(role);
        if (user == null) {
            throw NotFoundException.create("User", role);
        } return user;
    }

}
