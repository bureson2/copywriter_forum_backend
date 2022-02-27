package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.Category;
import cz.cvut.kbss.ear.copyto.model.Message;
import cz.cvut.kbss.ear.copyto.model.Order;
import cz.cvut.kbss.ear.copyto.model.Workplace;
import cz.cvut.kbss.ear.copyto.model.users.Client;
import cz.cvut.kbss.ear.copyto.model.users.Copywriter;
import cz.cvut.kbss.ear.copyto.model.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    /*============USER GENERATOR===============*/

    public static User generateClient(){

        final User client = new Client();
        client.setFirstName(RandomStringUtils.randomAlphabetic(10));
        client.setSurname(RandomStringUtils.randomAlphabetic(10));
        client.setEmail(RandomStringUtils.randomAlphabetic(6) + "@" + RandomStringUtils.randomAlphabetic(4) + "." + RandomStringUtils.randomAlphabetic(2));
        client.setDateOfBirth(new Date());
        client.setMobile("" + ((RAND.nextInt(7)+1) * 100));
        client.setRole(Role.CLIENT);
        client.setPassword(RandomStringUtils.randomAlphabetic(10));
        return client;

    }

    public static User generateCopywriter(){

        final User copywriter = new Copywriter();
        copywriter.setFirstName(RandomStringUtils.randomAlphabetic(10));
        copywriter.setSurname(RandomStringUtils.randomAlphabetic(10));
        copywriter.setEmail(RandomStringUtils.randomAlphabetic(6) + "@" + RandomStringUtils.randomAlphabetic(4) + "." + RandomStringUtils.randomAlphabetic(2));
        copywriter.setDateOfBirth(new Date());
        copywriter.setMobile("" + ((RAND.nextInt(7)+1) * 100));
        copywriter.setRole(Role.CLIENT);
        copywriter.setPassword(RandomStringUtils.randomAlphabetic(10));
        return copywriter;
    }

    /*============MESSAGE GENERATOR===============*/

    public static Message generateMessage(){
        return new Message(generateClient(),
                           generateCopywriter(),
                           RandomStringUtils.randomAlphabetic(40));
    }
    /*============ORDERS GENERATOR===============*/

    public static Order generateOrder() {
        return new Order(randomInt(), new Date());
    }
    public static List<Order> generateOrders(Category category, TestEntityManager em) {

        final List<Order> inCategory = new ArrayList<>();
        final Category other = Generator.generateCategory("otherCategory");
        em.persistAndFlush(other);

        for (int i = 0; i < 10; i++) {

            final Order order = Generator.generateOrder();
            order.setCategory(other);

            if (Generator.randomBoolean()) {
                order.setCategory(category);
                inCategory.add(order);
            }
            em.persistAndFlush(order);
        }
        return inCategory;
    }

/*    public static OrderContainer generateOrderContainer(EntityManager em){
        final OrderContainer o = new OrderContainer();
        List<Copywriter> candidates = new ArrayList<>();
        Client client = generateClient();
        em.persist(client);
        OrderDetail orderDetail = generateOrder();
        em.persist(orderDetail);
        Workplace workplace = generateWorkplace();
        em.persist(workplace);
        o.setCandidates(candidates);
        o.setClient(client);
        o.setOrderDetail(orderDetail);
        o.setWorkplace(workplace);
        return o;
    }*/

    /*============CATEGORY GENERATOR===============*/

    public static Category generateCategory(String name) {
        final Category cat = new Category();
        cat.setName(name);
        return cat;
    }

    /*============WORKPLACE GENERATOR===============*/

    public static Workplace generateWorkplace(){
        return new Workplace();
    }

//    public static Workplace generatePreparedWorkplace(EntityManager em){
//        final Workplace w = new Workplace();
//        List<Version> versions = new ArrayList<>();
//        for(int i = 0; i < 5; i++){
//            versions.add(generateVersion(em));
//        } w.setVersions(versions);
//        return w;
//    }

    /*============VERSION GENERATOR===============*/

//    public static Version generateVersion(EntityManager em){
//        final Version v = new Version();
//        em.persist(v);
//        v.setTitle(RandomStringUtils.randomAlphabetic(15));
//        v.setText(RandomStringUtils.randomAlphabetic(80));
//        return v;
//    }
}
