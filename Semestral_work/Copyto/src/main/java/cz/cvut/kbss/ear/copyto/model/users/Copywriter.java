package cz.cvut.kbss.ear.copyto.model.users;

import cz.cvut.kbss.ear.copyto.enums.Role;
import cz.cvut.kbss.ear.copyto.model.OrderContainer;

import javax.persistence.*;

@Entity
@DiscriminatorValue("COPYWRITER")
@NamedQueries({
        @NamedQuery(name = "Copywriter.findAcceptedOrders", query = "SELECT o FROM OrderContainer o WHERE :copywriter = o.assignee" )
})
public class Copywriter extends User {

    @Basic(optional = false)
    @Column(nullable = true)
    private String intro;

    public Copywriter() {
        this.role = Role.COPYWRITER;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    private void candidateForOrder(OrderContainer order) {
        order.addCandidate(this);
    }

    public String getIntro() {
        return intro;
    }


}
