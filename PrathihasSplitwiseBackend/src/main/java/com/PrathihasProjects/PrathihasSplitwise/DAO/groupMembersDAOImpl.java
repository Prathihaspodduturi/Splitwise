package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class groupMembersDAOImpl implements groupMembersDAO {

    private EntityManager entityManager;

    public groupMembersDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(GroupMembers members) {
        entityManager.persist(members);
    }

    @Override
    public List<Groups> findGroupsOfUser(String username) {
        List<GroupMembers> members = entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.user.username = :username", GroupMembers.class)
                .setParameter("username", username)
                .getResultList();

        System.out.println("" + members);
        return members.stream().map(GroupMembers::getGroup).collect(Collectors.toList());
    }

    @Override
    public List<User> findMembersByGroupId(int groupId) {
        return entityManager.createQuery(
                        "SELECT gm.user FROM GroupMembers gm WHERE gm.group.id = :groupId", User.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public boolean isMember(String username, int groupId) {

        try {
            User user = entityManager.createQuery(
                            "SELECT gm.user FROM GroupMembers gm WHERE gm.group.id = :groupId and gm.user.username = :username", User.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();

            return user == null;
        } catch (NoResultException e) {
            return false;
        }
    }
}
