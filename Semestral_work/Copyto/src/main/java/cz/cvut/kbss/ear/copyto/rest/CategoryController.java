package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.dto.OrderDTO;
import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.helpers.GetEditor;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.CategoryService;
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
@RequestMapping("/rest/categories")
@PreAuthorize("permitAll()")
public class CategoryController {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final OrderService orderService;

    @Autowired
    public CategoryController(CategoryService categoryService, OrderService orderService){
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    // --------------------CREATE--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        LOG.debug("Created category {}.", category);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/id/{id}", category.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ--------------------------------------

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CLIENT', 'ROLE_COPYWRITER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getCategories() {
        List<Category> categories = categoryService.findCategories();

        if (categories == null) {
            throw NotFoundException.create("Categories", "all");
        }

        final GetEditor editor = new GetEditor();
        editor.setFakeOrders(categories);

        return categories;
    }


    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category getById(@PathVariable Integer id) {
        final Category category = categoryService.findCategory(id);

        if (category == null) {
            throw NotFoundException.create("Category", id);
        }

        final GetEditor editor = new GetEditor();
        editor.setFakeOrders(category);

        return category;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COPYWRITER', 'ROLE_CLIENT')")
    @GetMapping(value = "/id/{id}/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDTO> getOrdersByCategory(@PathVariable Integer id) {
        List<Order> orders = orderService.findOrders(getById(id));
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

    // --------------------UPDATE--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/id/{id}/name/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Integer id, @PathVariable String name) {
        final Category original = categoryService.findCategory(id);
        if (original == null) {
            throw NotFoundException.create("category", id);
        }
        categoryService.updateCategory(original, name);
    }


    @PutMapping(value = "/id/{categoryId}/order/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOrderToCategory(Principal principal, @PathVariable Integer categoryId, @PathVariable Integer orderId) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Category category = getById(categoryId);
        final Order order = orderService.findOrder(orderId);
        final OrderContainer container = orderService.findContainer(order);
        if (order == null || container == null) {
            throw NotFoundException.create("order", orderId);
        }
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                container.getClient().getId().equals(auth.getPrincipal().getUser().getId())) {
            categoryService.addOrder(category, order);
            LOG.debug("Order {} added into category {}.", order, category);
        } else {
            throw new AccessDeniedException("Cannot access order of another client");
        }
    }

    // --------------------DELETE--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')") // todo + ten typek, kterej to vlastni
    @DeleteMapping(value = "/id/{categoryId}/orders/id/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOrderFromCategory(@PathVariable Integer categoryId,
                                        @PathVariable Integer orderId) {
        final Category category = getById(categoryId);
        final Order order = orderService.findOrder(orderId);
        if (order == null) {
            throw NotFoundException.create("Order", orderId);
        }
        categoryService.removeOrder(category, order);
        LOG.debug("Order {} removed from category {}.", order, category);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Integer id) {
        final Category category = categoryService.findCategory(id);
        if (category == null) {
            throw NotFoundException.create("Category", id);
        } categoryService.deleteCategory(category);
        LOG.debug("Category {} was deleted.", category);
    }
}
