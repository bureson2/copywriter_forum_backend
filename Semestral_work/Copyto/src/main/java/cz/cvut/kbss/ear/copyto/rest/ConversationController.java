package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.helpers.GetEditor;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.users.User;
import cz.cvut.kbss.ear.copyto.rest.util.RestUtils;
import cz.cvut.kbss.ear.copyto.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.copyto.service.MessageService;
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
@RequestMapping("/rest/conversations")
public class ConversationController {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationController.class);

    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public ConversationController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    // --------------------CREATE--------------------------------------

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CLIENT', 'ROLE_COPYWRITER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createConversation(@RequestBody Conversation conversation) {
        messageService.createConversation(conversation);
        LOG.debug("Created conversation {}.", conversation);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/id/{id}", conversation.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Conversation> getConversation() {
        return messageService.findConversations();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Conversation getById(@PathVariable Integer id) {
        final Conversation conversation = messageService.findConversation(id);
        if (conversation == null) {
            throw NotFoundException.create("Conversation", id);
        }
        return conversation;
    }

    @GetMapping(value = "/id-user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Conversation> getByUser(Principal principal, @PathVariable Integer id) {
        final User user = userService.find(id);
        if (user == null) {
            throw NotFoundException.create("user", id);
        }
        final List<Conversation> conversations = messageService.findConversations(user);
        if (conversations == null) {
            throw NotFoundException.create("Conversation for user", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if (auth.getPrincipal().getUser().getRole() == Role.ADMIN ||
                auth.getPrincipal().getUser().getId().equals(user.getId())) {
            GetEditor editor = new GetEditor();
            editor.setFakeConversation(conversations);
            return conversations;
        } else {
            throw new AccessDeniedException("Cannot access contracts of another user");
        }
    }
}
