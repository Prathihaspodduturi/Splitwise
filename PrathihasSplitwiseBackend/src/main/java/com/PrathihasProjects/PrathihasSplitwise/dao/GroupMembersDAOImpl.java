package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.dto.MemberInfo;
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
        entityManager.persist(members);
    }

    @Override
    public List<Groups> findGroupsOfUser(String username) {
        List<GroupMembers> members = entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.user.username = :username", GroupMembers.class)
                .setParameter("username", username)
                .getResultList();

        return members.stream().map(GroupMembers::getGroup).collect(Collectors.toList());
    }

    /*@Override
    public List<GroupMembers> findMembersByGroupId(int groupId) {
        return entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.group.id = :groupId", GroupMembers.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }*/

    public List<MemberInfo> findMembersByGroupId(int groupId) {
        return entityManager.createQuery(
                        "SELECT new com.PrathihasProjects.PrathihasSplitwise.dto.MemberInfo(gm.user.username, gm.removedBy.username, gm.removedDate) " +
                                "FROM GroupMembers gm WHERE gm.group.id = :groupId and gm.removedBy Is NULL ", MemberInfo.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public boolean isMember(String username, int groupId) {

        try {
            User user = entityManager.createQuery(
                            "SELECT gm.user FROM GroupMembers gm WHERE gm.group.id = :groupId and gm.user.username = :username and gm.removedBy IS NULL", User.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();

            return user != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public User isOldMember(String username, int groupId)
    {
        try {

            return entityManager.createQuery(
                            "SELECT gm.user FROM GroupMembers gm WHERE gm.group.id = :groupId and gm.user.username = :username and gm.removedBy IS NOT NULL", User.class)
                    .setParameter("username", username)
                    .setParameter("groupId", groupId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
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
