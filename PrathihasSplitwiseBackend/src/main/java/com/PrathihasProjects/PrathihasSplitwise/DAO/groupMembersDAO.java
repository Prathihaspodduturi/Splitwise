package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;

import java.util.List;

public interface groupMembersDAO {

    void save(GroupMembers members);

    List<Groups> allGroupsOfUser(String username);

    List<User> allUsersInGroup(int id);

}
