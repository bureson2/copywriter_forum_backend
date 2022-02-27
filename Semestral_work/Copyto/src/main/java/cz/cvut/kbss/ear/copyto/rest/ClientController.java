package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.ClientService;
import cz.cvut.kbss.ear.copyto.service.OrderService;
import cz.cvut.kbss.ear.copyto.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/rest/client")
public class ClientController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public ClientController(ClientService clientService, UserService userService, OrderService orderService) {
        this.clientService = clientService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @PutMapping(value = "/id-assignee/{assiggneId}/id-container/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeAssignee(Principal principal, @PathVariable Integer assiggneId, @PathVariable Integer orderId) {
        User copywriter = userService.find(assiggneId);
        if (copywriter == null) {
            throw NotFoundException.create("Copywriter", assiggneId);
        }
        final OrderContainer container = orderService.findContainer(orderId);
        if (container == null) {
            throw NotFoundException.create("Order container", orderId);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                container.getClient().getId().equals(auth.getPrincipal().getUser().getId())) {
            clientService.authorizeAssignee(container, copywriter);
            LOG.debug("Order {} has new assignee {}.", orderId, assiggneId);
        } else {
            throw new AccessDeniedException("Cannot access order of another client");
        }
    }

    @PutMapping(value = "/change/id-assignee/{assiggneId}/id-container/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeAssignee(Principal principal, @PathVariable Integer assiggneId, @PathVariable Integer orderId) {
        final User copywriter = userService.find(assiggneId);
        if (copywriter == null) {
            throw NotFoundException.create("Copywriter", assiggneId);
        }
        OrderContainer container = orderService.findContainer(orderId);
        if (container == null) {
            throw NotFoundException.create("Order container", orderId);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                container.getClient().getId().equals(auth.getPrincipal().getUser().getId())) {
            clientService.changeAssignee(container, copywriter);
            LOG.debug("Order {} has new assignee {}.", orderId, assiggneId);
        } else {
            throw new AccessDeniedException("Cannot access order of another client");
        }
    }

    @PutMapping(value = "/visibility/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeVisibility(Principal principal, @PathVariable Integer id) {
        OrderContainer container = orderService.findContainer(id);
        if (container == null) {
            throw NotFoundException.create("Order container", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                container.getClient().getId().equals(auth.getPrincipal().getUser().getId())) {
            clientService.changeVisibility(container);
            LOG.debug("Order {} change visibility.", id);
        } else {
            throw new AccessDeniedException("Cannot access order of another client");
        }
    }
}
