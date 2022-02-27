package cz.cvut.kbss.ear.copyto.dao;

import cz.cvut.kbss.ear.copyto.model.Version;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class VersionDao extends BaseDao<Version> {

    protected VersionDao() {
        super(Version.class);
    }

    public List<Version> findByDate(Date date) {
        return em.createNamedQuery("Version.findByDate", Version.class).setParameter("date", date)
                .getResultList();
    }

    public List<Version> findByTitle(String title) {
        return em.createNamedQuery("Version.findByTitle", Version.class).setParameter("title", title)
                .getResultList();
    }
}
