package cz.cvut.kbss.ear.copyto.rest;

import cz.cvut.kbss.ear.copyto.exception.NotFoundException;
import cz.cvut.kbss.ear.copyto.model.Conversation;
import cz.cvut.kbss.ear.copyto.model.Message;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/messages")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    // --------------------CREATE--------------------------------------

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CLIENT', 'ROLE_COPYWRITER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createMessage(@RequestBody Message message) {
        messageService.createMessage(message);
        LOG.debug("Sended message {}.", message);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", message.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CLIENT', 'ROLE_COPYWRITER')")
    @PostMapping(value = "/receiver-id/{receiverId}/text/{text}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(Principal principal, @PathVariable Integer receiverId, @PathVariable String text) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        final User author = userService.find(auth.getPrincipal().getUser().getId());
        if(author == null){
            throw NotFoundException.create("User", auth.getPrincipal().getUser().getId());
        }
        final User receiver = userService.find(receiverId);
        if(receiver == null){
            throw NotFoundException.create("User", auth.getPrincipal().getUser().getId());
        }
        Message message = new Message();
        message.setAuthor(author);
        message.setReceiver(receiver);
        message.setText(text);
        messageService.sendMessage(message);
        LOG.debug("Sended message {}.", message);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", message.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CLIENT', 'ROLE_COPYWRITER')")
    @PostMapping(value = "/text/{text}/group-message",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendGroupMessage(Principal principal, @RequestBody ArrayList<Integer> userIds, @PathVariable Integer authorId, @PathVariable String text) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        User author = userService.find(auth.getPrincipal().getUser().getId());
        if(author == null){
            throw NotFoundException.create("User", auth.getPrincipal().getUser().getId());
        }
        List<User> receivers = new ArrayList<>();
        for(Integer id : userIds){
            receivers.add(userService.find(id));
        }
        messageService.sendGroupMessage(author, receivers, text);
        LOG.debug("Sended group message {}.", text);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // --------------------READ--------------------------------------

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getMessage() {
        return messageService.findMessages();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message getById(@PathVariable Integer id) {
        final Message message = messageService.findMessage(id);
        if (message == null) {
            throw NotFoundException.create("Message", id);
        } return message;
    }

    @GetMapping(value = "/id-conversation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getByConversation(Principal principal, @PathVariable Integer id) {
        final Conversation conversation = messageService.findConversation(id);
        if(conversation == null){
            throw NotFoundException.create("Conversation", id);
        }
        final List<Message> messages = messageService.findAllMessagesInConversation(conversation);
        if(messages == null){
            throw NotFoundException.create("Messages for conversation", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(conversation.getUsers().contains(auth.getPrincipal().getUser())) {
            // TODO otestovat jestli tenhle user je spravnej user nebo to musi byt pres id
            return messages;
        } else {
            throw new AccessDeniedException("Cannot access conversation of someone else");
        }
    }

    @GetMapping(value = "/id-author/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getByAuthor(Principal principal, @PathVariable Integer id) {
        final User user = userService.find(id);
        if(user == null){
            throw NotFoundException.create("Author", id);
        }
        final List<Message> messages = messageService.findMessagesByAuthor(user);
        if (messages == null) {
            throw NotFoundException.create("Messages fo author", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().equals(user.getId())) {
            return messages;
        } else {
            throw new AccessDeniedException("Cannot access messages of someone else");
        }
    }

    @GetMapping(value = "/id-receiver/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getByReceiver(Principal principal, @PathVariable Integer id) {
        final User user = userService.find(id);
        if (user == null) {
            throw NotFoundException.create("Receiver", id);
        }
        final List<Message> messages = messageService.findMessagesByReceiver(user);
        if (messages == null) {
            throw NotFoundException.create("Messages for receiver", id);
        }
        final AuthenticationToken auth = (AuthenticationToken) principal;
        if(auth.getPrincipal().getUser().equals(user.getId())) {
            return messages;
        } else {
            throw new AccessDeniedException("Cannot access messages of someone else");
        }
    }

}
