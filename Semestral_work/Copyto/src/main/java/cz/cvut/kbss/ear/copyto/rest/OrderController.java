package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.dto.OrderDTO;
import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderContainerController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService workplaceService){
        this.orderService = workplaceService;
    }

    // --------------------POST--------------------------------------

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')") // OPRAVNENY
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {
        orderService.addOrder(order);
        LOG.debug("create order {}.", order);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/order/{id}", order.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ--------------------------------------

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COPYWRITER')")
    @GetMapping
    List<OrderDTO> getAllOrder() {
        List<Order> orders = orderService.findOrders();
        if (orders == null) {
            throw NotFoundException.create("orders", 404);
        }
        List<OrderDTO> orderView = new ArrayList<>();
        for (Order order: orders) {
            OrderDTO orderDTO = new OrderDTO();
            for (Category category: order.getCategories()) {
                orderDTO.addCategory(category.getName());
            }
            orderDTO.setDeadline(order.getDeadline());
            orderDTO.setLink(order.getLink());
            orderDTO.setInsertionDate(order.getInsertionDate());
            orderDTO.setPrice(order.getPrice());
            orderDTO.setId(order.getId());
            orderView.add(orderDTO);
        }
        return orderView;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COPYWRITER')")
    @GetMapping(value="/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getAvailableOrders() {
        return orderService.findAvailableOrders();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderContainer getById(@PathVariable Integer id) {
        final OrderContainer container = orderService.findContainer(id);
        if (container == null) {
            throw NotFoundException.create("container", id);
        } return container;
    }

    // --------------------UPDATE--------------------------------------

    // Duplicita s nize uvedenym
    @PutMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(Principal principal, @PathVariable Integer id, @RequestBody Order order) {
        final Order original = orderService.findOrder(id);
        final OrderContainer container = orderService.findContainer(order);

        original.setState(order.getState());
        original.setCategories(order.getCategories());
        original.setDeadline(order.getDeadline());
        original.setLink(order.getLink());
        original.setPrice(order.getPrice());

        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId())){
            orderService.update(original);
        }
    }

    @PutMapping(value = "id-container/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setOrder(Principal principal, @PathVariable Integer id, @RequestBody Order order){
        final OrderContainer container = orderService.findContainer(id);
        Order original = container.getOrder();

        original.setPrice(order.getPrice());
        original.setLink(order.getLink());
        original.setDeadline(order.getDeadline());
        original.setCategories(order.getCategories());
        original.setState(order.getState());

        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId())){
            orderService.update(original);
            LOG.debug("Order {} was setted.", original);
        }
    }


    // --------------------UPDATE--------------------------------------

    @DeleteMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeContainer(Principal principal, @PathVariable Integer id) {
        final Order toRemove = orderService.findOrder(id);
        final OrderContainer container = orderService.findContainer(toRemove);
        if (toRemove == null) {
            return;
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId())) {
            orderService.remove(toRemove);
            LOG.debug("Removed version {}.", toRemove);
        } else {
            throw new AccessDeniedException("Cannot delete someones else order");
        }
    }
}
