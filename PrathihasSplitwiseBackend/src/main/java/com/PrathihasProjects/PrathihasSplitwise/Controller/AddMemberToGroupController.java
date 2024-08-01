package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
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
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final UserDAOImpl theUserDAOImpl;
    private final GroupMembersDAOImpl groupMembersDAO;

    @Autowired
    public AddMemberToGroupController(GroupsDAOImpl theGroupsDAOImpl,
                                      UserDAOImpl theUserDAOImpl,
                                      GroupMembersDAOImpl groupMembersDAO) {
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.theUserDAOImpl = theUserDAOImpl;
        this.groupMembersDAO = groupMembersDAO;
    }

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

            boolean oldMember = groupMembersDAO.isOldMember(newUsername, groupId);
            if(oldMember)
            {
                GroupMembers gmTemp = groupMembersDAO.getDetails(groupId, newUsername);
                gmTemp.setAddedBy(curUser);
                gmTemp.setRemovedBy(null);
                gmTemp.setRemovedDate(null);
                gmTemp.setAddedDate(new Date());
                groupMembersDAO.save(gmTemp);
                List<String> members = groupMembersDAO.findMembersByGroupId(groupId);

                return ResponseEntity.ok().body(members);
            }

            // Create and save the new group member
            GroupMembers newMember = new GroupMembers();
            newMember.setUser(userToAdd);
            newMember.setGroup(group);
            newMember.setAddedBy(curUser);
            newMember.setAddedDate(new Date());
            groupMembersDAO.save(newMember);
            List<String> members = groupMembersDAO.findMembersByGroupId(groupId);
            return ResponseEntity.ok().body(members);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: please try again later!");
        }
    }

}
