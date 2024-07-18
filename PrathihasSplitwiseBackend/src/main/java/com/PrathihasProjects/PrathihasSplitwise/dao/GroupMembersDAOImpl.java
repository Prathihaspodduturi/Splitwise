package com.PrathihasProjects.PrathihasSplitwise.dao;

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
public class GroupMembersDAOImpl implements GroupMembersDAO {

    private final EntityManager entityManager;

    public GroupMembersDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(GroupMembers members) {
        boolean memberShip = isMember(members.getUser().getUsername(), members.getGroup().getId());
        boolean isOldMembership = isOldMember(members.getUser().getUsername(), members.getGroup().getId());
        if (memberShip || isOldMembership) {
            entityManager.merge(members);
        } else {
            entityManager.persist(members);
        }
    }

    @Override
    public List<Groups> findGroupsOfUser(String username) {
        List<GroupMembers> members = entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.user.username = :username", GroupMembers.class)
                .setParameter("username", username)
                .getResultList();

        return members.stream().map(GroupMembers::getGroup).collect(Collectors.toList());
    }

    public List<String> findMembersByGroupId(int groupId) {
        return entityManager.createQuery(
                        "SELECT gm.user.username FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.addedDate IS NOT NULL AND gm.removedDate IS NULL",
                        String.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public boolean isMember(String username, int groupId) {

        try {
            GroupMembers latestMembership = entityManager.createQuery(
                            "SELECT gm FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.user.username = :username AND removedBy IS NULL", GroupMembers.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();

            return latestMembership != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean isOldMember(String username, int groupId) {

        try {
            GroupMembers latestMembership = entityManager.createQuery(
                            "SELECT gm FROM GroupMembers gm WHERE gm.group.id = :groupId AND gm.user.username = :username AND removedBy IS NOT NULL", GroupMembers.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();

            return latestMembership != null;
        } catch (NoResultException e) {

            return false;
        }
    }

    @Override
    public GroupMembers getDetails(int groupId, String username) {

        try {

            return entityManager.createQuery(
                            "SELECT gm FROM GroupMembers gm WHERE gm.group.id = :groupId and gm.user.username = :username", GroupMembers.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
