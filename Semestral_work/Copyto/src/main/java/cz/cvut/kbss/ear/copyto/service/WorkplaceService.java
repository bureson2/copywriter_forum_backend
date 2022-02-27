package cz.cvut.kbss.ear.copyto.service;

import cz.cvut.kbss.ear.copyto.dao.VersionDao;
import cz.cvut.kbss.ear.copyto.dao.WorkplaceDao;
import cz.cvut.kbss.ear.copyto.model.Version;
import cz.cvut.kbss.ear.copyto.model.Workplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WorkplaceService {

    private final WorkplaceDao workplaceDao;
    private final VersionDao versionDao;

    @Autowired
    public WorkplaceService(WorkplaceDao workplaceDao, VersionDao versionDao){
        this.workplaceDao = workplaceDao;
        this.versionDao = versionDao;
    }

    // --------------------READ--------------------------------------

    @Transactional(readOnly = true)
    public Workplace findWorkplace(Integer id) {
        return workplaceDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Workplace> findWorkplaces() {
        return workplaceDao.findAll();
    }

    @Transactional(readOnly = true)
    public Version findVersion(Integer id) {
        return versionDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Version> findVersions() {
        return versionDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Version> findVersions(Date date) {
        return versionDao.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Version> findVersions(Workplace workplace) {
        return workplaceDao.findWorkplaceVersions(workplace);
    }

    @Transactional(readOnly = true)
    public Workplace findWorkplace(Version version){
        return workplaceDao.findWorkplaceByVersion(version);
    }

    @Transactional(readOnly = true)
    public List<Version> findVersion(String title) {
        return versionDao.findByTitle(title);
    }

    @Transactional
    public void createWorkplace(Workplace workplace){
        workplaceDao.update(workplace);
    }

    @Transactional
    public void addVersion(Version version, Workplace workplace){
        versionDao.persist(version);
        workplace.addVersion(version);
        workplaceDao.update(workplace);
    }

    @Transactional
    public void editTitle(Version version, String title){
        version.setTitle(title);
        versionDao.update(version);
    }

    @Transactional
    public void editText(Version version, String text){
        version.setText(text);
        versionDao.update(version);
    }

    @Transactional
    public void remove(Workplace workplace, Version version){
        workplace.removeVersion(version);
        versionDao.remove(version);
        workplaceDao.update(workplace);
    }

    @Transactional
    public void clear(Version version){
        version.setTitle("");
        version.setText("");
        versionDao.update(version);
    }

    @Transactional // TODO spravna kontrola uctu
    public void reset(Workplace workplace){
        workplace.setVersions(new ArrayList<Version>());
        workplaceDao.update(workplace);
    }

    @Transactional
    public void update(Workplace workplace){
        workplaceDao.update(workplace);
    }

    @Transactional
    public void update(Version version){
        versionDao.update(version);
    }

    @Transactional
    public void changeWorkplaceStatus(Workplace workplace){
        final boolean val = workplace.isEditable();
        workplace.setEditable(!val);
        workplaceDao.update(workplace);
    }

}
