package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class groupsDAOImpl implements groupsDAO{

    private EntityManager entityManager;

    public groupsDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Groups group) {
        entityManager.persist(group);
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
    public boolean undoDeletionOfGroup(int id) {
        Groups group = entityManager.find(Groups.class, id);

        if(group != null)
        {
            group.setDeleted(false);
            return true;
        }
        return false;
    }


}
