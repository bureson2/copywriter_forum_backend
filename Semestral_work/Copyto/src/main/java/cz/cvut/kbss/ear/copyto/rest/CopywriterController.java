package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.OrderState;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.CopywriterService;
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
@RequestMapping("/rest/copywriter")
public class CopywriterController {

    private static final Logger LOG = LoggerFactory.getLogger(CopywriterController.class);

    private final CopywriterService copywriterService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public CopywriterController(CopywriterService copywriterService, UserService userService, OrderService orderService) {
        this.copywriterService = copywriterService;
        this.userService = userService;
        this.orderService = orderService;
    }


    @PutMapping(value = "/id-container/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeAssignee(Principal principal, @PathVariable Integer id) {
        OrderContainer container = orderService.findContainer(id);
        if (container == null) {
            throw NotFoundException.create("container", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            copywriterService.finishJob(container);
            LOG.debug("Assignee {} completed his job.", id);
        } else {
            throw new AccessDeniedException("Cannot finnish somebodys else job");
        }
    }

    @PutMapping(value = "/user/{userId}/order/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signUpForOrder(Principal principal, @PathVariable Integer orderId, @PathVariable Integer userId) {
        Order order = orderService.findOrder(orderId);
        OrderContainer container = orderService.findContainer(order);
        if (container == null) {
            throw NotFoundException.create("container", orderId);
        }
        User assignee = userService.find(userId);
        if (assignee == null) {
            throw NotFoundException.create("user", userId);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().getId().equals(userId) && container.getOrder().getState() == OrderState.ADDED){
            copywriterService.signUpForOrder(assignee, container.getOrder());
            LOG.debug("Copywriter {}  sign up fo order {}.", userId, orderId);
        }
    }
}
