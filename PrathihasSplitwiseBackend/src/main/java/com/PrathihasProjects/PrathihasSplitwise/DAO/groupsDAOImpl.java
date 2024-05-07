package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Repository
public class groupsDAOImpl implements groupsDAO{

    private EntityManager entityManager;

    public groupsDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public Groups findGroupById(int id)
    {
        Groups group = entityManager.find(Groups.class, id);

        return group;
    }

    @Override
    @Transactional
    public void save(Groups group) {
        entityManager.persist(group);
    }

    @Override
    @Transactional
    public void updateGroupName(Groups group)
    {
        entityManager.merge(group);
    }

    @Override
    @Transactional
    public boolean deletegroupById(int id, String username, Date date) {
        Groups group = entityManager.find(Groups.class, id);

        User user = entityManager.find(User.class, username);
        if(group != null)
        {
            group.setDeleted(true);
            group.setDeletedBy(user);
            group.setDeletedDate(date);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean settlegroupById(int id, String username, Date date) {
        Groups group = entityManager.find(Groups.class, id);

        User user = entityManager.find(User.class, username);
        if(group != null)
        {
            group.setSettledUp(true);
            group.setSettledBy(user);
            group.setSettledDate(date);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean restoreGroup(int id) {
        Groups group = entityManager.find(Groups.class, id);

        if(group != null)
        {
            group.setDeleted(false);
            return true;
        }
        return false;
    }

}
