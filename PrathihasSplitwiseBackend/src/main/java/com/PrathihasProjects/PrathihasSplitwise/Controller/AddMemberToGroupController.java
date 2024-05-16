package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.GroupMembersId;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.MemberInfo;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class AddMemberToGroupController {

    @Autowired
    private GroupsDAOImpl theGroupsDAOImpl;

    @Autowired
    private UserDAOImpl theUserDAOImpl;

    @Autowired
    private GroupMembersDAOImpl groupMembersDAO;

    @PostMapping("/splitwise/groups/{groupId}/addmember")
    public ResponseEntity<?> addMemberToGroup(@PathVariable int groupId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        try {
            // Validate that the username is provided
            String newUsername = requestBody.get("newUsername");
            if (newUsername == null || newUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }

            String username = authentication.getName();

            User curUser = theUserDAOImpl.findUserByName(username);

            User userToAdd = theUserDAOImpl.findUserByName(newUsername);
            if (userToAdd == null) {
                return ResponseEntity.badRequest().body("User does not exist");
            }

            // Check if group exists
            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if user is already a member of the group
            boolean isAlreadyMember = groupMembersDAO.isMember(newUsername, groupId);
            if (isAlreadyMember) {
                return ResponseEntity.badRequest().body("User is already a member of this group");
            }

            // Create and save the new group member
            GroupMembers newMember = new GroupMembers();
            newMember.setUser(userToAdd);
            newMember.setGroup(group);
            newMember.setId(new GroupMembersId(groupId, newUsername));
            newMember.setAddedBy(curUser);
            newMember.setAddedDate(new Date());
            groupMembersDAO.save(newMember);
            List<MemberInfo> members = groupMembersDAO.findMembersByGroupId(groupId);
            return ResponseEntity.ok().body(members);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: please try again later!");
        }
    }

}
