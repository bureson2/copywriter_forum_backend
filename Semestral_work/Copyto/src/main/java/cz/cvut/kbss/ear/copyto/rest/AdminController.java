package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/admin")
public class AdminController{

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        adminService.createCategory(category);
        LOG.debug("Created category {}.", category);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("id/{id}", category.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/block/id-user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(@PathVariable Integer id) {
        adminService.blockUser(id);
        LOG.debug("User with id {} was blocked.", id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/unblock/id-client/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockClient(@PathVariable Integer id) {
        adminService.unblockClient(id);
        LOG.debug("Client with id {} was unblocked.", id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/unblock/id-copywriter/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockCopywriter(@PathVariable Integer id) {
        adminService.unblockCopywriter(id);
        LOG.debug("Copywriter with id {} was unblocked.", id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/id-user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Integer id) {
        adminService.deleteUser(id);
        LOG.debug("Category with id {} was deleted.", id);
    }
}
