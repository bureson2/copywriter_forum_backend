package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.Contract;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.ContractService;
import cz.cvut.kbss.ear.copyto.service.MessageService;
import cz.cvut.kbss.ear.copyto.service.OrderService;
import cz.cvut.kbss.ear.copyto.service.UserService;
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
@RequestMapping("/rest/contracts")
public class ContractController {

    private static final Logger LOG = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;
    private final UserService userService;
    private final OrderService orderService;
    private final MessageService messageService;

    @Autowired
    public ContractController(ContractService contractService, UserService userService, OrderService orderService, MessageService messageService) {
        this.contractService = contractService;
        this.userService = userService;
        this.orderService = orderService;
        this.messageService = messageService;
    }

    // --------------------CREATE--------------------------------------

    @PostMapping(value = "client-id/{clientId}/copywriter-id/{copywriterId}/order-id/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createContract(@PathVariable Integer clientId, @PathVariable Integer copywriterId, @PathVariable Integer orderId, @RequestBody Contract contract) {
        final User client = userService.find(clientId);
        final User copywriter = userService.find(copywriterId);
        final Order order = orderService.findOrder(orderId);

        if (client == null) {
            throw NotFoundException.create("Client", clientId);
        }
        if (client == null) {
            throw NotFoundException.create("Copywriter", copywriterId);
        }
        if (client == null) {
            throw NotFoundException.create("Order", orderId);
        }

        contract.setClient(client);
        contract.setCopywriter(copywriter);
        contract.setOrder(order);
        contractService.createContract(contract);
        LOG.debug("Created contract {}.", contract);

        Message messageForCopywriter = new Message(client, copywriter, "The contract was prepared, please confirm");
        Message messageForClient = new Message(client, copywriter, "The contract was prepared, please confirm");
        messageService.sendMessage(messageForCopywriter);
        messageService.sendMessage(messageForClient);

        LOG.debug("Informative message for {} were sended.", contract);

        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/id/{id}", contract.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ----------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Contract> getContracts() {
        return contractService.findContracts();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Contract getById(@PathVariable Integer id) {
        final Contract contract = contractService.findContract(id);
        if (contract == null) {
            throw NotFoundException.create("contract", id);
        }
        return contract;
    }

    @GetMapping(value = "/id-client/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Contract> getByClient(Principal principal, @PathVariable Integer id) {
        final User user = userService.find(id);
        if (user == null) {
            throw NotFoundException.create("user", id);
        }
        final List<Contract> contracts = contractService.findContractsByClient(user);
        if (contracts == null) {
            throw NotFoundException.create("contracts for user", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(id)) {
            return contracts;
        } else {
            throw new AccessDeniedException("Cannot access contracts of another user");
        }
    }

    @GetMapping(value = "/id-copywriter/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Contract> getByCopywrite(Principal principal, @PathVariable Integer id) {
        final User user = userService.find(id);
        if (user == null) {
            throw NotFoundException.create("user", id);
        }
        final List<Contract> contracts = contractService.findContractsByCopywriter(user);
        if (contracts == null) {
            throw NotFoundException.create("contracts for user", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(id)) {
            return contracts;
        } else {
            throw new AccessDeniedException("Cannot access contracts of another user");
        }
    }

    @GetMapping(value = "/id-order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Contract> getByOrder(Principal principal, @PathVariable Integer id) {
        final Order order = orderService.findOrder(id);
        final OrderContainer container = orderService.findContainer(order);
        if (order == null || container == null) {
            throw NotFoundException.create("contract for order", id);
        }
        final List<Contract> contracts = contractService.findContract(order);
        if (contracts == null) {
            throw NotFoundException.create("contract for order", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(container.getClient().getId()) ||
                auth.getPrincipal().getUser().getId().equals(container.getAssignee().getId())) {
            return contracts;
        } else {
            throw new AccessDeniedException("Cannot access contracts of another user");
        }
    }
}
