package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.Workplace;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.OrderService;
import cz.cvut.kbss.ear.copyto.service.WorkplaceService;
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
@RequestMapping("/rest/workplace")
public class WorkplaceController {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(WorkplaceController.class);

    private final WorkplaceService workplaceService;
    private final OrderService orderService;

    @Autowired
    public WorkplaceController(WorkplaceService workplaceService, OrderService orderService){
        this.workplaceService = workplaceService;
        this.orderService = orderService;
    }

    // --------------------CREATE--------------------------------------

    // pro testovani - pro fungovani nepotrebne, vytvari se automaticky
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createWorkplace(@RequestBody Workplace workplace) {
        workplaceService.createWorkplace(workplace);
        LOG.debug("create workplace {}.", workplace);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", workplace.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ--------------------------------------


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Workplace> getWorkplaces() {
        return workplaceService.findWorkplaces();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Workplace getById(@PathVariable Integer id) {
        final Workplace workplace = workplaceService.findWorkplace(id);
        if (workplace == null) {
            throw NotFoundException.create("workplace", id);
        } return workplace;
    }


    // --------------------UPDATE--------------------------------------

    @PutMapping(value = "/id-open/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeWorkplaceStatus(Principal principal, @PathVariable Integer id) {
        final Workplace original = workplaceService.findWorkplace(id);
        if (original == null) {
            throw NotFoundException.create("workplace", id);
        }
        final OrderContainer container = orderService.findContainer(original);
        if (container == null) {
            throw NotFoundException.create("Container", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
        auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId()) ||
        auth.getPrincipal().getUser().getId().equals(container.getClient().getId())){
            workplaceService.changeWorkplaceStatus(original);
            workplaceService.update(original);
        } else {
            throw new AccessDeniedException("Cannot update this workplace.");
        }
    }

    // --------------------DELETE--------------------------------------

    //TODO vlastnik
    @DeleteMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetWorkplace(Principal principal, @PathVariable Integer id){
        final Workplace toReset = workplaceService.findWorkplace(id);
        if (toReset == null) {
            throw NotFoundException.create("workplace", id);
        }
        final OrderContainer container = orderService.findContainer(toReset);
        if (container == null) {
            throw NotFoundException.create("Container", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId())){
            workplaceService.reset(toReset);
            LOG.debug("Reset workplace {}.", toReset);
        } else {
            throw new AccessDeniedException("Cannot update this workplace.");
        }


    }
}
