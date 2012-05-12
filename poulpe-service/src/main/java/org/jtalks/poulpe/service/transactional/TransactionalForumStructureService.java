package org.jtalks.poulpe.service.transactional;

import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.poulpe.model.dao.BranchDao;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.dao.SectionDao;
import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.ForumStructureService;
import org.jtalks.poulpe.service.PropertyLoader;

import java.util.List;

/**
 * @author stanislav bashkirtsev
 * @author Guram Savinov
 */
public class TransactionalForumStructureService implements ForumStructureService {
    private SectionDao sectionDao;
    private BranchDao branchDao;
    private final ComponentDao componentDao;
    private PropertyLoader propertyLoader = new PropertyLoader();

    public TransactionalForumStructureService(SectionDao sectionDao, BranchDao branchDao, ComponentDao componentDao) {
        this.sectionDao = sectionDao;
        this.branchDao = branchDao;
        this.componentDao = componentDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveJcommune(Jcommune jcommune) {
        if (jcommune.getId() == 0) {
            propertyLoader.loadDefaults(jcommune);
        }
        componentDao.saveOrUpdate(jcommune);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Jcommune getJcommune(long id) {
        return (Jcommune) componentDao.getByType(ComponentType.FORUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBranch(PoulpeBranch branch) {
        PoulpeSection section = (PoulpeSection) branch.getSection();
        section.deleteBranch(branch);
        sectionDao.saveOrUpdate(section);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveBranch(PoulpeBranch branch, PoulpeSection toSection) {
        PoulpeSection fromSection = (PoulpeSection) branch.getSection();
        fromSection.deleteBranch(branch);
        toSection.addOrUpdateBranch(branch);
        sectionDao.saveOrUpdate(fromSection);
        sectionDao.saveOrUpdate(toSection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Jcommune deleteSectionWithBranches(PoulpeSection section) {
        sectionDao.delete(section);
        return (Jcommune) componentDao.getByType(ComponentType.FORUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSectionAndMoveBranches(PoulpeSection toRemove, PoulpeSection toReceiveBranches) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PoulpeBranch saveBranch(PoulpeSection inSection, PoulpeBranch notYetSavedBranch) {
        if (notYetSavedBranch.getSection() == null) {
            inSection.addOrUpdateBranch(notYetSavedBranch);
            notYetSavedBranch.setSection(inSection);
            sectionDao.saveOrUpdate(inSection);
        } else {
            moveBranch(notYetSavedBranch, inSection);
        }
        return notYetSavedBranch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBranch(PoulpeBranch branch) {
        branchDao.delete(branch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveBranch(PoulpeBranch branch, PoulpeBranch target) {
        PoulpeSection targetSection = target.getPoulpeSection();
        List<PoulpeBranch> branches = targetSection.getPoulpeBranches();
        int index = branches.indexOf(target);
        removeBranch(branch);
        branches.add(index, branch);
        sectionDao.update(targetSection);
    }
}
