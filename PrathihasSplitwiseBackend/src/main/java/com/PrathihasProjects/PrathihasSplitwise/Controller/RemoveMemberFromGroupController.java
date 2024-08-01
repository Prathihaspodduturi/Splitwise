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
import java.util.Map;

@RestController
@CrossOrigin
public class RemoveMemberFromGroupController {
    private final GroupMembersDAOImpl groupMembersDAO;
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final UserDAOImpl theUserDAOImpl;

    @Autowired
    public RemoveMemberFromGroupController(GroupMembersDAOImpl groupMembersDAO,
                                           GroupsDAOImpl theGroupsDAOImpl,
                                           UserDAOImpl theUserDAOImpl) {
        this.groupMembersDAO = groupMembersDAO;
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.theUserDAOImpl = theUserDAOImpl;
    }

    @PutMapping("/splitwise/groups/{groupId}/removemember")
    public ResponseEntity<?> removeMember(@PathVariable int groupId, @RequestBody Map<String, String> requestBody, Authentication authentication)
    {
        try
        {
            String memberUsername = requestBody.get("username");
            if (memberUsername == null || memberUsername.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Member username is required");
            }

            String removedByUsername = authentication.getName();

            Groups group = theGroupsDAOImpl.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.badRequest().body("Bad Request");
            }

            User memberUser = theUserDAOImpl.findUserByName(memberUsername);
            if (memberUser == null) {
                return ResponseEntity.badRequest().body("User to be removed not found");
            }

            User removedByUser = theUserDAOImpl.findUserByName(removedByUsername);

            GroupMembers member = groupMembersDAO.getDetails(groupId, memberUsername);
            if (member == null) {
                return ResponseEntity.badRequest().body("User is not part of this group");
            }

            member.setRemovedBy(removedByUser);
            member.setRemovedDate(new Date());

            groupMembersDAO.save(member);

            return ResponseEntity.ok().body("Succesfull");
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: please try again later!");
        }
    }

}
