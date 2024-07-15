package com.PrathihasProjects.PrathihasSplitwise.dao;

import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;

import java.util.List;

public interface GroupMembersDAO {

    void save(GroupMembers members);

    List<Groups> findGroupsOfUser(String username);

    List<String> findMembersByGroupId(int id);

    boolean isMember(String username, int groupId);

    GroupMembers getDetails(int groupId, String username);

    boolean isOldMember(String username, int groupId);
}
