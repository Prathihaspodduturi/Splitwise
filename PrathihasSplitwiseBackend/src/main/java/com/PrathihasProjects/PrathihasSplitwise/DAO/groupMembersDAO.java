package com.PrathihasProjects.PrathihasSplitwise.DAO;

import com.PrathihasProjects.PrathihasSplitwise.DTO.MemberInfo;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;

import java.util.List;

public interface groupMembersDAO {

    void save(GroupMembers members);

    List<Groups> findGroupsOfUser(String username);

    List<MemberInfo> findMembersByGroupId(int id);

    boolean isMember(String username, int groupId);

    GroupMembers getDetails(int groupId, String username);

}