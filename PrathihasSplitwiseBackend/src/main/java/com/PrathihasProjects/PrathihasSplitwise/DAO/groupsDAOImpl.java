package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean deletegroupById(int id) {
        Groups group = entityManager.find(Groups.class, id);

        if(group != null)
        {
            group.setDeleted(true);
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
