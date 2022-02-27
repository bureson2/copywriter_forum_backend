package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.Version;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/versions")
public class VersionController {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(WorkplaceController.class);

    private final WorkplaceService workplaceService;
    private final OrderService orderService;

    @Autowired
    public VersionController(WorkplaceService workplaceService, OrderService orderService) {
        this.workplaceService = workplaceService;
        this.orderService = orderService;
    }

    // --------------------CREATE--------------------------------------

    @PostMapping(value = "workplace-id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createVersion(Principal principal, @PathVariable Integer id, @RequestBody Version version) {
        final Workplace workplace = workplaceService.findWorkplace(id);
        final OrderContainer container = orderService.findContainer(workplace);
        if (workplace == null || container == null) {
            throw NotFoundException.create("Workplace", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            workplaceService.addVersion(version, workplace);
            LOG.debug("created version {}.", version);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", version.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } else {
            throw new AccessDeniedException("Cannot access into this workplace");
        }
    }

    // --------------------READ--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Version getById(@PathVariable Integer id) {
        final Version version = workplaceService.findVersion(id);
        if (version == null) {
            throw NotFoundException.create("Version", id);
        }
        return version;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Version> getVersions() {
        final List<Version> versions = workplaceService.findVersions();
        if (versions == null) {
            throw NotFoundException.create("Versions", 0);
        }
        return versions;
    }

    @GetMapping(value = "/id-workplace/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Version> getWorkplaceVersions(Principal principal, @PathVariable Integer id) {
        final Workplace workplace = workplaceService.findWorkplace(id);
        if (workplace == null) {
            throw NotFoundException.create("Workplace", id);
        }
        final OrderContainer container = orderService.findContainer(workplace);
        if (container == null) {
            throw NotFoundException.create("Order for workplace", id);
        }
        final List<Version> versions = workplaceService.findVersions(workplace);
        if (versions == null) {
            throw NotFoundException.create("Versions for workplace", id);
        }

        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId())) {
            return versions;
        } else {
            throw new AccessDeniedException("Cannot access into this workplace");
        }
    }

    @GetMapping(value = "/title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Version> getByTitle(Principal principal, @PathVariable String title) {
        final List<Version> versions = workplaceService.findVersion(title);
        if (versions == null) {
            throw NotFoundException.create("Versions", title);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        List<Version> toReturn = new ArrayList<>();
        for (Version v : versions) {
            final Workplace workplace = workplaceService.findWorkplace(v);
            final OrderContainer container = orderService.findContainer(workplace);
            if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                    auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                    auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())
            ) {
                toReturn.add(v);
            }
        }
        return toReturn;
    }

    // --------------------UPDATE--------------------------------------

    @PutMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVersion(Principal principal, @PathVariable Integer id, @RequestBody Version version) {
        final Version original = workplaceService.findVersion(id);
        if (original == null) {
            throw NotFoundException.create("version", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Workplace workplace = workplaceService.findWorkplace(original); // TODO tady cekam, ze to mozna nebude fungovat
        final OrderContainer container = orderService.findContainer(workplace);
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            original.setTitle(version.getTitle());
            original.setText(version.getText());
            workplaceService.update(version);
        } else {
            throw new AccessDeniedException("Cannot update this version.");
        }

    }

    @PutMapping(value = "/id-clear/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearVersion(Principal principal, @PathVariable Integer id) {
        final Version original = workplaceService.findVersion(id);
        if (original == null) {
            throw NotFoundException.create("version", id);
        }

        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Workplace workplace = workplaceService.findWorkplace(original); // TODO tady cekam, ze to mozna nebude fungovat
        final OrderContainer container = orderService.findContainer(workplace);
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            workplaceService.clear(original);
            workplaceService.update(original);
        } else {
            throw new AccessDeniedException("Cannot update this version.");
        }
    }

    @PutMapping(value = "/id/{id}/text/{text}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateText(Principal principal, @PathVariable Integer id, @PathVariable String text) {
        final Version original = workplaceService.findVersion(id);
        if (original == null) {
            throw NotFoundException.create("version", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Workplace workplace = workplaceService.findWorkplace(original); // TODO tady cekam, ze to mozna nebude fungovat
        final OrderContainer container = orderService.findContainer(workplace);
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            workplaceService.editText(original, text);
        } else {
            throw new AccessDeniedException("Cannot update this version.");
        }
    }

    @PutMapping(value = "/id/{id}/title/{title}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTitle(Principal principal, @PathVariable Integer id, @PathVariable String title) {
        final Version original = workplaceService.findVersion(id);
        if (original == null) {
            throw NotFoundException.create("version", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Workplace workplace = workplaceService.findWorkplace(original); // TODO tady cekam, ze to mozna nebude fungovat
        final OrderContainer container = orderService.findContainer(workplace);
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            workplaceService.editTitle(original, title);
        } else {
            throw new AccessDeniedException("Cannot update this version.");
        }
    }

    // --------------------DELETE--------------------------------------

    @PreAuthorize("hasRole('ADMIN')") // + todo vlastnik, copywriter
    @DeleteMapping(value = "/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVersion(Principal principal, @PathVariable Integer id) {
        final Version toRemove = workplaceService.findVersion(id);
        if (toRemove == null) {
            throw NotFoundException.create("version", id);
        }

        final AuthenticationToken auth = (AuthenticationToken) principal;
        final Workplace workplace = workplaceService.findWorkplace(toRemove); // TODO tady cekam, ze to mozna nebude fungovat
        final OrderContainer container = orderService.findContainer(workplace);
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            workplaceService.remove(workplace, toRemove);
            LOG.debug("Removed version {}.", toRemove);
        } else {
            throw new AccessDeniedException("Cannot update this version.");
        }
    }
}
