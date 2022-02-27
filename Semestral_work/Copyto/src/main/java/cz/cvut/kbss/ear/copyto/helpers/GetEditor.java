package cz.cvut.kbss.ear.copyto.helpers;

import cz.cvut.kbss.ear.copyto.model.*;
import cz.cvut.kbss.ear.copyto.model.users.User;

import java.util.ArrayList;
import java.util.List;

public class GetEditor {

    public List<OrderContainer> setFakeCategories(List<OrderContainer> containers) {
        for (OrderContainer container : containers) {
            List<Category> categories = container.getOrder().getCategories();
            List<Category> fakeCategories = new ArrayList<>();
            for (Category toChange : categories) {
                Category fakeCategory = new Category();
                fakeCategory.setId(toChange.getId());
                fakeCategory.setName(toChange.getName());
                fakeCategories.add(fakeCategory);
            }
            container.getOrder().setCategories(fakeCategories);
        }
        return containers;
    }

    public OrderContainer setFakeCategories(OrderContainer container) {
        List<Category> categories = container.getOrder().getCategories();
        List<Category> fakeCategories = new ArrayList<>();
        for (Category toChange : categories) {
            Category fakeCategory = new Category();
            fakeCategory.setId(toChange.getId());
            fakeCategory.setName(toChange.getName());
            fakeCategories.add(fakeCategory);
        }
        container.getOrder().setCategories(fakeCategories);

        return container;
    }

    public Category setFakeOrders(Category category) {
        List<Order> orders = category.getOrders();
        List<Order> fakeOrders = new ArrayList<>();
        for (Order toChange : orders) {
            Order fakeOrder = new Order();
            fakeOrder.setDeadline(toChange.getDeadline());
            fakeOrder.setLink(toChange.getLink());
            fakeOrder.setState(toChange.getState());
            fakeOrder.setId(toChange.getId());
            fakeOrder.setInsertionDate(toChange.getInsertionDate());
            fakeOrders.add(fakeOrder);
        }
        category.setOrders(fakeOrders);
        return category;
    }

    public List<Category> setFakeOrders(List<Category> categories) {
        for(Category category : categories){
            List<Order> orders = category.getOrders();
            List<Order> fakeOrders = new ArrayList<>();
            for (Order toChange : orders) {
                Order fakeOrder = new Order();
                fakeOrder.setDeadline(toChange.getDeadline());
                fakeOrder.setLink(toChange.getLink());
                fakeOrder.setState(toChange.getState());
                fakeOrder.setId(toChange.getId());
                fakeOrder.setInsertionDate(toChange.getInsertionDate());
                fakeOrders.add(fakeOrder);
            }
            category.setOrders(fakeOrders);
        }
        return categories;
    }

    public List<Conversation> setFakeConversation(List<Conversation> conversations){
        List<Conversation> fakeConversations = new ArrayList<>();
        for(Conversation conversation : conversations){
            Conversation fakeConversation = new Conversation();
            List<User> hiddenUsers = new ArrayList<>();
            for (User user : conversation.getUsers()){
                User hiddenUser = new User();
                hiddenUser.setFirstName(user.getFirstName());
                hiddenUser.setSurname(user.getSurname());
                hiddenUser.setId(user.getId());
                hiddenUser.setRole(user.getRole());
                hiddenUsers.add(hiddenUser);
            }
            conversation.setMembers(hiddenUsers);
            for(Message message : conversation.getMessages()){
                User receiver = new User();
                receiver.setFirstName(message.getReceiver().getFirstName());
                receiver.setSurname(message.getReceiver().getSurname());
                receiver.setId(message.getReceiver().getId());
                receiver.setRole(message.getReceiver().getRole());
                message.setReceiver(receiver);
                User author = new User();
                author.setFirstName(message.getAuthor().getFirstName());
                author.setRole(message.getAuthor().getRole());
                author.setSurname(message.getAuthor().getSurname());
                author.setId(message.getAuthor().getId());
                message.setAuthor(author);
            }
        }
        return fakeConversations;
    }

}
