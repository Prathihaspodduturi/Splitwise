package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class groupMembersDAOImpl implements groupMembersDAO{

    private EntityManager entityManager;

    public groupMembersDAOImpl(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public void save(GroupMembers members) {
        entityManager.persist(members);
    }

    @Override
    public List<Groups> allGroupsOfUser(String username) {
        List<GroupMembers> members = entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.user.username = :username AND gm.group.deleted = false", GroupMembers.class)
                .setParameter("username", username)
                .getResultList();
        return members.stream().map(GroupMembers::getGroup).collect(Collectors.toList());
    }

    @Override
    public List<User> allUsersInGroup(int groupId) {
        List<GroupMembers> members = entityManager.createQuery(
                        "SELECT gm FROM GroupMembers gm WHERE gm.group.id = :groupId", GroupMembers.class)
                .setParameter("groupId", groupId)
                .getResultList();
        return members.stream().map(GroupMembers::getUser).collect(Collectors.toList());
    }
}
