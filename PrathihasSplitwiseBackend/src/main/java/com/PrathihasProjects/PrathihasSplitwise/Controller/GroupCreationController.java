package com.PrathihasProjects.PrathihasSplitwise.Controller;

import com.PrathihasProjects.PrathihasSplitwise.compositeKey.GroupMembersId;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupMembersDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.GroupsDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dao.UserDAOImpl;
import com.PrathihasProjects.PrathihasSplitwise.dto.GroupDTO;
import com.PrathihasProjects.PrathihasSplitwise.entity.GroupMembers;
import com.PrathihasProjects.PrathihasSplitwise.entity.Groups;
import com.PrathihasProjects.PrathihasSplitwise.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@CrossOrigin
public class GroupCreationController {
    private final GroupMembersDAOImpl groupMembersDAO;
    private final GroupsDAOImpl theGroupsDAOImpl;
    private final UserDAOImpl theUserDAOImpl;

    @Autowired
    public GroupCreationController(GroupMembersDAOImpl groupMembersDAO,
                                   GroupsDAOImpl theGroupsDAOImpl,
                                   UserDAOImpl theUserDAOImpl) {
        this.groupMembersDAO = groupMembersDAO;
        this.theGroupsDAOImpl = theGroupsDAOImpl;
        this.theUserDAOImpl = theUserDAOImpl;
    }

    @PostMapping("/splitwise/creategroup")
    public ResponseEntity<?> createGroup(@RequestBody GroupDTO groupDTO , Authentication authentication)
    {
        try
        {
            String currentUsername = authentication.getName();

            User user = theUserDAOImpl.findUserByName(currentUsername);

            if(user == null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("technical error");
            }

            Groups newGroup = new Groups();
            newGroup.setGroupName(groupDTO.getGroupName());
            newGroup.setSettledUp(false);
            newGroup.setDeleted(false);
            newGroup.setCreatedBy(user);
            newGroup.setGroupDescription(groupDTO.getGroupDescription());
            newGroup.setDateCreated(new Date());

            theGroupsDAOImpl.save(newGroup);

            GroupMembers groupMember = new GroupMembers();

            groupMember.setGroup(newGroup);
            groupMember.setUser(user);
            groupMember.setAddedBy(user);
            groupMember.setAddedDate(new Date());

            groupMembersDAO.save(groupMember);

            return ResponseEntity.ok(newGroup);

        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create group: " + e.getMessage());
        }
    }

}
