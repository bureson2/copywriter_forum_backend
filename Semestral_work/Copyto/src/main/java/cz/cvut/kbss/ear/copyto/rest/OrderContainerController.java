package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.helpers.GetEditor;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/containers")
public class OrderContainerController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderContainerController.class);

    private final OrderService orderService;

    @Autowired
    public OrderContainerController(OrderService workplaceService) {
        this.orderService = workplaceService;
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createOrderContainer(Principal principal, @RequestBody OrderContainer container) {
        container.setClient(((AuthenticationToken) principal).getPrincipal().getUser());
        orderService.createContainer(container);
        LOG.debug("create container {}.", container);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/id/{id}", container.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------------READ-------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<OrderContainer> getAllContainers() {
        List<OrderContainer> containers = orderService.findContainers();

        GetEditor editor = new GetEditor();
        editor.setFakeCategories(containers);

        return containers;
    }

    @GetMapping(value = "/id/{id}/candidates", produces = MediaType.APPLICATION_JSON_VALUE)
    List<User> getCandidates(@PathVariable Integer id) {
        OrderContainer container = orderService.findContainer(id);
        return orderService.findCandidates(container);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order getById(@PathVariable Integer id) {
        final Order order = orderService.findOrder(id);
        if (order == null) {
            throw NotFoundException.create("order", id);
        }
        return order;
    }

    // --------------------------DELETE-------------------------------

    @DeleteMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeContainer(Principal principal, @PathVariable Integer id) {
        final OrderContainer toRemove = orderService.findContainer(id);
        if (toRemove == null) {
            return;
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(toRemove.getClient().getId())) {
            orderService.remove(toRemove);
            LOG.debug("Removed version {}.", toRemove);
        } else {
            throw new AccessDeniedException("Cannot delete someones else order");
        }
    }
}
